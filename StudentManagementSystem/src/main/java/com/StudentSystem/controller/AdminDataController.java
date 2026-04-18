package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

/**
 * Handles all admin data management:
 * /admingrades    - manage grades
 * /adminattendance - manage attendance
 * /adminassignments - manage assignments
 * /adminfees      - manage fees
 * /adminsubjects  - manage subjects
 */
@WebServlet(urlPatterns = {
    "/admingradespage", "/adminattendancepage",
    "/adminassignmentspage", "/adminfeespage", "/adminsubjectspage"
})
public class AdminDataController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminDashboardController.isAdmin(request, response)) return;

        String path = request.getServletPath();
        loadSubjectsAndStudents(request);

        switch (path) {
            case "/admingradespage":
                loadGrades(request);
                request.getRequestDispatcher("/WEB-INF/pages/AdminGrades.jsp").forward(request, response);
                break;
            case "/adminattendancepage":
                loadAttendance(request);
                request.getRequestDispatcher("/WEB-INF/pages/AdminAttendance.jsp").forward(request, response);
                break;
            case "/adminassignmentspage":
                loadAssignments(request);
                request.getRequestDispatcher("/WEB-INF/pages/AdminAssignments.jsp").forward(request, response);
                break;
            case "/adminfeespage":
                loadFees(request);
                request.getRequestDispatcher("/WEB-INF/pages/AdminFees.jsp").forward(request, response);
                break;
            case "/adminsubjectspage":
                loadSubjectsList(request);
                request.getRequestDispatcher("/WEB-INF/pages/AdminSubjects.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminDashboardController.isAdmin(request, response)) return;

        String path   = request.getServletPath();
        String action = request.getParameter("action");

        try (Connection conn = DbConfig.getDbConnection()) {
            switch (path) {

                case "/admingradespage":
                    if ("add".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO grades (student_id, subject_id, marks, grade, semester) VALUES (?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE marks=VALUES(marks), grade=VALUES(grade)");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("student_id")));
                        stmt.setInt(2, Integer.parseInt(request.getParameter("subject_id")));
                        stmt.setFloat(3, Float.parseFloat(request.getParameter("marks")));
                        stmt.setString(4, request.getParameter("grade"));
                        stmt.setString(5, request.getParameter("semester"));
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM grades WHERE id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/admingradespage");
                    break;

                case "/adminattendancepage":
                    if ("add".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO attendance (student_id, subject_id, total_classes, attended) VALUES (?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE total_classes=VALUES(total_classes), attended=VALUES(attended)");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("student_id")));
                        stmt.setInt(2, Integer.parseInt(request.getParameter("subject_id")));
                        stmt.setInt(3, Integer.parseInt(request.getParameter("total_classes")));
                        stmt.setInt(4, Integer.parseInt(request.getParameter("attended")));
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM attendance WHERE id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/adminattendancepage");
                    break;

                case "/adminassignmentspage":
                    if ("add".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO assignments (student_id, subject_id, title, due_date, submitted) VALUES (?,?,?,?,FALSE)");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("student_id")));
                        stmt.setInt(2, Integer.parseInt(request.getParameter("subject_id")));
                        stmt.setString(3, request.getParameter("title"));
                        stmt.setString(4, request.getParameter("due_date"));
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM assignments WHERE id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/adminassignmentspage");
                    break;

                case "/adminfeespage":
                    if ("add".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO fees (student_id, amount, due_date, paid) VALUES (?,?,?,?)");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("student_id")));
                        stmt.setDouble(2, Double.parseDouble(request.getParameter("amount")));
                        stmt.setString(3, request.getParameter("due_date"));
                        stmt.setBoolean(4, "true".equals(request.getParameter("paid")));
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM fees WHERE id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    } else if ("markpaid".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("UPDATE fees SET paid=TRUE WHERE id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/adminfeespage");
                    break;

                case "/adminsubjectspage":
                    if ("add".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO subjects (subject_name, subject_code) VALUES (?,?)");
                        stmt.setString(1, request.getParameter("subject_name"));
                        stmt.setString(2, request.getParameter("subject_code"));
                        stmt.executeUpdate();
                    } else if ("delete".equals(action)) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM subjects WHERE subject_id=?");
                        stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                        stmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/adminsubjectspage");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admindashboard");
        }
    }

    private void loadSubjectsAndStudents(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT student_id, name FROM students ORDER BY name").executeQuery();
            StringBuilder students = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) students.append(",");
                students.append("{\"id\":").append(rs.getInt("student_id"))
                        .append(",\"name\":\"").append(escape(rs.getString("name"))).append("\"}");
                first = false;
            }
            students.append("]");
            request.setAttribute("studentsList", students.toString());

            rs = conn.prepareStatement("SELECT subject_id, subject_name FROM subjects ORDER BY subject_name").executeQuery();
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
            request.setAttribute("studentsList", "[]");
            request.setAttribute("subjectsList", "[]");
        }
    }

    private void loadGrades(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT g.id, st.name, su.subject_name, g.marks, g.grade, g.semester " +
                "FROM grades g JOIN students st ON g.student_id=st.student_id " +
                "JOIN subjects su ON g.subject_id=su.subject_id ORDER BY st.name").executeQuery();
            request.setAttribute("grades", buildJson(rs, "id", "name", "subject_name", "marks", "grade", "semester"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("grades", "[]"); }
    }

    private void loadAttendance(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT a.id, st.name, su.subject_name, a.total_classes, a.attended, " +
                "ROUND((a.attended/a.total_classes)*100,1) AS percentage " +
                "FROM attendance a JOIN students st ON a.student_id=st.student_id " +
                "JOIN subjects su ON a.subject_id=su.subject_id ORDER BY st.name").executeQuery();
            request.setAttribute("attendance", buildJson(rs, "id", "name", "subject_name", "total_classes", "attended", "percentage"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("attendance", "[]"); }
    }

    private void loadAssignments(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement(
            	"SELECT a.id, st.name, su.subject_name, a.title, a.due_date, a.submitted, a.submission_file " +
                "FROM assignments a JOIN students st ON a.student_id=st.student_id " +
                "JOIN subjects su ON a.subject_id=su.subject_id ORDER BY a.due_date").executeQuery();
            request.setAttribute("assignments", buildJson(rs, "id", "name", "subject_name", "title", "due_date", "submitted", "submission_file"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("assignments", "[]"); }
    }

    private void loadFees(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT f.id, st.name, f.amount, f.due_date, f.paid " +
                "FROM fees f JOIN students st ON f.student_id=st.student_id ORDER BY f.due_date DESC").executeQuery();
            request.setAttribute("fees", buildJson(rs, "id", "name", "amount", "due_date", "paid"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("fees", "[]"); }
    }

    private void loadSubjectsList(HttpServletRequest request) {
        try (Connection conn = DbConfig.getDbConnection()) {
            ResultSet rs = conn.prepareStatement(
                "SELECT subject_id, subject_name, subject_code FROM subjects ORDER BY subject_name").executeQuery();
            request.setAttribute("subjects", buildJson(rs, "subject_id", "subject_name", "subject_code"));
        } catch (SQLException e) { e.printStackTrace(); request.setAttribute("subjects", "[]"); }
    }

    private String buildJson(ResultSet rs, String... columns) throws SQLException {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        ResultSetMetaData meta = rs.getMetaData();
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
        json.append("]");
        return json.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}