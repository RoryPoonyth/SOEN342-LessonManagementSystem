package src.main.java.com.example;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

public class BookingsCatalog {
    private static BookingsCatalog instance = null;
    private Session session;

    private BookingsCatalog(Session session) {
        this.session = session;
    }

    public static BookingsCatalog getInstance(Session session) {
        if (instance == null) {
            instance = new BookingsCatalog(session);
        }
        return instance;
    }

    public Bookings getBooking(int bookingId) {
        return session.get(Bookings.class, bookingId);
    }

    public Bookings createBooking(Client client, Offering offering, Integer minorId) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Bookings newBooking = new Bookings(client, "Booked", "true", false, offering);
            session.save(newBooking);

            offering.addBooking(newBooking);
            client.getBookings().add(newBooking);

            if (minorId != null) {
                Minor minor = session.get(Minor.class, minorId);
                if (minor != null) {
                    minor.getBookings().add(newBooking);
                }
            }

            transaction.commit();
            System.out.println("\nBooking for offering " + offering.getId() + " successfully created!");
            return newBooking;

        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            System.out.println("An error occurred while trying to create the booking.");
            return null;
        }
    }

    public void cancelBooking(Client client, Bookings booking) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            client.getBookings().remove(booking);

            if (booking.getMinor() != null) {
                Minor minor = session.get(Minor.class, booking.getMinor().getId());
                if (minor != null) {
                    minor.getBookings().remove(booking);
                }
            }

            booking.getOffering().getBookings().remove(booking);
            session.delete(booking);

            transaction.commit();
            System.out.println("Booking " + booking.getId() + " has been successfully canceled.");

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("An error occurred while trying to cancel the booking: " + e.getMessage());
        }
    }

    public List<Bookings> getClientBookings(Client client) {
        return session.createQuery("from Bookings where client.id = :clientId", Bookings.class)
                .setParameter("clientId", client.getId())
                .list();
    }

    public List<Bookings> getMinorBookings(int minorId) {
        return session.createQuery("from Bookings where minorId = :minorId", Bookings.class)
                .setParameter("minorId", minorId)
                .list();
    }
}
