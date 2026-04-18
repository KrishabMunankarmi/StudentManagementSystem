<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Grades</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Manage Grades</h1>

    <!-- Add Grade Form -->
    <div class="form-card">
        <h2>Add / Update Grade</h2>
        <form method="post" action="${pageContext.request.contextPath}/admingradespage" class="admin-form">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Student</label>
                    <select name="student_id" required id="studentSelect"></select>
                </div>
                <div class="form-group">
                    <label>Subject</label>
                    <select name="subject_id" required id="subjectSelect"></select>
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

    <!-- Grades Table -->
    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Subject</th><th>Marks</th><th>Grade</th><th>Semester</th><th>Actions</th></tr>
            </thead>
            <tbody id="gradesTable"></tbody>
        </table>
    </div>
</div>

<script>
var students = ${studentsList};
var subjects = ${subjectsList};
var grades   = ${grades};

var ss = document.getElementById("studentSelect");
var su = document.getElementById("subjectSelect");
students.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });
subjects.forEach(function(s) { su.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });

var tbody = document.getElementById("gradesTable");
if (grades.length === 0) {
    tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No grades found.</td></tr>";
} else {
    grades.forEach(function(g) {
        tbody.innerHTML += "<tr><td>" + g.name + "</td><td>" + g.subject_name + "</td><td>" +
            g.marks + "</td><td>" + g.grade + "</td><td>" + g.semester + "</td>" +
            "<td><form method='post' action='${pageContext.request.contextPath}/admingradespage'>" +
            "<input type='hidden' name='action' value='delete'>" +
            "<input type='hidden' name='id' value='" + g.id + "'>" +
            "<button class='btn-delete' onclick=\"return confirm('Delete?')\">Delete</button></form></td></tr>";
    });
}
</script>
</body>
</html>
