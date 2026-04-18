package com.StudentSystem.controller;

import com.StudentSystem.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/admindashboard")
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) return;

        try (Connection conn = DbConfig.getDbConnection()) {

            //Total students
            ResultSet rs = conn.prepareStatement("SELECT COUNT(*) AS total FROM students").executeQuery();
            request.setAttribute("totalStudents", rs.next() ? rs.getInt("total") : 0);

            //Total subjects
            rs = conn.prepareStatement("SELECT COUNT(*) AS total FROM subjects").executeQuery();
            request.setAttribute("totalSubjects", rs.next() ? rs.getInt("total") : 0);

            //Unpaid fees count
            rs = conn.prepareStatement("SELECT COUNT(*) AS total FROM fees WHERE paid = FALSE").executeQuery();
            request.setAttribute("unpaidFees", rs.next() ? rs.getInt("total") : 0);

            //Low attendance count (below 75%)
            rs = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM attendance " +
                "WHERE (attended / total_classes) * 100 < 75").executeQuery();
            request.setAttribute("lowAttendance", rs.next() ? rs.getInt("total") : 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/pages/AdminDashboard.jsp").forward(request, response);
    }

    public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/adminlogin");
            return false;
        }
        return true;
    }
}