package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {
    "/teacherdashboard", "/teacherstudents",
    "/teacherattendance", "/teachergrades",
    "/teacherassignments", "/teachersubmissions"
})
public class TeacherDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isTeacher(request, response)) return;

        HttpSession session = request.getSession(false);
        int subjectId = (int) session.getAttribute("subject_id");
        String path   = request.getServletPath();

        switch (path) {
            case "/teacherdashboard":
                loadDashboard(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherDashboard.jsp").forward(request, response);
                break;
            case "/teacherstudents":
                loadStudents(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherStudents.jsp").forward(request, response);
                break;
            case "/teacherattendance":
                loadAttendance(request, subjectId);
                loadStudents(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherAttendance.jsp").forward(request, response);
                break;
            case "/teachergrades":
                loadGrades(request, subjectId);
                loadStudents(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherGrades.jsp").forward(request, response);
                break;
            case "/teacherassignments":
                loadAssignments(request, subjectId);
                loadStudents(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherAssignments.jsp").forward(request, response);
                break;
            case "/teachersubmissions":
                loadSubmissions(request, subjectId);
                request.getRequestDispatcher("/WEB-INF/pages/TeacherSubmissions.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isTeacher(request, response)) return;

        HttpSession session = request.getSession(false);
        int subjectId = (int) session.getAttribute("subject_id");
        String path   = request.getServletPath();
        String action = request.getParameter("action");

        try (Connection conn = DbConfig.getDbConnection()) {
            switch (path) {

                case "/teacherattendance":
                    if ("save".equals(action)) {
                        int studentId    = Integer.parseInt(request.getParameter("student_id"));
                        int total        = Integer.parseInt(request.getParameter("total_classes"));
                        int attended     = Integer.parseInt(request.getParameter("attended"));
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO attendance (student_id, subject_id, total_classes, attended) VALUES (?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE total_classes=VALUES(total_classes), attended=VALUES(attended)"
                        );
                        stmt.setInt(1, studentId);
                        stmt.setInt(2, subjectId);
                        stmt.setInt(3, total);
                        stmt.setInt(4, attended);
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/teacherattendance");
                    break;

                case "/teachergrades":
                    if ("save".equals(action)) {
                        int studentId = Integer.parseInt(request.getParameter("student_id"));
                        float marks   = Float.parseFloat(request.getParameter("marks"));
                        String grade  = request.getParameter("grade");
                        String sem    = request.getParameter("semester");
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO grades (student_id, subject_id, marks, grade, semester) VALUES (?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE marks=VALUES(marks), grade=VALUES(grade)"
                        );
                        stmt.setInt(1, studentId);
                        stmt.setInt(2, subjectId);
                        stmt.setFloat(3, marks);
                        stmt.setString(4, grade);
                        stmt.setString(5, sem);
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/teachergrades");
                    break;

                case "/teacherassignments":
                    if ("add".equals(action)) {
                        int studentId  = Integer.parseInt(request.getParameter("student_id"));
                        String title   = request.getParameter("title");
                        String dueDate = request.getParameter("due_date");
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO assignments (student_id, subject_id, title, due_date, submitted) VALUES (?,?,?,?,FALSE)"
                        );
                        stmt.setInt(1, studentId);
                        stmt.setInt(2, subjectId);
                        stmt.setString(3, title);
                        stmt.setString(4, dueDate);
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        int id = Integer.parseInt(request.getParameter("id"));
                        PreparedStatement stmt = conn.prepareStatement(
                            "DELETE FROM assignments WHERE id=? AND subject_id=?"
                        );
                        stmt.setInt(1, id);
                        stmt.setInt(2, subjectId);
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/teacherassignments");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/teacherdashboard");
        }
    }

    private void loadDashboard(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            //Subject name
            ResultSet rs = conn.prepareStatement(
                "SELECT subject_name FROM subjects WHERE subject_id=" + subjectId).executeQuery();
            request.setAttribute("subjectName", rs.next() ? rs.getString("subject_name") : "Unknown");

            //Student count
            rs = conn.prepareStatement(
                "SELECT COUNT(DISTINCT student_id) AS total FROM timetable WHERE subject_id=" + subjectId).executeQuery();
            request.setAttribute("studentCount", rs.next() ? rs.getInt("total") : 0);

            //Pending submissions
            rs = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM assignments WHERE subject_id=" + subjectId +
                " AND submitted=FALSE AND due_date>=CURDATE()").executeQuery();
            request.setAttribute("pendingSubmissions", rs.next() ? rs.getInt("total") : 0);

            //Low attendance count
            rs = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM attendance WHERE subject_id=" + subjectId +
                " AND (attended/total_classes)*100 < 75").executeQuery();
            request.setAttribute("lowAttendance", rs.next() ? rs.getInt("total") : 0);

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadStudents(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT s.student_id, s.name FROM students s " +
                "JOIN timetable t ON s.student_id = t.student_id " +
                "WHERE t.subject_id = ? ORDER BY s.name"
            );
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{\"id\":").append(rs.getInt("student_id"))
                    .append(",\"name\":\"").append(escape(rs.getString("name"))).append("\"}");
                first = false;
            }
            json.append("]");
            request.setAttribute("students", json.toString());
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("students", "[]"); }
    }

    private void loadAttendance(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, s.name, a.total_classes, a.attended, " +
                "ROUND((a.attended/a.total_classes)*100,1) AS percentage " +
                "FROM attendance a JOIN students s ON a.student_id=s.student_id " +
                "WHERE a.subject_id=? ORDER BY s.name"
            );
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("attendance", buildJson(rs, "id", "name", "total_classes", "attended", "percentage"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("attendance", "[]"); }
    }

    private void loadGrades(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT g.id, s.name, g.marks, g.grade, g.semester " +
                "FROM grades g JOIN students s ON g.student_id=s.student_id " +
                "WHERE g.subject_id=? ORDER BY s.name"
            );
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("grades", buildJson(rs, "id", "name", "marks", "grade", "semester"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("grades", "[]"); }
    }

    private void loadAssignments(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, s.name, a.title, a.due_date, a.submitted " +
                "FROM assignments a JOIN students s ON a.student_id=s.student_id " +
                "WHERE a.subject_id=? ORDER BY a.due_date"
            );
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("assignments", buildJson(rs, "id", "name", "title", "due_date", "submitted"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("assignments", "[]"); }
    }

    private void loadSubmissions(HttpServletRequest request, int subjectId) {
        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.id, s.name, a.title, a.submitted_at, a.submission_file " +
                "FROM assignments a JOIN students s ON a.student_id=s.student_id " +
                "WHERE a.subject_id=? AND a.submitted=TRUE ORDER BY a.submitted_at DESC"
            );
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("submissions", buildJson(rs, "id", "name", "title", "submitted_at", "submission_file"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("submissions", "[]"); }
    }

    private String buildJson(ResultSet rs, String... columns) throws SQLException {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        while (rs.next()) {
            if (!first) json.append(",");
            json.append("{");
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) json.append(",");
                Object val = rs.getObject(columns[i]);
                json.append("\"").append(columns[i]).append("\":");
                if (val == null) json.append("null");
                else if (val instanceof Number || val instanceof Boolean) json.append(val);
                else json.append("\"").append(escape(val.toString())).append("\"");
            }
            json.append("}");
            first = false;
        }
        return json.append("]").toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static boolean isTeacher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/teacherlogin");
            return false;
        }
        return true;
    }
}