<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Teacher Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body class="login-body">
<div class="login-card">
    <h2>👨‍🏫 Teacher Login</h2>
    <p class="login-subtitle">Student Management System</p>

    <% if(request.getAttribute("error") != null) { %>
        <div class="alert-error">${error}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/teacherlogin" method="post">
        <div class="form-group">
            <label>Email Address</label>
            <input type="email" name="email" required placeholder="Enter your email">
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="Enter your password">
        </div>
        <button type="submit" class="btn-primary btn-block">Login</button>
    </form>
    <p style="text-align:center; margin-top:1rem; font-size:0.85rem; color:#64748b;">
        <a href="${pageContext.request.contextPath}/login" style="color:#2563eb;">Student Login</a>
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/adminlogin" style="color:#2563eb;">Admin Login</a>
    </p>
</div>
</body>
</html>
