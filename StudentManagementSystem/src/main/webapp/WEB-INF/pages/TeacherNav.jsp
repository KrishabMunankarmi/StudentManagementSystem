<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="admin-nav">
    <div class="nav-brand">👨‍🏫 Teacher Portal</div>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/teacherdashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacherstudents">My Students</a></li>
        <li><a href="${pageContext.request.contextPath}/teacherattendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/teachergrades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/teacherassignments">Assignments</a></li>
        <li><a href="${pageContext.request.contextPath}/teachersubmissions">Submissions</a></li>
    </ul>
    <a href="${pageContext.request.contextPath}/teacherlogin" class="nav-logout">Logout</a>
</nav>
