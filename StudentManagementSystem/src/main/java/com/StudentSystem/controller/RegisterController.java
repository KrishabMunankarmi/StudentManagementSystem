package com.StudentSystem.controller;

import com.StudentSystem.service.RegisterService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Only admin can access registration
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/adminlogin");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Only admin can register students
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/adminlogin");
            return;
        }

        String redirectURL = request.getContextPath() + "/register";

        try {
            boolean success = registerService.registerUser(request, null);
            if (success) {
                response.sendRedirect(redirectURL + "?success=true");
            } else {
                response.sendRedirect(redirectURL + "?error=1");
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect(redirectURL + "?error=3");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectURL + "?error=2");
        }
    }
}