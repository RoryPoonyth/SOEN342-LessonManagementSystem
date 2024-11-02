package model;

import enums.OfferingType;

public class Lesson {
    private String lessonID;
    private String name;
    private OfferingType offeringType;
    private String duration;

    public Lesson(String lessonID, String name, OfferingType offeringType, String duration) {
        this.lessonID = lessonID;
        this.name = name;
        this.offeringType = offeringType;
        this.duration = duration;
    }

    public String getLessonID() { return lessonID; }
    public String getName() { return name; }
    public OfferingType getOfferingType() { return offeringType; }
    public String getDuration() { return duration; }
    public String getDetails() {
        return String.format("Lesson: %s (%s) - Duration: %s", name, offeringType, duration);
    }
}