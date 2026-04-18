<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile - Student Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Profile.css">
</head>
<body>
    <jsp:include page="Header.jsp" />

    <main class="profile-container">
        <h1 class="profile-title">My Profile</h1>

        <div class="profile-picture">
		    <img src="${pageContext.request.contextPath}/resources/Man.png" 
		         alt="Profile Picture" id="profilePreview">
		</div>

        <div class="profile-details">
            <div class="detail-group">
                <label>Student ID:</label>
                <span>${sessionScope.student_id}</span>
            </div>
            <div class="detail-group">
                <label>Full Name:</label>
                <span>${sessionScope.fullName}</span>
            </div>
            <div class="detail-group">
                <label>Email:</label>
                <span>${email}</span>
            </div>
            <div class="detail-group">
                <label>Programme:</label>
                <span>${programme != null ? programme : 'Not specified'}</span>
            </div>
            <div class="detail-group">
                <label>Year of Study:</label>
                <span>${year_of_study != null ? year_of_study : 'Not specified'}</span>
            </div>
        </div>

        <div class="profile-actions">
            <a href="${pageContext.request.contextPath}/chatbot" class="action-btn edit-profile">Open AI Assistant</a>
            <a href="${pageContext.request.contextPath}/assignments" class="action-btn change-password">My Assignments</a>
        </div>
    </main>

    <jsp:include page="Footer.jsp" />
</body>
</html>
