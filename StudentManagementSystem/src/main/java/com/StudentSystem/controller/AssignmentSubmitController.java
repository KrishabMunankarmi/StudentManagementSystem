package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

@WebServlet("/submitassignment")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class AssignmentSubmitController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, a.title, s.subject_name, a.due_date " +
                "FROM assignments a JOIN subjects s ON a.subject_id = s.subject_id " +
                "WHERE a.student_id = ? AND a.submitted = FALSE AND a.due_date >= CURDATE() " +
                "ORDER BY a.due_date ASC"
            );
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"title\":\"").append(escape(rs.getString("title"))).append("\",")
                    .append("\"subject\":\"").append(escape(rs.getString("subject_name"))).append("\",")
                    .append("\"due_date\":\"").append(rs.getDate("due_date")).append("\"")
                    .append("}");
                first = false;
            }
            json.append("]");
            request.setAttribute("assignments", json.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("assignments", "[]");
        }

        request.getRequestDispatcher("/WEB-INF/pages/SubmitAssignment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int studentId      = (int) session.getAttribute("student_id");
        String assignmentIdStr = request.getParameter("assignment_id");
        Part filePart      = request.getPart("submission_file");

        if (assignmentIdStr == null || filePart == null || filePart.getSize() == 0) {
            response.sendRedirect(request.getContextPath() + "/assignments?error=true");
            return;
        }

        int assignmentId = Integer.parseInt(assignmentIdStr);

        //Save file to submissions folder
        String uploadsDir = getServletContext().getRealPath("/") + "submissions";
        Files.createDirectories(Paths.get(uploadsDir));

        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String savedName    = "student" + studentId + "_assign" + assignmentId + "_" + System.currentTimeMillis() + "_" + originalName;
        filePart.write(uploadsDir + File.separator + savedName);

        //Mark assignment as submitted in database
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE assignments SET submitted = TRUE, submission_file = ?, submitted_at = NOW() " +
                "WHERE id = ? AND student_id = ?"
            );
            stmt.setString(1, savedName);
            stmt.setInt(2, assignmentId);
            stmt.setInt(3, studentId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                response.sendRedirect(request.getContextPath() + "/assignments?success=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/assignments?error=true");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/assignments?error=true");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}