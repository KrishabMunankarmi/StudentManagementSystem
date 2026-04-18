package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = { "/chatHistory", "/chatSessions" })
public class ChatHistoryController extends HttpServlet {

    // GET - load messages or sessions
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.setStatus(401);
            response.getWriter().write("[]");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");
        String path   = request.getServletPath();
        response.setContentType("application/json;charset=UTF-8");

        if ("/chatHistory".equals(path)) {
            // Return messages for a specific session
            String sessionIdParam = request.getParameter("session_id");
            if (sessionIdParam == null) { response.getWriter().write("[]"); return; }

            int sessionId = Integer.parseInt(sessionIdParam);

            try (Connection conn = DbConfig.getDbConnection()) {
                // Verify session belongs to this student
                PreparedStatement check = conn.prepareStatement(
                    "SELECT session_id FROM chat_sessions WHERE session_id=? AND student_id=?");
                check.setInt(1, sessionId);
                check.setInt(2, studentId);
                if (!check.executeQuery().next()) { response.getWriter().write("[]"); return; }

                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT sender, message, sent_at FROM chat_messages " +
                    "WHERE session_id=? ORDER BY sent_at ASC");
                stmt.setInt(1, sessionId);
                ResultSet rs = stmt.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append("{")
                        .append("\"sender\":\"").append(rs.getString("sender")).append("\",")
                        .append("\"message\":\"").append(escapeJson(rs.getString("message"))).append("\",")
                        .append("\"sent_at\":\"").append(rs.getTimestamp("sent_at")).append("\"")
                        .append("}");
                    first = false;
                }
                json.append("]");
                response.getWriter().write(json.toString());

            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().write("[]");
            }

        } else {
            // Return all sessions for this student
            try (Connection conn = DbConfig.getDbConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT session_id, title, started_at FROM chat_sessions " +
                    "WHERE student_id=? ORDER BY started_at DESC LIMIT 20");
                stmt.setInt(1, studentId);
                ResultSet rs = stmt.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append("{")
                        .append("\"session_id\":").append(rs.getInt("session_id")).append(",")
                        .append("\"title\":\"").append(escapeJson(rs.getString("title"))).append("\",")
                        .append("\"started_at\":\"").append(rs.getTimestamp("started_at")).append("\"")
                        .append("}");
                    first = false;
                }
                json.append("]");
                response.getWriter().write(json.toString());

            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().write("[]");
            }
        }
    }

    // DELETE - delete a chat session and its messages
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"success\":false}");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");
        String sessionIdParam = request.getParameter("session_id");

        if (sessionIdParam == null) {
            response.getWriter().write("{\"success\":false}");
            return;
        }

        int sessionId = Integer.parseInt(sessionIdParam);
        response.setContentType("application/json;charset=UTF-8");

        try (Connection conn = DbConfig.getDbConnection()) {
            // Verify session belongs to this student before deleting
            PreparedStatement check = conn.prepareStatement(
                "SELECT session_id FROM chat_sessions WHERE session_id=? AND student_id=?");
            check.setInt(1, sessionId);
            check.setInt(2, studentId);
            if (!check.executeQuery().next()) {
                response.getWriter().write("{\"success\":false}");
                return;
            }

            // Delete messages first (foreign key)
            PreparedStatement delMessages = conn.prepareStatement(
                "DELETE FROM chat_messages WHERE session_id=?");
            delMessages.setInt(1, sessionId);
            delMessages.executeUpdate();

            // Delete the session
            PreparedStatement delSession = conn.prepareStatement(
                "DELETE FROM chat_sessions WHERE session_id=?");
            delSession.setInt(1, sessionId);
            delSession.executeUpdate();

            response.getWriter().write("{\"success\":true}");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false}");
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}