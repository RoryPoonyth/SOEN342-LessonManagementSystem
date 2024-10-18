public class TimeSlot {
    String day;
    String startTime;
    String endTime;
    boolean isAvailable;
    Instructor instructor;
    String type; // private or group

    public TimeSlot(String day, String startTime, String endTime, String type) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
        this.type = type;
    }
}
