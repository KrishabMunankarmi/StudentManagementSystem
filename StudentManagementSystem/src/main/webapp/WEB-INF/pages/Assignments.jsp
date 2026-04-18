<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>My Assignments - Student Management System</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/Assignments.css">
</head>
<body>
<jsp:include page="Header.jsp"/>

<main class="assignments-container">
    <div class="assignments-header">
        <h1>📌 My Assignments</h1>
        <p class="subtitle">View your pending and submitted assignments. Upload your work directly from here.</p>
    </div>

    <% if(request.getAttribute("success") != null) { %>
        <div class="alert-success">✅ ${success}</div>
    <% } %>
    <% if(request.getAttribute("error") != null) { %>
        <div class="alert-error">❌ ${error}</div>
    <% } %>

    <!-- Pending Assignments -->
    <div class="section-card">
        <h2>⏳ Pending Assignments</h2>
        <div id="pendingList"></div>
    </div>

    <!-- Submit Assignment Form -->
    <div class="section-card" id="submitSection" style="display:none;">
        <h2>📤 Submit Assignment</h2>
        <form action="${pageContext.request.contextPath}/submitassignment"
              method="post" enctype="multipart/form-data" class="submit-form">
            <input type="hidden" name="assignment_id" id="selectedAssignmentId">

            <div class="selected-info" id="selectedInfo"></div>

            <div class="file-upload-area">
                <label for="submissionFile">
                    <div class="upload-icon">📎</div>
                    <div id="fileLabel">Click to choose your submission file (PDF or DOCX)</div>
                    <small>Maximum file size: 10MB</small>
                </label>
                <input type="file" id="submissionFile" name="submission_file"
                       accept=".pdf,.docx" onchange="onFileSelected(this)">
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-submit" id="submitBtn" disabled>
                    ✓ Submit Assignment
                </button>
                <button type="button" class="btn-cancel" onclick="cancelSubmit()">
                    Cancel
                </button>
            </div>
        </form>
    </div>

    <!-- Submitted Assignments -->
    <div class="section-card">
        <h2>✅ Submitted Assignments</h2>
        <div id="submittedList"></div>
    </div>
</main>

<script>
var pending   = ${pendingAssignments};
var submitted = ${submittedAssignments};
var contextPath = "${pageContext.request.contextPath}";

//Render pending assignments
var pendingDiv = document.getElementById("pendingList");
if (pending.length === 0) {
    pendingDiv.innerHTML = "<div class='empty-state'>🎉 You have no pending assignments — you're all caught up!</div>";
} else {
    pending.forEach(function(a) {
        var daysLeft = a.days_left;
        var urgent   = daysLeft <= 2 ? "<span class='badge urgent'>Due soon!</span>" : "";
        pendingDiv.innerHTML +=
            "<div class='assignment-card pending'>" +
            "<div class='assignment-info'>" +
            "<div class='assignment-title'>" + a.title + urgent + "</div>" +
            "<div class='assignment-meta'>" +
            "<span>📚 " + a.subject_name + "</span>" +
            "<span>📅 Due: " + a.due_date + " (" + daysLeft + " days left)</span>" +
            "</div></div>" +
            "<button class='btn-select' onclick='selectAssignment(" + a.id + ", \"" + a.title.replace(/"/g, '\\"') + "\", \"" + a.subject_name + "\")'>Submit</button>" +
            "</div>";
    });
}

//Render submitted assignments
var submittedDiv = document.getElementById("submittedList");
if (submitted.length === 0) {
    submittedDiv.innerHTML = "<div class='empty-state'>No submitted assignments yet.</div>";
} else {
    submitted.forEach(function(a) {
        var viewBtn = a.has_file
            ? "<a href='" + contextPath + "/viewsubmission?id=" + a.id + "' target='_blank' class='btn-view'>📄 View Submission</a>"
            : "<span style='color:#94a3b8; font-size:0.85rem;'>No file attached</span>";

        submittedDiv.innerHTML +=
            "<div class='assignment-card submitted'>" +
            "<div class='assignment-info'>" +
            "<div class='assignment-title'>✅ " + a.title + "</div>" +
            "<div class='assignment-meta'>" +
            "<span>📚 " + a.subject_name + "</span>" +
            "<span>📅 Submitted: " + (a.submitted_at || "N/A") + "</span>" +
            "</div></div>" +
            viewBtn +
            "</div>";
    });
}

function selectAssignment(id, title, subject) {
    document.getElementById("selectedAssignmentId").value = id;
    document.getElementById("selectedInfo").innerHTML =
        "<strong>Selected:</strong> " + title + " (" + subject + ")";
    document.getElementById("submitSection").style.display = "block";
    document.getElementById("submitSection").scrollIntoView({ behavior: "smooth" });
    document.getElementById("submissionFile").value = "";
    document.getElementById("fileLabel").textContent = "Click to choose your submission file (PDF or DOCX)";
    document.getElementById("submitBtn").disabled = true;
}

function cancelSubmit() {
    document.getElementById("submitSection").style.display = "none";
}

function onFileSelected(input) {
    if (input.files && input.files[0]) {
        document.getElementById("fileLabel").textContent = "📎 " + input.files[0].name;
        document.getElementById("submitBtn").disabled = false;
    }
}
</script>

<jsp:include page="Footer.jsp"/>
</body>
</html>
