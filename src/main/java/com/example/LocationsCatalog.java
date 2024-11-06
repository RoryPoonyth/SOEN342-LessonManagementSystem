package src.main.java.com.example;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;

public class LocationsCatalog {
    private static LocationsCatalog instance = null;
    private Session session;

    private LocationsCatalog(Session session) {
        this.session = session;
    }

    public static LocationsCatalog getInstance(Session session) {
        if (instance == null) {
            instance = new LocationsCatalog(session);
        }
        return instance;
    }

    public Location createLocation(String name, String address, int capacity, City city, List<String> spaceTypes) {
        Location existingLocation = session.createQuery("from Location where name = :name and address = :address and city = :city and spaceTypes in :spaceTypes", Location.class)
                .setParameter("name", name)
                .setParameter("address", address)
                .setParameter("city", city)
                .setParameter("spaceTypes", spaceTypes)
                .uniqueResult();

        if (existingLocation != null) {
            throw new IllegalArgumentException("Location '" + name + "' located at " + address + " in " + city.getName() + " already exists");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Location location = new Location(name, address, capacity, city, spaceTypes);
            session.save(location);
            transaction.commit();
            return location;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Location not created: " + e.getMessage());
        }
    }

    public Object getLocation(String city, String name, String address) {
        if (name == null && address == null) {
            return session.createQuery("from Location where city.name = :city", Location.class)
                    .setParameter("city", city)
                    .list();
        } else {
            Location location = session.createQuery("from Location where city.name = :city and name = :name and address = :address", Location.class)
                    .setParameter("city", city)
                    .setParameter("name", name)
                    .setParameter("address", address)
                    .uniqueResult();
            if (location == null) {
                throw new IllegalArgumentException("Location in city '" + city + "' does not exist");
            }
            return location;
        }
    }

    public Location getLocationById(int id) {
        Location location = session.get(Location.class, id);
        if (location == null) {
            throw new IllegalArgumentException("Location with id '" + id + "' does not exist");
        }
        return location;
    }

    public Location deleteLocation(String city, String name, String address, Integer id) {
        Location location;
        if (id != null) {
            location = session.get(Location.class, id);
        } else {
            location = session.createQuery("from Location where city.name = :city and name = :name and address = :address", Location.class)
                    .setParameter("city", city)
                    .setParameter("name", name)
                    .setParameter("address", address)
                    .uniqueResult();
        }

        if (location == null) {
            throw new IllegalArgumentException("Location not found");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(location);
            transaction.commit();
            return location;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error deleting location: " + e.getMessage());
        }
    }

    public boolean addTimeslot(Location location, Timeslot timeslot) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            location.getSchedule().addTimeslot(timeslot);
            session.update(location);
            transaction.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Error adding timeslot: " + e.getMessage());
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return false;
        }
    }

    public List<Location> getAllLocations() {
        return session.createQuery("from Location", Location.class).list();
    }
} 
