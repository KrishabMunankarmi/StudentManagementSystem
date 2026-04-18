<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Only admin can access this page
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/adminlogin");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register Student</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Register New Student</h1>

    <div class="form-card">
        <% if(request.getParameter("error") != null) { %>
            <div class="alert-error">
                <%= request.getParameter("error").equals("3") ? "Passwords do not match." :
                   request.getParameter("error").equals("1") ? "Registration failed. Email may already be in use." :
                   request.getParameter("error").equals("2") ? "Database error. Please try again." :
                   "An error occurred." %>
            </div>
        <% } %>

        <% if(request.getParameter("success") != null) { %>
            <div style="background:#d4edda; color:#155724; border:1px solid #c3e6cb; padding:0.75rem; border-radius:6px; margin-bottom:1rem;">
                Student registered successfully!
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register" method="post" class="admin-form">
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" required>
                </div>
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" name="email" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Programme</label>
                    <input type="text" name="programme" placeholder="e.g. BSc Computer Science">
                </div>
                <div class="form-group">
                    <label>Year of Study</label>
                    <input type="number" name="year_of_study" min="1" max="6">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <div class="form-group">
                    <label>Confirm Password</label>
                    <input type="password" name="re-password" required>
                </div>
            </div>
            <button type="submit" class="btn-primary">Register Student</button>
            <a href="${pageContext.request.contextPath}/adminstudents" class="btn-secondary" style="margin-left:0.5rem;">Cancel</a>
        </form>
    </div>
</div>
</body>
</html>
