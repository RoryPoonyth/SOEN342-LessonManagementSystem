package src.main.java.com.example;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import java.util.List;
import java.util.Scanner;

public class DatabaseSetup {
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        // Initialize Hibernate and create session factory
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(Location.class);
            configuration.addAnnotatedClass(SpaceType.class);
            configuration.addAnnotatedClass(Timeslot.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Instructor.class);
            configuration.addAnnotatedClass(Admin.class);
            configuration.addAnnotatedClass(Minor.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            System.err.println("Error creating SessionFactory: " + e.getMessage());
            e.printStackTrace();
        }

        Admin admin = new Admin("admin", "password");
        Session session = getSession();
        session.beginTransaction();
        session.save(admin);
        session.getTransaction().commit();
        admin.adminMenu(session);
    }

    private static void createTables() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Drop all tables if they exist and create them again
            List<String> tableNames = session.createNativeQuery("SELECT tablename FROM pg_tables WHERE schemaname = 'public'").list();
            for (String tableName : tableNames) {
                session.createNativeQuery("DROP TABLE IF EXISTS " + tableName + " CASCADE").executeUpdate();
            }
            transaction.commit();

            // Create tables based on models
            transaction = session.beginTransaction();
            session.createNativeQuery("CREATE TABLE IF NOT EXISTS Location (...)").executeUpdate(); // Specify columns
            session.createNativeQuery("CREATE TABLE IF NOT EXISTS SpaceType (...)").executeUpdate();
            // Add other model tables
            transaction.commit();

            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("An error occurred while creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static Session getSessionInstance() {
        Session session = sessionFactory.openSession();
        try {
            return session;
        } finally {
            session.close();
        }
    }
}
