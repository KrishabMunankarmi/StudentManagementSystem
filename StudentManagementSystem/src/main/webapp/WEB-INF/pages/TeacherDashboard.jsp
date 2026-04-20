<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Teacher Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>Welcome, ${sessionScope.fullName}!</h1>
    <p style="color:#64748b; margin-bottom:1.5rem;">You are teaching: <strong>${subjectName}</strong></p>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">👨‍🎓</div>
            <div class="stat-info">
                <h3>My Students</h3>
                <p class="stat-value">${studentCount}</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📌</div>
            <div class="stat-info">
                <h3>Pending Submissions</h3>
                <p class="stat-value">${pendingSubmissions}</p>
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
            <a href="${pageContext.request.contextPath}/teacherstudents"    class="link-card">👨‍🎓 My Students</a>
            <a href="${pageContext.request.contextPath}/teacherattendance"  class="link-card">📋 Mark Attendance</a>
            <a href="${pageContext.request.contextPath}/teachergrades"      class="link-card">📊 Enter Grades</a>
            <a href="${pageContext.request.contextPath}/teacherassignments" class="link-card">📌 Assignments</a>
            <a href="${pageContext.request.contextPath}/teachersubmissions" class="link-card">📄 View Submissions</a>
        </div>
    </div>
</div>
</body>
</html>
