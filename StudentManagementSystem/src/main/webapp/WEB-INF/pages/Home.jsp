<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home - Student Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Home.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="Header.jsp" />

    <main class="home-container">
        <section class="hero-section">
            <div class="hero-content">
                <h1>Welcome to Your Student Portal</h1>
                <p class="subtitle">Your academic information, assignments, and AI assistant — all in one place.</p>
                <div class="cta-buttons">
                    <a href="${pageContext.request.contextPath}/chatbot" class="btn btn-primary">Open AI Assistant</a>
                    <a href="${pageContext.request.contextPath}/assignments" class="btn btn-outline">My Assignments</a>
                </div>
            </div>
            <div class="hero-image">
                <img src="${pageContext.request.contextPath}/resources/EmpManage.png" alt="Student Portal">
            </div>
        </section>

        <section class="features-section">
            <h2 class="section-title">What You Can Do</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <i class="fas fa-robot feature-icon"></i>
                    <h3>AI Chatbot Assistant</h3>
                    <p>Ask about your attendance, grades, timetable, exams, fees and more — instantly.</p>
                </div>
                <div class="feature-card">
                    <i class="fas fa-file-upload feature-icon"></i>
                    <h3>Note Summarizer</h3>
                    <p>Upload your lecture notes as PDF or DOCX and get an instant AI-powered summary.</p>
                </div>
                <div class="feature-card">
                    <i class="fas fa-tasks feature-icon"></i>
                    <h3>Assignment Submission</h3>
                    <p>View pending assignments and submit your work directly through the portal or chatbot.</p>
                </div>
                <div class="feature-card">
                    <i class="fas fa-chart-bar feature-icon"></i>
                    <h3>Academic Performance</h3>
                    <p>Track your grades, CGPA, attendance and get alerts when you are at risk.</p>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="Footer.jsp" />
</body>
</html>
