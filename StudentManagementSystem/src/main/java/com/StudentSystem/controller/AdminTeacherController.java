package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/adminteachers")
public class AdminTeacherController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminDashboardController.isAdmin(request, response)) return;
        loadData(request);
        request.getRequestDispatcher("/WEB-INF/pages/AdminTeacherManager.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminDashboardController.isAdmin(request, response)) return;

        String action = request.getParameter("action");

        try (Connection conn = DbConfig.getDbConnection()) {
            if ("add".equals(action)) {
                String name      = request.getParameter("name");
                String email     = request.getParameter("email");
                String password  = request.getParameter("password");
                int subjectId    = Integer.parseInt(request.getParameter("subject_id"));
                String hashedPw  = PasswordUtil.hashPassword(password);

                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO teachers (name, email, password, subject_id) VALUES (?,?,?,?)"
                );
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, hashedPw);
                stmt.setInt(4, subjectId);
                stmt.executeUpdate();
                request.setAttribute("success", "Teacher added successfully!");

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM teachers WHERE teacher_id=?");
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Operation failed. Email may already be in use.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        loadData(request);
        request.getRequestDispatcher("/WEB-INF/pages/AdminTeacherManager.jsp").forward(request, response);
    }

    private void loadData(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            //Load teachers with subject names
            ResultSet rs = conn.prepareStatement(
                "SELECT t.teacher_id, t.name, t.email, s.subject_name " +
                "FROM teachers t JOIN subjects s ON t.subject_id = s.subject_id " +
                "ORDER BY t.name"
            ).executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"teacher_id\":").append(rs.getInt("teacher_id")).append(",")
                    .append("\"name\":\"").append(escape(rs.getString("name"))).append("\",")
                    .append("\"email\":\"").append(escape(rs.getString("email"))).append("\",")
                    .append("\"subject_name\":\"").append(escape(rs.getString("subject_name"))).append("\"")
                    .append("}");
                first = false;
            }
            json.append("]");
            request.setAttribute("teachers", json.toString());

            //Load subjects for dropdown
            rs = conn.prepareStatement(
                "SELECT subject_id, subject_name FROM subjects ORDER BY subject_name"
            ).executeQuery();
            StringBuilder subjects = new StringBuilder("[");
            first = true;
            while (rs.next()) {
                if (!first) subjects.append(",");
                subjects.append("{\"id\":").append(rs.getInt("subject_id"))
                    .append(",\"name\":\"").append(escape(rs.getString("subject_name"))).append("\"}");
                first = false;
            }
            subjects.append("]");
            request.setAttribute("subjectsList", subjects.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("teachers", "[]");
            request.setAttribute("subjectsList", "[]");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}