package com.StudentSystem.service;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterService {

    public boolean registerUser(HttpServletRequest request, String imagePath) throws Exception {

        String name          = request.getParameter("name");
        String email         = request.getParameter("email");
        String password      = request.getParameter("password");
        String confirmPw     = request.getParameter("re-password");
        String programme     = request.getParameter("programme");
        String yearStr       = request.getParameter("year_of_study");

        //Validate required fields
        if (name == null || name.isBlank() ||
            email == null || email.isBlank() ||
            password == null || password.isBlank() ||
            confirmPw == null || confirmPw.isBlank()) {
            throw new IllegalArgumentException("All required fields must be filled.");
        }

        if (!password.equals(confirmPw)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        //Hash password
        String hashedPassword = PasswordUtil.hashPassword(password);

        //Parse year of study
        Integer yearOfStudy = null;
        if (yearStr != null && !yearStr.isBlank()) {
            try { yearOfStudy = Integer.parseInt(yearStr); } catch (NumberFormatException ignored) {}
        }

        //Insert into students table
        try (Connection conn = DbConfig.getDbConnection()) {
            String sql = "INSERT INTO students (name, email, password, programme, year_of_study) " +
                         "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);
                stmt.setString(4, programme != null ? programme : "");
                if (yearOfStudy != null) {
                    stmt.setInt(5, yearOfStudy);
                } else {
                    stmt.setNull(5, java.sql.Types.INTEGER);
                }
                return stmt.executeUpdate() > 0;
            }
        }
    }
}