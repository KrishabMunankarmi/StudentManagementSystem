package com.StudentSystem.service;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.model.DepartmentModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDepartmentService {

    // Fetch all departments
    public List<DepartmentModel> getAllDepartments() {
        List<DepartmentModel> departments = new ArrayList<>();
        String sql = "SELECT * FROM Department";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DepartmentModel d = new DepartmentModel();
                d.setDepartmentID(rs.getInt("DepartmentID"));
                d.setDepartmentName(rs.getString("DepartmentName"));
                departments.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    // Add a new department
    public boolean addDepartment(String deptName) {
        String sql = "INSERT INTO Department (DepartmentName) VALUES (?)";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deptName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get a department by ID
    public DepartmentModel getDepartmentById(int deptID) {
        String sql = "SELECT * FROM Department WHERE DepartmentID=?";
        DepartmentModel department = null;
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    department = new DepartmentModel();
                    department.setDepartmentID(rs.getInt("DepartmentID"));
                    department.setDepartmentName(rs.getString("DepartmentName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return department;
    }

    // Update department details
    public boolean updateDepartment(int deptID, String deptName) {
        String sql = "UPDATE Department SET DepartmentName=? WHERE DepartmentID=?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deptName);
            ps.setInt(2, deptID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete department by ID
    public boolean deleteDepartment(int deptID) {
        String sql = "DELETE FROM Department WHERE DepartmentID=?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Search departments by name keyword
    public List<DepartmentModel> searchDepartmentsByName(String keyword) {
        List<DepartmentModel> departments = new ArrayList<>();
        String sql = "SELECT * FROM Department WHERE DepartmentName LIKE ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentModel d = new DepartmentModel();
                    d.setDepartmentID(rs.getInt("DepartmentID"));
                    d.setDepartmentName(rs.getString("DepartmentName"));
                    departments.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }
}
