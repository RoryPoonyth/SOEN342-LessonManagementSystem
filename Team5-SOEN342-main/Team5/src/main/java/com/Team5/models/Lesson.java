package com.Team5.models;

import java.time.LocalTime;
import java.util.Set;

import com.Team5.enums.DayOfWeek;
import com.Team5.enums.LessonType;

public class Lesson {
    private Integer id;
    private LessonType type;
    private String title;
    private Integer locationId;
    private Integer assignedInstructorId;
    private Boolean isAvailable;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<DayOfWeek> schedule;
    private Integer bookingId;

    public Lesson(LessonType type, String title, int locationId,
            LocalTime startTime, LocalTime endTime, Set<DayOfWeek> schedule) {
        this.type = type;
        this.title = title;
        this.locationId = locationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.schedule = schedule;
        this.isAvailable = false;
        this.assignedInstructorId = -1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LessonType getType() {
        return type;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getAssignedInstructorId() {
        return assignedInstructorId;
    }

    public void setAssignedInstructorId(Integer assignedInstructorId) {
        this.assignedInstructorId = assignedInstructorId;

        if (assignedInstructorId == -1) {
            this.isAvailable = false;
        } else {
            this.isAvailable = true;
        }
    }


    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        this.isAvailable = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Set<DayOfWeek> getSchedule() {
        return schedule;
    }

    public void setSchedule(Set<DayOfWeek> schedule) {
        this.schedule = schedule;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
}
