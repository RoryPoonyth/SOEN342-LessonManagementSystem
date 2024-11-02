package model;

public abstract class User {
    protected String userID;
    protected String name;
    protected String email;
    protected String password;
    protected String phoneNumber;

    public User(String userID, String name, String email, String password, String phoneNumber) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
    
    public void logout() {
        // Log Out implementation
    }
}
