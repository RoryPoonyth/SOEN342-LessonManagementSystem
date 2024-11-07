package com.Team5.collections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.Team5.models.Booking;
import com.Team5.models.Lesson;
import com.Team5.core.DatabaseManager;

public class BookingCollection {
    private static final String INSERT_BOOKING_SQL = "INSERT INTO booking (clientId, lessonId, childId) VALUES (?, ?, ?)";
    private static final String SELECT_BOOKING_BY_ID_SQL = "SELECT * FROM booking WHERE id = ?";
    private static final String SELECT_BOOKINGS_BY_CLIENT_ID_SQL = "SELECT * FROM booking WHERE clientId = ?";
    private static final String SELECT_BOOKING_BY_LESSON_ID_SQL = "SELECT * FROM booking WHERE lessonId = ?";
    private static final String DELETE_BOOKING_SQL = "DELETE FROM booking WHERE id = ?";

    public static List<Booking> getBookings() {
        return executeQuery("SELECT * FROM booking", statement -> {}, BookingCollection::extractBookingList);
    }

    public static Booking getById(Integer id) {
        return executeQuery(SELECT_BOOKING_BY_ID_SQL, statement -> statement.setInt(1, id), BookingCollection::extractBooking);
    }

    public static boolean add(Booking booking) {
        return executeUpdate(INSERT_BOOKING_SQL, statement -> {
            statement.setInt(1, booking.getClientId());
            statement.setInt(2, booking.getLessonId());
            if (booking.getChildId() != null) {
                statement.setInt(3, booking.getChildId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                booking.setId(generatedKeys.getInt(1));
            }
        }) > 0;
    }

    public static boolean validateBooking(Integer selectedLessonId) {
        Lesson selectedLesson = LessonCollection.getById(selectedLessonId);
        return selectedLesson != null && selectedLesson.getIsAvailable() && selectedLesson.getAssignedInstructorId() != null && getByLessonId(selectedLesson.getId()) == null;
    }

    public static Integer createBooking(Integer clientId, Integer lessonId, Integer childId) {
        Booking newBooking = new Booking(clientId, lessonId, childId);
        return add(newBooking) ? newBooking.getId() : null;
    }

    public static List<Booking> getByClientId(Integer clientId) {
        return executeQuery(SELECT_BOOKINGS_BY_CLIENT_ID_SQL, statement -> statement.setInt(1, clientId), BookingCollection::extractBookingList);
    }

    public static Booking getByLessonId(Integer lessonId) {
        return executeQuery(SELECT_BOOKING_BY_LESSON_ID_SQL, statement -> statement.setInt(1, lessonId), BookingCollection::extractBooking);
    }

    public static boolean delete(Integer id) {
        return executeUpdate(DELETE_BOOKING_SQL, statement -> statement.setInt(1, id), null) > 0;
    }

    private static List<Booking> extractBookingList(ResultSet resultSet) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        while (resultSet.next()) {
            bookings.add(extractBookingFromResultSet(resultSet));
        }
        return bookings;
    }

    private static Booking extractBooking(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? extractBookingFromResultSet(resultSet) : null;
    }

    private static Booking extractBookingFromResultSet(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking(resultSet.getInt("clientId"), resultSet.getInt("lessonId"), resultSet.getInt("childId"));
        booking.setId(resultSet.getInt("id"));
        return booking;
    }

    private static int executeUpdate(String sql, StatementPreparer preparer, GeneratedKeyHandler keyHandler) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparer.prepare(statement);
            int rowsAffected = statement.executeUpdate();
            if (keyHandler != null) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    keyHandler.handle(generatedKeys);
                }
            }
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static <T> T executeQuery(String sql, StatementPreparer preparer, ResultSetHandler<T> handler) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preparer.prepare(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return handler.handle(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FunctionalInterface
    private interface StatementPreparer {
        void prepare(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    private interface GeneratedKeyHandler {
        void handle(ResultSet generatedKeys) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet resultSet) throws SQLException;
    }
}
