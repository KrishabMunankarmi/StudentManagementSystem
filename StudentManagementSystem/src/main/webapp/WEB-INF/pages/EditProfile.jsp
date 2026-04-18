<%-- Edit Profile JSP --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Profile - Employee Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/EditProfile.css">
</head>
<body>
    <jsp:include page="Header.jsp" />

    <main class="profile-container">
        <h1 class="profile-title">Edit Profile</h1>

        <% 
            if (session.getAttribute("successMessage") != null) {
        %>
            <div class="success"><%= session.getAttribute("successMessage") %></div>
        <%
            session.removeAttribute("successMessage");
            }
        %>

        <% 
            if (request.getAttribute("errorMessage") != null) {
        %>
            <div class="error"><%= request.getAttribute("errorMessage") %></div>
        <%
            }
        %>

        <form class="edit-form" action="${pageContext.request.contextPath}/editprofile" method="post">
            <div class="form-group">
                <label for="fullName">Full Name:</label>
                <input type="text" id="fullName" name="fullName" value="<%= request.getAttribute("fullName") %>" required>
            </div>

            <div class="form-group">
                <label for="age">Age:</label>
                <input type="number" id="age" name="age" value="<%= request.getAttribute("age") %>" min="18" max="100" required>
            </div>

            <div class="form-group">
                <label for="contact">Contact:</label>
                <input type="tel" id="contact" name="contact" value="<%= request.getAttribute("contact") %>" pattern="[0-9]{10}" required>
            </div>

            <div class="form-actions">
                <button type="submit" class="save-btn">Save Changes</button>
                <a href="${pageContext.request.contextPath}/profile" class="cancel-btn">Cancel</a>
            </div>
        </form>
    </main>

    <jsp:include page="Footer.jsp" />
</body>
</html>
