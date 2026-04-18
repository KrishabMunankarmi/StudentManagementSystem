package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/adminstudents")
public class AdminStudentController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminDashboardController.isAdmin(request, response)) return;

        String search = request.getParameter("search");
        String action = request.getParameter("action");

        try (Connection conn = DbConfig.getDbConnection()) {

            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM students WHERE student_id = ?");
                stmt.setInt(1, id);
                stmt.executeUpdate();
                response.sendRedirect(request.getContextPath() + "/adminstudents");
                return;
            }

            //Load students
            String sql = "SELECT student_id, name, email, programme, year_of_study FROM students";
            if (search != null && !search.isEmpty()) {
                sql += " WHERE name LIKE ? OR email LIKE ?";
            }
            sql += " ORDER BY student_id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (search != null && !search.isEmpty()) {
                stmt.setString(1, "%" + search + "%");
                stmt.setString(2, "%" + search + "%");
            }

            ResultSet rs = stmt.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(rs.getInt("student_id")).append(",")
                    .append("\"name\":\"").append(escape(rs.getString("name"))).append("\",")
                    .append("\"email\":\"").append(escape(rs.getString("email"))).append("\",")
                    .append("\"programme\":\"").append(escape(rs.getString("programme") != null ? rs.getString("programme") : "")).append("\",")
                    .append("\"year\":").append(rs.getInt("year_of_study"))
                    .append("}");
                first = false;
            }
            json.append("]");
            request.setAttribute("students", json.toString());
            request.setAttribute("search", search);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("students", "[]");
        }

        request.getRequestDispatcher("/WEB-INF/pages/AdminStudents.jsp").forward(request, response);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}