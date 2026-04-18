package com.StudentSystem.model;

public class DepartmentModel {
    private int departmentID;       // Unique identifier for department
    private String departmentName;  // Name of the department

    public DepartmentModel() {}     // Default constructor

    public DepartmentModel(int departmentID, String departmentName) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
