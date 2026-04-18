<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Admin Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body class="login-body">
<div class="login-card">
    <h2>Admin Login</h2>
    <p class="login-subtitle">Student Management System</p>

    <% if(request.getAttribute("error") != null) { %>
        <div class="alert-error">${error}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/adminlogin" method="post">
        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required placeholder="Enter username">
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="Enter password">
        </div>
        <button type="submit" class="btn-primary btn-block">Login</button>
    </form>
</div>
</body>
</html>
