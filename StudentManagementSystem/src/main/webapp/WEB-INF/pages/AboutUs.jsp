<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>About - Student Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AboutUs.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="Header.jsp" />

    <main class="about-container">
        <section class="about-hero">
            <h1>About This System</h1>
            <p>The Student Management System (SMS) is an AI-powered academic portal developed as a Final Year Project at Islington College. It combines intelligent academic assistance with a fully functional student management platform.</p>
        </section>

        <section class="team-section">
            <h2>Meet The Developer</h2>
            <div class="team-grid" style="justify-content:center;">
                <div class="team-card">
                    <div class="team-photo">
                        <img src="${pageContext.request.contextPath}/resources/Aarav.png" alt="Krishab Munankarmi">
                    </div>
                    <h3>Krishab Munankarmi</h3>
                    <p class="position">Developer — BSc (Hons) Computer Science</p>
                    <p class="bio">Final Year student at Islington College developing an AI-integrated Student Management System as part of the CS6P05NI Final Year Project module.</p>
                </div>
            </div>
        </section>

        <section class="mission-section">
            <h2>Project Overview</h2>
            <div class="mission-content">
                <div class="mission-text">
                    <p>This system was built to address the limitations of traditional student management platforms that focus only on administrative tasks. By integrating an AI chatbot, students can instantly access their academic information through natural language queries.</p>
                    <p>The AI module uses Natural Language Processing (NLP) with TF-IDF vectorization and Support Vector Machine (SVM) classification, built entirely from scratch without relying on pre-built chatbot frameworks.</p>
                </div>
                <div class="mission-image">
                    <img src="${pageContext.request.contextPath}/resources/EmpManage.png" alt="System Overview">
                </div>
            </div>
        </section>

        <section class="contact-info-section">
            <h2>Contact</h2>
            <p>For any queries regarding this project, please reach out:</p>
            <ul class="contact-list">
                <li>📧 Email: kabinkrish@gmail.com</li>
                <li>🏫 Institution: Islington College, Kathmandu, Nepal</li>
                <li>📋 Module: CS6P05NI — Final Year Project</li>
            </ul>
        </section>
    </main>

    <jsp:include page="Footer.jsp" />
</body>
</html>
