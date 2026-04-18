package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

@WebServlet("/chatbot")
public class ChatController extends HttpServlet {

    //GET: Show chatbot page with chat history
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
            String sql = "SELECT session_id, title, started_at FROM chat_sessions " +
                         "WHERE student_id = ? ORDER BY started_at DESC LIMIT 20";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sessionsJson = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) sessionsJson.append(",");
                sessionsJson.append("{")
                    .append("\"session_id\":").append(rs.getInt("session_id")).append(",")
                    .append("\"title\":\"").append(escapeJson(rs.getString("title"))).append("\",")
                    .append("\"started_at\":\"").append(rs.getTimestamp("started_at")).append("\"")
                    .append("}");
                first = false;
            }
            sessionsJson.append("]");
            request.setAttribute("chatSessions", sessionsJson.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("chatSessions", "[]");
        }

        request.getRequestDispatcher("/WEB-INF/pages/Chatbot.jsp").forward(request, response);
    }

    //POST: Handle chat message
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int studentId = 1;
        if (session != null && session.getAttribute("student_id") != null) {
            studentId = (int) session.getAttribute("student_id");
        }

        String message        = request.getParameter("message");
        String sessionIdParam = request.getParameter("session_id");

        if (message == null || message.trim().isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"reply\":\"Please type a message.\",\"session_id\":-1}");
            return;
        }

        message = message.trim();

        //Check if we are in assignment submission flow
        Integer submitState = (Integer) session.getAttribute("submit_state");
        if (submitState != null) {
            String stateReply = handleSubmitState(session, message, studentId, request.getContextPath());
            if (stateReply != null) {
                int chatSessionId = saveAndReturn(session, studentId, sessionIdParam, message, stateReply);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    "{\"reply\":\"" + escapeJson(stateReply) + "\",\"session_id\":" + chatSessionId +
                    ",\"submit_state\":" + (session.getAttribute("submit_state") != null ? session.getAttribute("submit_state") : "null") +
                    ",\"submit_assignment_id\":" + (session.getAttribute("submit_selected_id") != null ? session.getAttribute("submit_selected_id") : "null") +
                    "}"
                );
                return;
            }
        }

        //Normal chat flow - forward to Flask
        int chatSessionId = -1;
        try (Connection conn = DbConfig.getDbConnection()) {
            if (sessionIdParam != null && !sessionIdParam.isEmpty()) {
                chatSessionId = Integer.parseInt(sessionIdParam);
            } else {
                String title = message.length() > 50 ? message.substring(0, 50) + "..." : message;
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO chat_sessions (student_id, title) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, studentId);
                stmt.setString(2, title);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) chatSessionId = keys.getInt(1);
            }
            saveMessage(conn, chatSessionId, "user", message);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String botReply = callFlask(message, studentId);

        //Check if Flask returned a submit_assignment intent
        //If so, start the submission flow
        if (botReply.contains("Submission Page") || botReply.contains("submitassignment")) {
            botReply = startSubmitFlow(session, studentId, botReply);
        }

        if (chatSessionId != -1) {
            try (Connection conn = DbConfig.getDbConnection()) {
                saveMessage(conn, chatSessionId, "bot", botReply);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int submitAssignmentId = session.getAttribute("submit_selected_id") != null ?
            (int) session.getAttribute("submit_selected_id") : -1;

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            "{\"reply\":\"" + escapeJson(botReply) + "\",\"session_id\":" + chatSessionId +
            ",\"submit_state\":" + (session.getAttribute("submit_state") != null ? session.getAttribute("submit_state") : "null") +
            ",\"submit_assignment_id\":" + (submitAssignmentId > 0 ? submitAssignmentId : "null") +
            "}"
        );
    }

    //Handle assignment submission conversation states
    private String handleSubmitState(HttpSession session, String message, int studentId, String contextPath) {
        int state = (Integer) session.getAttribute("submit_state");

        if (state == 1) {
            //Student should type a number to select assignment
            try {
                int choice = Integer.parseInt(message.trim());
                @SuppressWarnings("unchecked")
                java.util.List<int[]> assignments = (java.util.List<int[]>) session.getAttribute("submit_assignments");

                if (assignments == null || choice < 1 || choice > assignments.size()) {
                    return "Please enter a valid number between 1 and " + (assignments != null ? assignments.size() : 1) + ".";
                }

                int[] selected = assignments.get(choice - 1);
                session.setAttribute("submit_selected_id", selected[0]);
                session.setAttribute("submit_selected_title", selected[1]);
                session.setAttribute("submit_state", 2);

                return "📎 You selected: **" + selected[1] + "**\n\n" +
                       "Please upload your submission file using the upload button below. " +
                       "Only PDF and DOCX files are accepted.\n\n" +
                       "__SHOW_UPLOAD_BUTTON__";

            } catch (NumberFormatException e) {
                //Not a number - check if they want to cancel
                if (message.toLowerCase().contains("cancel")) {
                    session.removeAttribute("submit_state");
                    session.removeAttribute("submit_assignments");
                    return "Submission cancelled. How else can I help you?";
                }
                return "Please type the **number** of the assignment you want to submit, or type 'cancel' to go back.";
            }
        }

        if (state == 2) {
            //Waiting for file upload - remind them
            if (message.toLowerCase().contains("cancel")) {
                session.removeAttribute("submit_state");
                session.removeAttribute("submit_assignments");
                session.removeAttribute("submit_selected_id");
                session.removeAttribute("submit_selected_title");
                return "Submission cancelled. How else can I help you?";
            }
            return "Please upload your file using the upload button below, or type 'cancel' to go back.";
        }

        //Unknown state - reset
        session.removeAttribute("submit_state");
        return null;
    }

    //Start the assignment submission flow
    private String startSubmitFlow(HttpSession session, int studentId, String originalReply) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, a.title, s.subject_name, a.due_date " +
                "FROM assignments a JOIN subjects s ON a.subject_id = s.subject_id " +
                "WHERE a.student_id = ? AND a.submitted = FALSE AND a.due_date >= CURDATE() " +
                "ORDER BY a.due_date ASC"
            );
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            java.util.List<int[]> assignmentIds = new java.util.ArrayList<>();
            StringBuilder reply = new StringBuilder("📌 **Your Pending Assignments:**\n\n");
            int i = 1;

            while (rs.next()) {
                int id    = rs.getInt("id");
                String title   = rs.getString("title");
                String subject = rs.getString("subject_name");
                String due     = rs.getDate("due_date").toString();

                assignmentIds.add(new int[]{id, 0});
                //Store title alongside
                session.setAttribute("submit_title_" + id, title);

                reply.append(i).append(". **").append(title).append("** (").append(subject).append(")")
                     .append(" — due ").append(due).append("\n");
                i++;
            }

            if (assignmentIds.isEmpty()) {
                return "🎉 You have no pending assignments to submit!";
            }

            //Store list in session and set state
            //Re-fetch with titles properly
            java.util.List<int[]> properList = new java.util.ArrayList<>();
            stmt = conn.prepareStatement(
                "SELECT a.id, a.title FROM assignments a " +
                "WHERE a.student_id = ? AND a.submitted = FALSE AND a.due_date >= CURDATE() " +
                "ORDER BY a.due_date ASC"
            );
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                properList.add(new int[]{rs.getInt("id"), 0});
                session.setAttribute("submit_title_" + rs.getInt("id"), rs.getString("title"));
            }

            session.setAttribute("submit_assignments", properList);
            session.setAttribute("submit_state", 1);

            reply.append("\nType the **number** of the assignment you want to submit:");
            return reply.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return originalReply;
        }
    }

    //Call Flask for normal chat
    private String callFlask(String message, int studentId) {
        try {
            URL url = new URL("http://localhost:5000/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"message\":\"" + escapeJson(message) + "\",\"student_id\":" + studentId + "}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("UTF-8"));
            }

            int status = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                status == 200 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) output.append(line);
            br.close();
            conn.disconnect();

            return extractJsonString(output.toString(), "response");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Could not contact AI server.";
        }
    }

    //Save message and get/create session
    private int saveAndReturn(HttpSession session, int studentId, String sessionIdParam,
                               String userMessage, String botReply) {
        int chatSessionId = -1;
        try (Connection conn = DbConfig.getDbConnection()) {
            if (sessionIdParam != null && !sessionIdParam.isEmpty()) {
                chatSessionId = Integer.parseInt(sessionIdParam);
            } else {
                String title = userMessage.length() > 50 ? userMessage.substring(0, 50) + "..." : userMessage;
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO chat_sessions (student_id, title) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, studentId);
                stmt.setString(2, title);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) chatSessionId = keys.getInt(1);
            }
            saveMessage(conn, chatSessionId, "user", userMessage);
            saveMessage(conn, chatSessionId, "bot", botReply);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatSessionId;
    }

    private void saveMessage(Connection conn, int sessionId, String sender, String message)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO chat_messages (session_id, sender, message) VALUES (?, ?, ?)");
        stmt.setInt(1, sessionId);
        stmt.setString(2, sender);
        stmt.setString(3, message);
        stmt.executeUpdate();
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private String extractJsonString(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) return "Error: Invalid response from AI server";
        int valueStart = json.indexOf("\"", keyIndex + key.length() + 2);
        if (valueStart == -1) return "Error: Invalid response from AI server";
        valueStart++;
        StringBuilder sb = new StringBuilder();
        int i = valueStart;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':  sb.append('"');  i += 2; continue;
                    case '\\': sb.append('\\'); i += 2; continue;
                    case 'n':  sb.append('\n'); i += 2; continue;
                    case 'r':  sb.append('\r'); i += 2; continue;
                    case 't':  sb.append('\t'); i += 2; continue;
                    case 'u':
                        if (i + 5 < json.length()) {
                            String hex = json.substring(i + 2, i + 6);
                            try {
                                int cp = Integer.parseInt(hex, 16);
                                if (Character.isHighSurrogate((char) cp) && i + 11 < json.length()
                                        && json.charAt(i + 6) == '\\' && json.charAt(i + 7) == 'u') {
                                    int cp2 = Integer.parseInt(json.substring(i + 8, i + 12), 16);
                                    sb.appendCodePoint(Character.toCodePoint((char) cp, (char) cp2));
                                    i += 12;
                                } else { sb.appendCodePoint(cp); i += 6; }
                            } catch (NumberFormatException e) { sb.append('\\'); i++; }
                        } else { sb.append('\\'); i++; }
                        continue;
                }
            }
            if (c == '"') break;
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}