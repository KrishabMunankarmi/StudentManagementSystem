package com.StudentSystem.service;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.model.StudentSystemModel;

import java.sql.*;

public class ProfileService {

    // Fetch employee details by employee ID
    public StudentSystemModel getEmployeeById(int empId) {
        String sql = "SELECT FullName, Age, ContactNo FROM employee WHERE EmpID = ?";
        StudentSystemModel emp = null;

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            // Execute query and process result
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                emp = new StudentSystemModel();
                emp.setName(rs.getString("FullName"));
                emp.setAge(rs.getInt("Age"));
                emp.setContact(rs.getString("ContactNo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emp;
    }

    // Update employee profile data in the database
    public boolean updateEmployeeProfile(int empId, String fullName, int age, String contact) {
        String sql = "UPDATE employee SET FullName = ?, Age = ?, ContactNo = ? WHERE EmpID = ?";
        boolean updated = false;

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for update statement
            stmt.setString(1, fullName);
            stmt.setInt(2, age);
            stmt.setString(3, contact);
            stmt.setInt(4, empId);

            // Execute update and check if rows affected
            updated = stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updated;
    }
}
