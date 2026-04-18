package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/assignments")
public class AssignmentsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        //Pass success/error from redirect params
        if (request.getParameter("success") != null) {
            request.setAttribute("success", "Assignment submitted successfully!");
        }
        if (request.getParameter("error") != null) {
            request.setAttribute("error", "Could not submit assignment. Please try again.");
        }

        loadAssignments(request, studentId);
        request.getRequestDispatcher("/WEB-INF/pages/Assignments.jsp").forward(request, response);
    }

    private void loadAssignments(HttpServletRequest request, int studentId) {
        try (Connection conn = DbConfig.getDbConnection()) {

            //Pending assignments
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, a.title, s.subject_name, a.due_date, " +
                "DATEDIFF(a.due_date, CURDATE()) AS days_left " +
                "FROM assignments a JOIN subjects s ON a.subject_id = s.subject_id " +
                "WHERE a.student_id = ? AND a.submitted = FALSE AND a.due_date >= CURDATE() " +
                "ORDER BY a.due_date ASC"
            );
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder pending = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) pending.append(",");
                pending.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"title\":\"").append(escape(rs.getString("title"))).append("\",")
                    .append("\"subject_name\":\"").append(escape(rs.getString("subject_name"))).append("\",")
                    .append("\"due_date\":\"").append(rs.getDate("due_date")).append("\",")
                    .append("\"days_left\":").append(rs.getInt("days_left"))
                    .append("}");
                first = false;
            }
            pending.append("]");
            request.setAttribute("pendingAssignments", pending.toString());

            //Submitted assignments — include has_file flag
            stmt = conn.prepareStatement(
                "SELECT a.id, a.title, s.subject_name, a.submitted_at, " +
                "(a.submission_file IS NOT NULL AND a.submission_file != '') AS has_file " +
                "FROM assignments a JOIN subjects s ON a.subject_id = s.subject_id " +
                "WHERE a.student_id = ? AND a.submitted = TRUE " +
                "ORDER BY a.submitted_at DESC"
            );
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();

            StringBuilder submitted = new StringBuilder("[");
            first = true;
            while (rs.next()) {
                if (!first) submitted.append(",");
                submitted.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"title\":\"").append(escape(rs.getString("title"))).append("\",")
                    .append("\"subject_name\":\"").append(escape(rs.getString("subject_name"))).append("\",")
                    .append("\"submitted_at\":\"").append(rs.getTimestamp("submitted_at") != null ? rs.getTimestamp("submitted_at").toString().substring(0, 16) : "").append("\",")
                    .append("\"has_file\":").append(rs.getBoolean("has_file"))
                    .append("}");
                first = false;
            }
            submitted.append("]");
            request.setAttribute("submittedAssignments", submitted.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("pendingAssignments", "[]");
            request.setAttribute("submittedAssignments", "[]");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}