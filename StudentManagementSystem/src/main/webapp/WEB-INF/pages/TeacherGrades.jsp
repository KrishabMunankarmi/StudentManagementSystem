<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Grades</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>📊 Manage Grades</h1>

    <div class="form-card">
        <h2>Add / Update Grade</h2>
        <form method="post" action="${pageContext.request.contextPath}/teachergrades" class="admin-form">
            <input type="hidden" name="action" value="save">
            <div class="form-row">
                <div class="form-group">
                    <label>Student</label>
                    <select name="student_id" required id="studentSelect"></select>
                </div>
                <div class="form-group">
                    <label>Marks</label>
                    <input type="number" name="marks" min="0" max="100" step="0.1" required>
                </div>
                <div class="form-group">
                    <label>Grade</label>
                    <select name="grade" required>
                        <option>A+</option><option>A</option><option>B+</option>
                        <option>B</option><option>C+</option><option>C</option>
                        <option>D</option><option>F</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Semester</label>
                    <input type="text" name="semester" placeholder="e.g. Semester 1" required>
                </div>
            </div>
            <button type="submit" class="btn-primary">Save Grade</button>
        </form>
    </div>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Marks</th><th>Grade</th><th>Semester</th></tr>
            </thead>
            <tbody id="gradesTable"></tbody>
        </table>
    </div>
</div>
<script>
var students = ${students};
var grades   = ${grades};
var ss = document.getElementById("studentSelect");
students.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });
var tbody = document.getElementById("gradesTable");
if (grades.length === 0) {
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No grades yet.</td></tr>";
} else {
    grades.forEach(function(g) {
        tbody.innerHTML += "<tr><td>" + g.name + "</td><td>" + g.marks + "</td><td>" +
            g.grade + "</td><td>" + g.semester + "</td></tr>";
    });
}
</script>
</body>
</html>
