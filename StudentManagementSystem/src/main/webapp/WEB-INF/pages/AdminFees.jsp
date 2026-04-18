<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Fees</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminPanel.css">
</head>
<body>
<jsp:include page="AdminNav.jsp"/>
<div class="admin-content">
    <h1>Manage Fees</h1>

    <div class="form-card">
        <h2>Add Fee Record</h2>
        <form method="post" action="${pageContext.request.contextPath}/adminfeespage" class="admin-form">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Student</label>
                    <select name="student_id" required id="studentSelect"></select>
                </div>
                <div class="form-group">
                    <label>Amount (NPR)</label>
                    <input type="number" name="amount" min="0" step="0.01" required>
                </div>
                <div class="form-group">
                    <label>Due Date</label>
                    <input type="date" name="due_date" required>
                </div>
                <div class="form-group">
                    <label>Status</label>
                    <select name="paid">
                        <option value="false">Unpaid</option>
                        <option value="true">Paid</option>
                    </select>
                </div>
            </div>
            <button type="submit" class="btn-primary">Add Fee</button>
        </form>
    </div>

    <div class="table-container">
        <table class="admin-table">
            <thead>
                <tr><th>Student</th><th>Amount</th><th>Due Date</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody id="feesTable"></tbody>
        </table>
    </div>
</div>

<script>
var students = ${studentsList};
var fees     = ${fees};

var ss = document.getElementById("studentSelect");
students.forEach(function(s) { ss.innerHTML += "<option value='" + s.id + "'>" + s.name + "</option>"; });

var tbody = document.getElementById("feesTable");
if (fees.length === 0) {
    tbody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>No fee records found.</td></tr>";
} else {
    fees.forEach(function(f) {
        var status = f.paid ? "✅ Paid" : "❌ Unpaid";
        var markPaid = !f.paid ?
            "<form method='post' action='${pageContext.request.contextPath}/adminfeespage' style='display:inline'>" +
            "<input type='hidden' name='action' value='markpaid'>" +
            "<input type='hidden' name='id' value='" + f.id + "'>" +
            "<button class='btn-secondary' style='margin-right:4px'>Mark Paid</button></form>" : "";
        tbody.innerHTML += "<tr><td>" + f.name + "</td><td>NPR " + f.amount + "</td><td>" +
            f.due_date + "</td><td>" + status + "</td><td>" + markPaid +
            "<form method='post' action='${pageContext.request.contextPath}/adminfeespage' style='display:inline'>" +
            "<input type='hidden' name='action' value='delete'>" +
            "<input type='hidden' name='id' value='" + f.id + "'>" +
            "<button class='btn-delete' onclick=\"return confirm('Delete?')\">Delete</button></form></td></tr>";
    });
}
</script>
</body>
</html>
