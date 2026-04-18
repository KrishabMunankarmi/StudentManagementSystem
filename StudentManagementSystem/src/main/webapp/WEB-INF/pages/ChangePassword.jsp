<%-- Change Password JSP --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Change Password - Employee Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ChangePassword.css">
</head>
<body>
    <jsp:include page="Header.jsp" />

    <main class="change-password-container">
        <h1 class="change-password-title">Change Password</h1>

        <form class="password-form" action="${pageContext.request.contextPath}/changepassword" method="post">
            <div class="password-form-group">
                <label for="currentPassword">Current Password:</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>

            <div class="password-form-group">
                <label for="newPassword">New Password:</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>

            <div class="password-form-group">
                <label for="confirmPassword">Confirm New Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <div class="password-form-actions">
                <button type="submit" class="password-submit-btn">Change Password</button>
                <a href="${pageContext.request.contextPath}/profile" class="password-cancel-btn">Cancel</a>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="password-error-message"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="password-success-message"><%= request.getAttribute("success") %></div>
            <% } %>
        </form>
    </main>

    <jsp:include page="Footer.jsp" />
</body>
</html>
