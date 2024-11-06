package src.main.java.com.example;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UsersCatalog {
    private static UsersCatalog instance = null;
    private Session session;

    private UsersCatalog(Session session) {
        this.session = session;
    }

    public static UsersCatalog getInstance(Session session) {
        if (instance == null) {
            instance = new UsersCatalog(session);
        }
        return instance;
    }

    public Admin registerAdmin(String name, String password) {
        User existingUser = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();

        if (existingUser != null) {
            throw new IllegalArgumentException("Admin with name '" + name + "' already exists");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Admin admin = new Admin(name, password);
            session.save(admin);
            transaction.commit();
            return admin;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Admin not created: " + e.getMessage());
        }
    }

    public Instructor registerInstructor(String name, String password, String phoneNumber, List<SpecializationType> list, List<String> availableCities) {
        User existingUser = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();

        if (existingUser != null) {
            throw new IllegalArgumentException("Instructor with name '" + name + "' already exists");
        }

        Instructor existingInstructor = session.createQuery("from Instructor where phoneNumber = :phoneNumber", Instructor.class)
                .setParameter("phoneNumber", phoneNumber)
                .uniqueResult();

        if (existingInstructor != null) {
            throw new IllegalArgumentException("Instructor with phone number '" + phoneNumber + "' already exists");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Instructor instructor = new Instructor(name, password, phoneNumber, list, availableCities);
            session.save(instructor);
            transaction.commit();
            return instructor;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Instructor not created: " + e.getMessage());
        }
    }

    public Client registerClient(String name, String phoneNumber, String password, boolean isLegalGuardian) {
        User existingUser = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();

        if (existingUser != null) {
            throw new IllegalArgumentException("User with name '" + name + "' already exists");
        }

        Client existingClient = session.createQuery("from Client where phoneNumber = :phoneNumber", Client.class)
                .setParameter("phoneNumber", phoneNumber)
                .uniqueResult();

        if (existingClient != null) {
            throw new IllegalArgumentException("User with phone number '" + phoneNumber + "' already exists");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Client client = new Client(name, phoneNumber, password, isLegalGuardian);
            session.save(client);
            transaction.commit();
            return client;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Client not created: " + e.getMessage());
        }
    }

    public User login(String name, String password, String phoneNumber) {
        User user = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();

        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user instanceof Client && phoneNumber != null) {
            Client client = session.createQuery("from Client where id = :userId and phoneNumber = :phoneNumber", Client.class)
                    .setParameter("userId", user.getId())
                    .setParameter("phoneNumber", phoneNumber)
                    .uniqueResult();
            return client;
        } else if (user instanceof Instructor && phoneNumber != null) {
            Instructor instructor = session.createQuery("from Instructor where id = :userId and phoneNumber = :phoneNumber", Instructor.class)
                    .setParameter("userId", user.getId())
                    .setParameter("phoneNumber", phoneNumber)
                    .uniqueResult();
            return instructor;
        } else if (user instanceof Admin) {
            return user;
        }
        return null;
    }

    public boolean hasAdmin() {
        return session.createQuery("from Admin", Admin.class).uniqueResult() != null;
    }

    public User deleteUser(String name, Integer id) {
        User user = null;
        if (id != null) {
            user = session.get(User.class, id);
        } else if (name != null) {
            user = session.createQuery("from User where name = :name", User.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user instanceof Admin) {
            throw new IllegalArgumentException("Cannot delete admin");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error deleting user: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return session.createQuery("from User", User.class).list();
    }

    public User getUser(String name) {
        User user = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();
        if (user == null) {
            throw new IllegalArgumentException("User with name '" + name + "' does not exist");
        }
        return user;
    }

    public User getUserById(int id) {
        User user = session.get(User.class, id);
        if (user == null) {
            throw new IllegalArgumentException("User with id '" + id + "' does not exist");
        }
        return user;
    }

    public Minor createAndAddMinor(Client guardian, String name, int age) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Minor minor = new Minor(guardian, name, age);
            session.save(minor);
            transaction.commit();
            return minor;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Error creating minor: " + e.getMessage());
        }
    }

    public Client getClientById(int clientId) {
        return session.get(Client.class, clientId);
    }

    public void updateInstructor(Instructor instructor) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(instructor);
            transaction.commit();
            System.out.println("Instructor " + instructor.getName() + " has been updated successfully.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new IllegalArgumentException("Failed to update instructor: " + e.getMessage());
        }
    }
}