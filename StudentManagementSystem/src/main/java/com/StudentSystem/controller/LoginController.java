package com.StudentSystem.controller;

import com.StudentSystem.service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Login form uses "name" field for email (keeping your existing form as-is)
        String email    = request.getParameter("name");
        String password = request.getParameter("password");

        Object[] result = loginService.loginStudent(email, password);

        if (result != null) {
            //Login success
            HttpSession session = request.getSession();
            session.setAttribute("student_id", result[0]);   // int
            session.setAttribute("fullName",   result[1]);   // String
            session.setAttribute("role",       "student");

            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            //Login failed
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(request, response);
    }
}