<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Attendance</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>📋 Manage Attendance</h1>

    <div class="form-card">
        <h2>Add / Update Attendance</h2>
        <form method="post" action="${pageContext.request.contextPath}/teacherattendance" class="admin-form">
            <input type="hidden" name="action" value="save">
            <div class="form-row">
                <div class="form-group">
                    <label>Student</label>
                    <select name="student_id" required id="studentSelect"></select>
                </div>
                <div class="form-group">
                    <label>Total Classes</label>
                    <input type="number" name="total_classes" min="1" required>
                </div>
                <div class="form-group">
                    <label>Classes Attended</label>
                    <input type="number" name="attended" min="0" required>
                </div>
            </div>
            <button type="submit" class="btn-primary">Save Attendance</button>
        </form>
    </div>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Attended</th><th>Total</th><th>Percentage</th></tr>
            </thead>
            <tbody id="attendanceTable"></tbody>
        </table>
    </div>
</div>
<script>
var students   = ${students};
var attendance = ${attendance};
var ss = document.getElementById("studentSelect");
students.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });
var tbody = document.getElementById("attendanceTable");
if (attendance.length === 0) {
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No records yet.</td></tr>";
} else {
    attendance.forEach(function(a) {
        var flag = parseFloat(a.percentage) < 75 ? " ⚠️" : "";
        tbody.innerHTML += "<tr><td>" + a.name + "</td><td>" + a.attended + "</td><td>" +
            a.total_classes + "</td><td>" + a.percentage + "%" + flag + "</td></tr>";
    });
}
</script>
</body>
</html>
