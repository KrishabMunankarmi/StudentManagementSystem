<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Admin Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Dashboard</h1>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">👨‍🎓</div>
            <div class="stat-info">
                <h3>Total Students</h3>
                <p class="stat-value">${totalStudents}</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📚</div>
            <div class="stat-info">
                <h3>Total Subjects</h3>
                <p class="stat-value">${totalSubjects}</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">💳</div>
            <div class="stat-info">
                <h3>Unpaid Fees</h3>
                <p class="stat-value">${unpaidFees}</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">⚠️</div>
            <div class="stat-info">
                <h3>Low Attendance</h3>
                <p class="stat-value">${lowAttendance}</p>
            </div>
        </div>
    </div>

    <div class="quick-links">
        <h2>Quick Actions</h2>
        <div class="links-grid">
            <a href="${pageContext.request.contextPath}/adminstudents" class="link-card">👨‍🎓 Manage Students</a>
            <a href="${pageContext.request.contextPath}/admingradespage" class="link-card">📊 Manage Grades</a>
            <a href="${pageContext.request.contextPath}/adminattendancepage" class="link-card">📋 Manage Attendance</a>
            <a href="${pageContext.request.contextPath}/adminassignmentspage" class="link-card">📌 Manage Assignments</a>
            <a href="${pageContext.request.contextPath}/adminfeespage" class="link-card">💳 Manage Fees</a>
            <a href="${pageContext.request.contextPath}/adminsubjectspage" class="link-card">📚 Manage Subjects</a>
        </div>
    </div>
</div>
</body>
</html>
