<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Assignments</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>📌 Manage Assignments</h1>

    <div class="form-card">
        <h2>Add Assignment</h2>
        <form method="post" action="${pageContext.request.contextPath}/teacherassignments" class="admin-form">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Student</label>
                    <select name="student_id" required id="studentSelect"></select>
                </div>
                <div class="form-group">
                    <label>Title</label>
                    <input type="text" name="title" required placeholder="Assignment title">
                </div>
                <div class="form-group">
                    <label>Due Date</label>
                    <input type="date" name="due_date" required>
                </div>
            </div>
            <button type="submit" class="btn-primary">Add Assignment</button>
        </form>
    </div>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Title</th><th>Due Date</th><th>Submitted</th><th>Actions</th></tr>
            </thead>
            <tbody id="assignmentsTable"></tbody>
        </table>
    </div>
</div>
<script>
var students    = ${students};
var assignments = ${assignments};
var ss = document.getElementById("studentSelect");
students.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });
var tbody = document.getElementById("assignmentsTable");
if (assignments.length === 0) {
    tbody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>No assignments yet.</td></tr>";
} else {
    assignments.forEach(function(a) {
        var submitted = a.submitted ? "✅ Yes" : "❌ No";
        tbody.innerHTML += "<tr><td>" + a.name + "</td><td>" + a.title + "</td><td>" +
            a.due_date + "</td><td>" + submitted + "</td>" +
            "<td><form method='post' action='${pageContext.request.contextPath}/teacherassignments' style='display:inline'>" +
            "<input type='hidden' name='action' value='delete'>" +
            "<input type='hidden' name='id' value='" + a.id + "'>" +
            "<button class='btn-delete' onclick=\"return confirm('Delete?')\">Delete</button></form></td></tr>";
    });
}
</script>
</body>
</html>
