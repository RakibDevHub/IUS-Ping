package com.rakibdevhub.iusping.model;

public class StudentModel {

    private int id;
    private String studentId;
    private String name;
    private String department;
    private String phoneNumber;

    // Constructors, getters, and setters
    public StudentModel() {
    }

    public StudentModel(int id, String studentId, String name, String department, String phoneNumber) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
