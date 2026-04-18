/* package com.StudentSystem.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        // Define pages/resources that don't require login
        boolean isLoginPage = uri.endsWith("/login");
        boolean isRegisterPage = uri.endsWith("/register");
        boolean isPublicResource = uri.endsWith(".css") || uri.endsWith(".js") 
                                  || uri.endsWith(".png") || uri.endsWith(".jpg");

        HttpSession session = req.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        boolean isLoggedIn = role != null;

        // Pages restricted to admin users only
        boolean isAdminOnly = uri.endsWith("/dashboard") 
                              || uri.endsWith("/adminemployee") 
                              || uri.endsWith("/admindepartment");

        // Allow access to public pages/resources without authentication
        if (isPublicResource || isLoginPage || isRegisterPage) {
            chain.doFilter(request, response);
            return;
        }

        // Redirect unauthenticated users to login page
        if (!isLoggedIn) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        // Restrict admin-only pages for non-admin users
        if (isAdminOnly && !"admin".equalsIgnoreCase(role)) {
            res.sendRedirect(contextPath + "/home");
            return;
        }

        // User has access, continue processing request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
*/