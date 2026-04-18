<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Subjects</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Manage Subjects</h1>

    <div class="form-card">
        <h2>Add Subject</h2>
        <form method="post" action="${pageContext.request.contextPath}/adminsubjectspage" class="admin-form">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Subject Name</label>
                    <input type="text" name="subject_name" required placeholder="e.g. Mathematics">
                </div>
                <div class="form-group">
                    <label>Subject Code</label>
                    <input type="text" name="subject_code" required placeholder="e.g. MATH101">
                </div>
            </div>
            <button type="submit" class="btn-primary">Add Subject</button>
        </form>
    </div>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>ID</th><th>Subject Name</th><th>Subject Code</th><th>Actions</th></tr>
            </thead>
            <tbody id="subjectsTable"></tbody>
        </table>
    </div>
</div>

<script>
var subjects = ${subjects};
var tbody = document.getElementById("subjectsTable");
if (subjects.length === 0) {
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No subjects found.</td></tr>";
} else {
    subjects.forEach(function(s) {
        tbody.innerHTML += "<tr><td>" + s.subject_id + "</td><td>" + s.subject_name + "</td><td>" +
            s.subject_code + "</td>" +
            "<td><form method='post' action='${pageContext.request.contextPath}/adminsubjectspage'>" +
            "<input type='hidden' name='action' value='delete'>" +
            "<input type='hidden' name='id' value='" + s.subject_id + "'>" +
            "<button class='btn-delete' onclick=\"return confirm('Delete?')\">Delete</button></form></td></tr>";
    });
}
</script>
</body>
</html>
