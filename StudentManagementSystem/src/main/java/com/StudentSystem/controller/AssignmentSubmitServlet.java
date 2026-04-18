package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

/**
 * Handles file upload from the chatbot submission flow.
 * Called when student uploads a file in the chat after selecting an assignment.
 */
@WebServlet("/chatsubmit")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class AssignmentSubmitServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"Not logged in.\"}");
            return;
        }

        int studentId    = (int) session.getAttribute("student_id");
        String assignIdStr = request.getParameter("assignment_id");
        Part filePart    = request.getPart("file");

        if (assignIdStr == null || filePart == null || filePart.getSize() == 0) {
            response.getWriter().write("{\"success\":false,\"message\":\"No file uploaded.\"}");
            return;
        }

        int assignmentId = Integer.parseInt(assignIdStr);

        //Save file to submissions folder
        String uploadsDir = getServletContext().getRealPath("/") + "submissions";
        Files.createDirectories(Paths.get(uploadsDir));

        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String savedName    = "s" + studentId + "_a" + assignmentId + "_" + System.currentTimeMillis() + "_" + originalName;
        filePart.write(uploadsDir + File.separator + savedName);

        //Mark assignment as submitted
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE assignments SET submitted=TRUE, submission_file=?, submitted_at=NOW() " +
                "WHERE id=? AND student_id=?"
            );
            stmt.setString(1, savedName);
            stmt.setInt(2, assignmentId);
            stmt.setInt(3, studentId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                //Clear submission state from session
                session.removeAttribute("submit_state");
                session.removeAttribute("submit_assignments");
                session.removeAttribute("submit_selected_id");
                session.removeAttribute("submit_selected_title");

                response.getWriter().write(
                    "{\"success\":true,\"message\":\"✅ Assignment submitted successfully! Well done!\"}"
                );
            } else {
                response.getWriter().write(
                    "{\"success\":false,\"message\":\"Could not submit. Please try again.\"}"
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Database error.\"}");
        }
    }
}