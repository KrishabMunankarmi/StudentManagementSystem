package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

/**
 * Serves submitted assignment files securely.
 * Students can only view their own submissions.
 * Admins can view all submissions.
 * URL: /viewsubmission?id=ASSIGNMENT_ID
 */
@WebServlet("/viewsubmission")
public class SubmissionFileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role        = (String) session.getAttribute("role");
        String assignIdStr = request.getParameter("id");

        if (assignIdStr == null) {
            response.sendError(400, "Assignment ID is required.");
            return;
        }

        int assignmentId = Integer.parseInt(assignIdStr);
        boolean isAdmin  = "admin".equals(role);
        Integer studentId = (Integer) session.getAttribute("student_id");

        if (!isAdmin && studentId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        //Fetch submission file from database
        String submissionFile = null;
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt;

            if (isAdmin) {
                //Admin can view any submission
                stmt = conn.prepareStatement(
                    "SELECT submission_file FROM assignments WHERE id = ? AND submitted = TRUE"
                );
                stmt.setInt(1, assignmentId);
            } else {
                //Student can only view their own submission
                stmt = conn.prepareStatement(
                    "SELECT submission_file FROM assignments WHERE id = ? AND student_id = ? AND submitted = TRUE"
                );
                stmt.setInt(1, assignmentId);
                stmt.setInt(2, studentId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                submissionFile = rs.getString("submission_file");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Database error.");
            return;
        }

        if (submissionFile == null) {
            response.sendError(404, "Submission file not found or access denied.");
            return;
        }

        //Serve the file
        String uploadsDir = getServletContext().getRealPath("/") + "submissions";
        File file = new File(uploadsDir + File.separator + submissionFile);

        if (!file.exists()) {
            response.sendError(404, "File not found on server.");
            return;
        }

        //Set content type based on file extension
        String fileName = submissionFile.toLowerCase();
        if (fileName.endsWith(".pdf")) {
            response.setContentType("application/pdf");
        } else if (fileName.endsWith(".docx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else {
            response.setContentType("application/octet-stream");
        }

        //Set filename for download
        String originalName = submissionFile.replaceAll("^s\\d+_a\\d+_\\d+_", "");
        response.setHeader("Content-Disposition", "inline; filename=\"" + originalName + "\"");
        response.setContentLengthLong(file.length());

        //Stream file to response
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}