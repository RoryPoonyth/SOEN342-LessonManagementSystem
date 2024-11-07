package com.Team5.models;

public class Administrator {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private static final Administrator INSTANCE = new Administrator("Admin", "Admin", "admin@gmail.com", "password123");

    private Administrator(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public static Administrator getInstance() {
        return INSTANCE;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
