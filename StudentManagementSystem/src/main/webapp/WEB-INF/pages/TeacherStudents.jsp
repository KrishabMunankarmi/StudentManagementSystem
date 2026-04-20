<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>My Students</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>👨‍🎓 My Students</h1>
    <p style="color:#64748b; margin-bottom:1.5rem;">Students enrolled in your subject.</p>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>#</th><th>Student Name</th></tr>
            </thead>
            <tbody id="studentsTable"></tbody>
        </table>
    </div>
</div>
<script>
var students = ${students};
var tbody = document.getElementById("studentsTable");
if (students.length === 0) {
    tbody.innerHTML = "<tr><td colspan='2' style='text-align:center;'>No students found.</td></tr>";
} else {
    students.forEach(function(s, i) {
        tbody.innerHTML += "<tr><td>" + (i+1) + "</td><td>" + s.name + "</td></tr>";
    });
}
</script>
</body>
</html>
