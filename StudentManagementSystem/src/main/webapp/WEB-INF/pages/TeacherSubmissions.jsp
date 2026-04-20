<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Submissions</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="TeacherNav.jsp"/>
<div class="admin-content">
    <h1>📄 Student Submissions</h1>
    <p style="color:#64748b; margin-bottom:1.5rem;">All submitted assignments for your subject.</p>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Assignment</th><th>Submitted At</th><th>File</th></tr>
            </thead>
            <tbody id="submissionsTable"></tbody>
        </table>
    </div>
</div>
<script>
var submissions = ${submissions};
var tbody = document.getElementById("submissionsTable");
if (submissions.length === 0) {
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No submissions yet.</td></tr>";
} else {
    submissions.forEach(function(s) {
        var viewBtn = s.submission_file
            ? "<a href='${pageContext.request.contextPath}/viewsubmission?id=" + s.id + "' target='_blank' class='btn-secondary' style='font-size:0.85rem;'>📄 View File</a>"
            : "<span style='color:#94a3b8;'>No file</span>";
        tbody.innerHTML += "<tr><td>" + s.name + "</td><td>" + s.title + "</td><td>" +
            (s.submitted_at || "N/A") + "</td><td>" + viewBtn + "</td></tr>";
    });
}
</script>
</body>
</html>
