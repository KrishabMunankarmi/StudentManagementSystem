package com.StudentSystem.service;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

    /**
     * Verifies student login credentials against the students table in sms_db.
     *
     * @param email    the student's email address
     * @param password the plain text password entered by the student
     * @return the student's data as an array [student_id, name] if login succeeds,
     *         null if credentials are wrong or a DB error occurs
     */
    public Object[] loginStudent(String email, String password) {
        String sql = "SELECT student_id, name, password FROM students WHERE email = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    // Return student_id and name so the controller can set them in session
                    return new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("name")
                    };
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // login failed
    }
}