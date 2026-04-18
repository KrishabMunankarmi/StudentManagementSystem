<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Students</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Manage Students</h1>

    <!-- Search -->
    <form method="get" action="${pageContext.request.contextPath}/adminstudents" class="search-form">
        <input type="text" name="search" placeholder="Search by name or email..." value="${search}">
        <button type="submit" class="btn-primary">Search</button>
        <a href="${pageContext.request.contextPath}/adminstudents" class="btn-secondary">Clear</a>
    </form>

    <!-- Students Table -->
    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Programme</th>
                    <th>Year</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="studentsTable"></tbody>
        </table>
    </div>
</div>

<script>
var students = ${students};
var tbody = document.getElementById("studentsTable");
if (students.length === 0) {
    tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No students found.</td></tr>";
} else {
    students.forEach(function(s) {
        var row = "<tr>" +
            "<td>" + s.id + "</td>" +
            "<td>" + s.name + "</td>" +
            "<td>" + s.email + "</td>" +
            "<td>" + (s.programme || "-") + "</td>" +
            "<td>" + (s.year || "-") + "</td>" +
            "<td><a href='${pageContext.request.contextPath}/adminstudents?action=delete&id=" + s.id + "' " +
            "class='btn-delete' onclick=\"return confirm('Delete this student?')\">Delete</a></td>" +
            "</tr>";
        tbody.innerHTML += row;
    });
}
</script>
</body>
</html>
