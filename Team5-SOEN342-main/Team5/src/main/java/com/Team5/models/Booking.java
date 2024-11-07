package com.Team5.models;

public class Booking {
    private Integer id;
    private Integer clientId;
    private Integer childId;
    private Integer lessonId;

    public Booking(Integer clientId, Integer lessonId, Integer childId) {
        this.clientId = clientId;
        this.lessonId = lessonId;
        this.childId = childId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }
}