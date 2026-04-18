package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadProfileData(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadProfileData(request, response);
    }

    private void loadProfileData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("student_id") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        try (Connection conn = DbConfig.getDbConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT student_id, name, email, programme, year_of_study " +
                "FROM students WHERE student_id = ?"
            );
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                request.setAttribute("email",         rs.getString("email"));
                request.setAttribute("programme",     rs.getString("programme"));
                request.setAttribute("year_of_study", rs.getInt("year_of_study"));
            }

            //Default profile image
            request.setAttribute("imagePath", "resources/Man.png");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading profile data.");
        }

        request.getRequestDispatcher("/WEB-INF/pages/Profile.jsp").forward(request, response);
    }
}