package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import com.StudentSystem.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/teacherlogin")
public class TeacherLoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/TeacherLogin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT teacher_id, name, password, subject_id FROM teachers WHERE email = ?"
            );
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                boolean valid = false;

                //Support both hashed and plain text passwords
                try {
                    valid = PasswordUtil.verifyPassword(password, storedHash);
                } catch (Exception e) {
                    valid = password.equals(storedHash);
                }

                if (valid) {
                    HttpSession session = request.getSession();
                    session.setAttribute("teacher_id",  rs.getInt("teacher_id"));
                    session.setAttribute("fullName",    rs.getString("name"));
                    session.setAttribute("subject_id",  rs.getInt("subject_id"));
                    session.setAttribute("role",        "teacher");
                    response.sendRedirect(request.getContextPath() + "/teacherdashboard");
                    return;
                }
            }

            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/pages/TeacherLogin.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/pages/TeacherLogin.jsp").forward(request, response);
        }
    }
}