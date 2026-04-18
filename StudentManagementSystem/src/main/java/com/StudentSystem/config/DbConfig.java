package com.StudentSystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    // Load JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Connect to sms_db
    public static Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/sms_db?serverTimezone=UTC",
            "root",
            ""
        );
    }

    public static void main(String[] args) {
        try (Connection con = getDbConnection()) {
            System.out.println("Connected to sms_db successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}