// Teacher.java
package com.ius.ping.model;

public class TeacherModel {
    private int id; // Primary key
    private String name;
    private String email;

    // Constructors, getters, and setters
    public TeacherModel() {}

    public TeacherModel(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}