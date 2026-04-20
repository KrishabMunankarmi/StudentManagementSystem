<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="admin-nav">
    <div class="nav-brand">🎓 SMS Admin</div>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/admindashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/adminstudents">Students</a></li>
        <li><a href="${pageContext.request.contextPath}/register">Register Student</a></li>
        <li><a href="${pageContext.request.contextPath}/adminteachers">Teachers</a></li>
        <li><a href="${pageContext.request.contextPath}/admingradespage">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/adminattendancepage">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/adminassignmentspage">Assignments</a></li>
        <li><a href="${pageContext.request.contextPath}/adminfeespage">Fees</a></li>
        <li><a href="${pageContext.request.contextPath}/adminsubjectspage">Subjects</a></li>
    </ul>
    <a href="${pageContext.request.contextPath}/adminlogin" class="nav-logout">Logout</a>
</nav>
