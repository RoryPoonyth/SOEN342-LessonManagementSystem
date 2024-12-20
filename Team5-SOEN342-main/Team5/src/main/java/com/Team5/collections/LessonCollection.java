package com.Team5.collections;

import com.Team5.enums.DayOfWeek;
import com.Team5.enums.LessonType;
import com.Team5.models.Lesson;
import com.Team5.core.Backend;
import com.Team5.core.DatabaseManager;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class LessonCollection {
    private static final String UPDATE_LESSON_SQL = "UPDATE lesson SET title = ?, type = ?, locationId = ?, startTime = ?, endTime = ?, assignedInstructorId = ?, isAvailable = ? WHERE id = ?";
    private static final String DELETE_SCHEDULE_SQL = "DELETE FROM lesson_schedule WHERE lessonId = ?";
    private static final String INSERT_SCHEDULE_SQL = "INSERT INTO lesson_schedule (lessonId, dayOfWeek) VALUES (?, ?)";
    private static final String INSERT_LESSON_SQL = "INSERT INTO lesson (title, type, locationId, assignedInstructorId, isAvailable, startTime, endTime) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_LESSONS_SQL = "SELECT * FROM lesson";
    private static final String SELECT_LESSON_BY_ID_SQL = "SELECT * FROM lesson WHERE id = ?";
    private static final String SELECT_LESSONS_BY_INSTRUCTOR_ID_SQL = "SELECT * FROM lesson WHERE assignedInstructorId = ?";
    private static final String SELECT_SCHEDULE_BY_LESSON_ID_SQL = "SELECT dayOfWeek FROM lesson_schedule WHERE lessonId = ?";

    public static List<Lesson> getLessons() {
        List<Lesson> lessons = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_LESSONS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Lesson lesson = new Lesson(
                        LessonType.valueOf(resultSet.getString("type")),
                        resultSet.getString("title"),
                        resultSet.getInt("locationId"),
                        LocalTime.parse(resultSet.getString("startTime")),
                        LocalTime.parse(resultSet.getString("endTime")),
                        getScheduleByLessonId(resultSet.getInt("id")));
                lesson.setAssignedInstructorId(resultSet.getInt("assignedInstructorId"));
                lesson.setId(resultSet.getInt("id"));
                lessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    public static Set<DayOfWeek> getScheduleByLessonId(int lessonId) {
        List<DayOfWeek> schedule = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SCHEDULE_BY_LESSON_ID_SQL)) {

            statement.setInt(1, lessonId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    schedule.add(DayOfWeek.valueOf(resultSet.getString("dayOfWeek")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        schedule.sort(Comparator.comparingInt(day -> Arrays.asList(DayOfWeek.values()).indexOf(day)));
        return new LinkedHashSet<>(schedule);
    }

    public static Lesson getById(Integer id) {
        Lesson lesson = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LESSON_BY_ID_SQL)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lesson = new Lesson(
                            LessonType.valueOf(resultSet.getString("type")),
                            resultSet.getString("title"),
                            resultSet.getInt("locationId"),
                            LocalTime.parse(resultSet.getString("startTime")),
                            LocalTime.parse(resultSet.getString("endTime")),
                            getScheduleByLessonId(resultSet.getInt(id)));
                    lesson.setAssignedInstructorId(resultSet.getInt("assignedInstructorId"));
                    lesson.setId(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lesson;
    }

    public static boolean add(Lesson lesson) {
        Connection connection = null;
        PreparedStatement lessonStatement = null;
        PreparedStatement scheduleStatement = null;

        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);

            lessonStatement = connection.prepareStatement(INSERT_LESSON_SQL, Statement.RETURN_GENERATED_KEYS);
            lessonStatement.setString(1, lesson.getTitle());
            lessonStatement.setString(2, lesson.getType().name());
            lessonStatement.setInt(3, lesson.getLocationId());
            lessonStatement.setInt(4, lesson.getAssignedInstructorId());
            lessonStatement.setBoolean(5, lesson.getIsAvailable());
            lessonStatement.setString(6, lesson.getStartTime().toString());
            lessonStatement.setString(7, lesson.getEndTime().toString());

            int rowsInserted = lessonStatement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = lessonStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lesson.setId(generatedKeys.getInt(1));
                    }
                }

                scheduleStatement = connection.prepareStatement(INSERT_SCHEDULE_SQL);
                for (DayOfWeek day : lesson.getSchedule()) {
                    scheduleStatement.setInt(1, lesson.getId());
                    scheduleStatement.setString(2, day.name());
                    scheduleStatement.addBatch();
                }

                scheduleStatement.executeBatch();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (scheduleStatement != null) scheduleStatement.close();
                if (lessonStatement != null) lessonStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<Lesson> getByInstructorId(Integer instructorId) {
        List<Lesson> lessons = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement lessonStatement = connection.prepareStatement(SELECT_LESSONS_BY_INSTRUCTOR_ID_SQL);
             PreparedStatement scheduleStatement = connection.prepareStatement(SELECT_SCHEDULE_BY_LESSON_ID_SQL)) {

            lessonStatement.setInt(1, instructorId);
            try (ResultSet lessonResultSet = lessonStatement.executeQuery()) {
                while (lessonResultSet.next()) {
                    int lessonId = lessonResultSet.getInt("id");

                    scheduleStatement.setInt(1, lessonId);
                    Set<DayOfWeek> schedule = new HashSet<>();
                    try (ResultSet scheduleResultSet = scheduleStatement.executeQuery()) {
                        while (scheduleResultSet.next()) {
                            schedule.add(DayOfWeek.valueOf(scheduleResultSet.getString("dayOfWeek")));
                        }
                    }

                    Lesson lesson = new Lesson(
                            LessonType.valueOf(lessonResultSet.getString("type")),
                            lessonResultSet.getString("title"),
                            lessonResultSet.getInt("locationId"),
                            LocalTime.parse(lessonResultSet.getString("startTime")),
                            LocalTime.parse(lessonResultSet.getString("endTime")),
                            schedule
                    );

                    lesson.setId(lessonId);
                    lessons.add(lesson);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    public static boolean validateLesson(Integer locationId, String startTime, String endTime, String schedule) {
        int startMinutes = Backend.convertTimeToMinutes(startTime);
        int endMinutes = Backend.convertTimeToMinutes(endTime);
        Set<DayOfWeek> requestedDays = Backend.convertScheduleToDays(schedule);

        for (Lesson existingLesson : getLessons()) {
            if (existingLesson.getLocationId() == locationId) {
                Set<DayOfWeek> existingDays = existingLesson.getSchedule();

                if (!requestedDays.isEmpty() && !existingDays.isEmpty() && !Collections.disjoint(requestedDays, existingDays)) {
                    int existingStartMinutes = Backend.convertTimeToMinutes(existingLesson.getStartTime().toString());
                    int existingEndMinutes = Backend.convertTimeToMinutes(existingLesson.getEndTime().toString());

                    boolean isOverlapping = (startMinutes < existingEndMinutes && endMinutes > existingStartMinutes);

                    if (isOverlapping) {
                        System.out.printf("Conflict detected with existing lesson (ID: %d) at location %d on overlapping days.%n", existingLesson.getId(), locationId);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean createLesson(Integer locationId, String title, LessonType lessonType, String startTime,
                                       String endTime, String schedule) {
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            Set<DayOfWeek> dayOfWeeks = Backend.parseSchedule(schedule);

            Lesson lesson = new Lesson(lessonType, title, locationId, start, end, dayOfWeeks);
            lesson.setAssignedInstructorId(-1);
            
            return add(lesson);
        } catch (Exception e) {
            System.out.println("Error creating lesson: " + e.getMessage());
            return false;
        }
    }

    public static List<Lesson> getUnassignedLessons(int locationId) {
        return getLessons().stream()
                .filter(lesson -> lesson.getLocationId() == locationId)
                .filter(lesson -> lesson.getAssignedInstructorId() == -1)
                .filter(lesson -> BookingCollection.getBookings().stream()
                        .noneMatch(booking -> booking.getLessonId() == lesson.getId()))
                .collect(Collectors.toList());
    }

    public static List<Lesson> getAvailableLessons(int locationId) {
        return getLessons().stream()
                .filter(lesson -> lesson.getLocationId() == locationId)
                .filter(lesson -> lesson.getAssignedInstructorId() != -1)
                .filter(lesson -> lesson.getType() == LessonType.GROUP ||
                        (lesson.getType() == LessonType.PRIVATE && lesson.getIsAvailable()))
                .filter(lesson -> BookingCollection.getBookings().stream()
                        .noneMatch(booking -> booking.getLessonId() == lesson.getId()))
                .collect(Collectors.toList());
    }

    public static boolean updateLesson(Lesson lesson) {
        Connection connection = null;
        PreparedStatement lessonStatement = null;
        PreparedStatement deleteScheduleStatement = null;
        PreparedStatement insertScheduleStatement = null;

        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);

            lessonStatement = connection.prepareStatement(UPDATE_LESSON_SQL);
            lessonStatement.setString(1, lesson.getTitle());
            lessonStatement.setString(2, lesson.getType().name());
            lessonStatement.setInt(3, lesson.getLocationId());
            lessonStatement.setString(4, lesson.getStartTime().toString());
            lessonStatement.setString(5, lesson.getEndTime().toString());
            lessonStatement.setInt(6, lesson.getAssignedInstructorId());
            lessonStatement.setBoolean(7, lesson.getIsAvailable());
            lessonStatement.setInt(8, lesson.getId());

            int rowsUpdated = lessonStatement.executeUpdate();

            deleteScheduleStatement = connection.prepareStatement(DELETE_SCHEDULE_SQL);
            deleteScheduleStatement.setInt(1, lesson.getId());
            deleteScheduleStatement.executeUpdate();

            insertScheduleStatement = connection.prepareStatement(INSERT_SCHEDULE_SQL);
            for (DayOfWeek day : lesson.getSchedule()) {
                insertScheduleStatement.setInt(1, lesson.getId());
                insertScheduleStatement.setString(2, day.name());
                insertScheduleStatement.addBatch();
            }
            insertScheduleStatement.executeBatch();

            connection.commit();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (insertScheduleStatement != null) insertScheduleStatement.close();
                if (deleteScheduleStatement != null) deleteScheduleStatement.close();
                if (lessonStatement != null) lessonStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}