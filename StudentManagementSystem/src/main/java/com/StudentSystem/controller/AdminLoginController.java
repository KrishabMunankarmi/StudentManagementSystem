package com.StudentSystem.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/adminlogin")
public class AdminLoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/AdminLogin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("admin".equals(username) && "admin".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("fullName", "Administrator");
            session.setAttribute("role", "admin");
            response.sendRedirect(request.getContextPath() + "/admindashboard");
        } else {
            request.setAttribute("error", "Invalid admin credentials.");
            request.getRequestDispatcher("/WEB-INF/pages/AdminLogin.jsp").forward(request, response);
        }
    }
}