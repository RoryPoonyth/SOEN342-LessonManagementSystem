package src.main.java.com.example;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class OfferingCatalog {
    private static OfferingCatalog instance = null;
    private Session session;

    private OfferingCatalog(Session session) {
        this.session = session;
    }

    public static OfferingCatalog getInstance(Session session) {
        if (instance == null) {
            instance = new OfferingCatalog(session);
        }
        return instance;
    }

    public Offering createOffering(OfferingType offeringType, SpecializationType specialization, Location location, int capacity, Timeslot timeslot) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Offering offering = new Offering(offeringType, specialization, location, capacity, timeslot);
            session.save(offering);
            transaction.commit();
            return offering;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Offering not created: " + e.getMessage(), e);
        }
    }

    public List<Offering> getAvailableOfferingsForInstructor(List<String> cities, List<SpecializationType> specializations) {
        return session.createQuery("select o from Offering o join o.location l where l.city in :cities and o.specialization in :specializations and o.instructorId is null", Offering.class)
                .setParameter("cities", cities)
                .setParameter("specializations", specializations)
                .list();
    }

    public List<Offering> getOfferingsByInstructorId(int instructorId) {
        return session.createQuery("from Offering where instructorId = :instructorId", Offering.class)
                .setParameter("instructorId", instructorId)
                .list();
    }

    public List<Offering> getAllOfferings(String city, SpecializationType specialization, OfferingType type, boolean isAdmin) {
        StringBuilder queryStr = new StringBuilder("select o from Offering o join o.location l");
        if (isAdmin) {
            if (city != null) queryStr.append(" where l.city = :city");
            if (specialization != null) queryStr.append(city != null ? " and" : " where").append(" o.specialization = :specialization");
            if (type != null) queryStr.append((city != null || specialization != null) ? " and" : " where").append(" o.type = :type");
        } else {
            queryStr.append(" where o.isAvailable = true");
            if (city != null) queryStr.append(" and l.city = :city");
            if (specialization != null) queryStr.append(" and o.specialization = :specialization");
            if (type != null) queryStr.append(" and o.type = :type");
        }
        var query = session.createQuery(queryStr.toString(), Offering.class);
        if (city != null) query.setParameter("city", city);
        if (specialization != null) query.setParameter("specialization", specialization);
        if (type != null) query.setParameter("type", type);
        return query.list();
    }

    public boolean hasTimeConflict(List<Offering> instructorOfferings, Offering newOffering) {
        for (Offering offering : instructorOfferings) {
            if (offering.getTimeslot().overlapsWith(newOffering.getTimeslot())) {
                return true;
            }
        }
        return false;
    }

    public Offering getOfferingById(int id) {
        Offering offering = session.get(Offering.class, id);
        if (offering == null) {
            throw new IllegalArgumentException("Offering with id '" + id + "' does not exist");
        }
        return offering;
    }

    public Offering cancelOffering(Offering offering) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            offering.cancelOffering();
            session.update(offering);
            transaction.commit();
            return offering;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error canceling offering: " + e.getMessage(), e);
        }
    }

    public List<Offering> getOfferingsWithInstructor() {
        return session.createQuery("from Offering where instructorId is not null", Offering.class).list();
    }

    public List<Offering> getOfferingsWithInstructorBySpecialization(SpecializationType specialization) {
        return session.createQuery("from Offering where specialization = :specialization and instructorId is not null", Offering.class)
                .setParameter("specialization", specialization)
                .list();
    }

    public void assignInstructorToOffering(Instructor instructor, Offering offering) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            offering.setInstructor(instructor);
            instructor.getOfferings().add(offering);
            session.update(offering);
            session.update(instructor);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error assigning instructor to offering: " + e.getMessage(), e);
        }
    }

    public void removeInstructorFromOffering(Instructor instructor, Offering offering) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            offering.setInstructor(null);
            instructor.getOfferings().remove(offering);
            session.update(offering);
            session.update(instructor);

            offering.getBookings().forEach(booking -> {
                if (booking.getMinor() != null) {
                    System.out.println("Removing booking for minor with ID " + booking.getMinor());
                }
                if (booking.getClient() != null) {
                    System.out.println("Removing booking for client with ID " + booking.getClient());
                }
                session.delete(booking);
            });

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error removing instructor from offering: " + e.getMessage(), e);
        }
    }
}
