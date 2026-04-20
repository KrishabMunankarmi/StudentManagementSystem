<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Teachers</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>👨‍🏫 Manage Teachers</h1>

    <!-- Add Teacher Form -->
    <div class="form-card">
        <h2>Add / Register Teacher</h2>
        <form method="post" action="${pageContext.request.contextPath}/adminteachers" class="admin-form">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" required placeholder="e.g. Mr. Sharma">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" required placeholder="teacher@sms.com">
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required placeholder="Set a password">
                </div>
                <div class="form-group">
                    <label>Subject</label>
                    <select name="subject_id" required id="subjectSelect"></select>
                </div>
            </div>
            <button type="submit" class="btn-primary">Add Teacher</button>
        </form>
    </div>

    <% if(request.getAttribute("success") != null) { %>
        <div style="background:#d4edda;color:#155724;border:1px solid #c3e6cb;padding:0.75rem;border-radius:6px;margin-bottom:1rem;">
            ✅ ${success}
        </div>
    <% } %>
    <% if(request.getAttribute("error") != null) { %>
        <div style="background:#fee2e2;color:#b91c1c;border:1px solid #fecaca;padding:0.75rem;border-radius:6px;margin-bottom:1rem;">
            ❌ ${error}
        </div>
    <% } %>

    <!-- Teachers Table -->
    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>ID</th><th>Name</th><th>Email</th><th>Subject</th><th>Actions</th></tr>
            </thead>
            <tbody id="teachersTable"></tbody>
        </table>
    </div>
</div>

<script>
var subjects = ${subjectsList};
var teachers = ${teachers};

var ss = document.getElementById("subjectSelect");
subjects.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });

var tbody = document.getElementById("teachersTable");
if (teachers.length === 0) {
    tbody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>No teachers found.</td></tr>";
} else {
    teachers.forEach(function(t) {
        tbody.innerHTML += "<tr><td>" + t.teacher_id + "</td><td>" + t.name + "</td><td>" +
            t.email + "</td><td>" + t.subject_name + "</td>" +
            "<td><form method='post' action='${pageContext.request.contextPath}/adminteachers' style='display:inline'>" +
            "<input type='hidden' name='action' value='delete'>" +
            "<input type='hidden' name='id' value='" + t.teacher_id + "'>" +
            "<button class='btn-delete' onclick=\"return confirm('Delete this teacher?')\">Delete</button></form></td></tr>";
    });
}
</script>
</body>
</html>
