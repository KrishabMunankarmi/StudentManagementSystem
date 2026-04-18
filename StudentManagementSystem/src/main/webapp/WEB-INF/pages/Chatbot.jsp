<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Chatbot</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/Chatbot.css">
</head>
<body>

    <!-- Navbar -->
    <div class="navbar">
        <div class="nav-brand">SMS AI Assistant</div>
        <div class="nav-links">
            <span style="font-weight:500; color:#2563eb;">
                Hello, ${sessionScope.fullName != null ? sessionScope.fullName.split(' ')[0] : 'Student'}!
            </span>
            <a href="${pageContext.request.contextPath}/home">&#8592; Back to Dashboard</a>
        </div>
    </div>

    <!-- Chat Interface -->
    <div class="container chat-container">

        <!-- Chat History Sidebar -->
        <div class="chat-history">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
                <h4>Previous Chats</h4>
                <button class="btn" style="padding:0.3rem 0.7rem; font-size:0.8rem;" onclick="newChat()">+ New</button>
            </div>
            <div id="chatHistoryList"></div>
        </div>

        <!-- Main Chat Area -->
        <div class="chat-main">
            <div class="chat-messages" id="chatMessages">
                <div class="bubble bubble-bot">
                    <strong>Bot:</strong> Hello, ${sessionScope.fullName != null ? sessionScope.fullName.split(' ')[0] : 'there'}!
                    How can I help you with your studies today?
                    You can also upload a PDF or DOCX file and I'll summarize it for you!
                </div>
            </div>

            <!-- File Upload Panel (for summarizer) -->
            <div class="upload-panel" id="uploadPanel">
                <div class="upload-panel-header">
                    <span>Summarize a Note / Document</span>
                    <button class="upload-close-btn" onclick="toggleUploadPanel()">&#10005;</button>
                </div>
                <div class="upload-panel-body">
                    <label for="fileInput" class="file-drop-area" id="dropArea">
                        <span id="dropLabel">Click to choose or drag &amp; drop a PDF / DOCX file</span>
                        <input type="file" id="fileInput" accept=".pdf,.docx" onchange="onFileSelected(this)">
                    </label>
                    <div class="upload-options">
                        <span class="upload-options-label">Summary style:</span>
                        <label><input type="radio" name="summaryStyle" value="brief" checked> Brief</label>
                        <label><input type="radio" name="summaryStyle" value="bullet"> Bullet Points</label>
                        <label><input type="radio" name="summaryStyle" value="detailed"> Detailed</label>
                    </div>
                    <button class="btn btn-summarize" id="summarizeBtn" onclick="summarizeFile()" disabled>
                        &#10003; Summarize
                    </button>
                </div>
            </div>

            <!-- Input Row -->
            <div class="chat-input-box">
                <button class="btn btn-attach" title="Summarize a document" onclick="toggleUploadPanel()">
                    &#128206;
                </button>
                <input type="text" id="messageInput" placeholder="Type your question here..."
                       onkeydown="if(event.key === 'Enter') sendMessage()">
                <button class="btn" onclick="sendMessage()">Send</button>
            </div>
        </div>
    </div>

    <script>
    var currentSessionId  = null;
    var chatSessions      = ${chatSessions != null ? chatSessions : '[]'};
    var submitState       = null;
    var submitAssignmentId = null;

    window.onload = function() { renderChatHistory(); };

    // Sidebar
    function renderChatHistory() {
        var list = document.getElementById("chatHistoryList");
        list.innerHTML = "";
        if (chatSessions.length === 0) {
            list.innerHTML = "<small style='color:#94a3b8;'>No previous chats yet.</small>";
            return;
        }
        chatSessions.forEach(function(s) {
            var div = document.createElement("div");
            div.className = "chat-history-item" + (currentSessionId === s.session_id ? " active" : "");
            var info = document.createElement("div");
            info.className = "chat-history-info";
            info.innerHTML = "<strong>" + escapeHtml(s.title) + "</strong><br><small>" + formatDate(s.started_at) + "</small>";
            info.onclick = function() { loadSession(s.session_id); };
            var delBtn = document.createElement("button");
            delBtn.className = "chat-delete-btn";
            delBtn.innerHTML = "&#128465;";
            delBtn.title = "Delete chat";
            delBtn.onclick = function(e) { e.stopPropagation(); deleteSession(s.session_id); };
            div.appendChild(info);
            div.appendChild(delBtn);
            list.appendChild(div);
        });
    }

    function newChat() {
        currentSessionId = null;
        submitState = null;
        submitAssignmentId = null;
        document.getElementById("chatMessages").innerHTML =
            '<div class="bubble bubble-bot"><strong>Bot:</strong> Hello! How can I help you today?</div>';
        renderChatHistory();
    }

    function loadSession(sessionId) {
        currentSessionId = sessionId;
        renderChatHistory();
        var chatBox = document.getElementById("chatMessages");
        chatBox.innerHTML = '<div class="bubble bubble-bot"><strong>Bot:</strong> <span class="thinking">Loading chat...</span></div>';
        fetch("${pageContext.request.contextPath}/chatHistory?session_id=" + sessionId)
        .then(function(res) { return res.json(); })
        .then(function(messages) {
            chatBox.innerHTML = "";
            messages.forEach(function(m) {
                var type  = m.sender === "user" ? "user" : "bot";
                var label = m.sender === "user" ? "<strong>You:</strong> " : "<strong>Bot:</strong> ";
                addBubble(label + formatBotMessage(escapeHtml(m.message)), type);
            });
        })
        .catch(function() {
            chatBox.innerHTML = '<div class="bubble bubble-bot" style="color:red;">Could not load chat.</div>';
        });
    }

    function deleteSession(sessionId) {
        if (!confirm("Delete this chat? This cannot be undone.")) return;
        fetch("${pageContext.request.contextPath}/chatHistory?session_id=" + sessionId, { method: "DELETE" })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                if (currentSessionId === sessionId) {
                    currentSessionId = null;
                    document.getElementById("chatMessages").innerHTML =
                        '<div class="bubble bubble-bot"><strong>Bot:</strong> Hello! How can I help you today?</div>';
                }
                chatSessions = chatSessions.filter(function(s) { return s.session_id !== sessionId; });
                renderChatHistory();
            }
        });
    }

    // Chat
    function sendMessage() {
        var input   = document.getElementById("messageInput");
        var message = input.value.trim();
        if (!message) return;

        addBubble("<strong>You:</strong> " + escapeHtml(message), "user");
        var loadingId = "loading-" + Date.now();
        addBubble("<strong>Bot:</strong> <span class='thinking'>Thinking...</span>", "bot", loadingId);
        input.value = "";

        var body = "message=" + encodeURIComponent(message);
        if (currentSessionId) body += "&session_id=" + currentSessionId;

        fetch("${pageContext.request.contextPath}/chatbot", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var el = document.getElementById(loadingId);

            //Update submission state
            submitState        = data.submit_state || null;
            submitAssignmentId = data.submit_assignment_id || null;

            var replyText = data.reply;

            //Check if bot wants to show upload button for assignment submission
            var showUpload = replyText.indexOf("__SHOW_UPLOAD_BUTTON__") !== -1;
            replyText = replyText.replace("__SHOW_UPLOAD_BUTTON__", "");

            if (el) {
                var html = "<strong>Bot:</strong> " + formatBotMessage(replyText);
                if (showUpload && submitAssignmentId) {
                    html += buildAssignmentUploadButton(submitAssignmentId);
                }
                el.innerHTML = html;
            }

            if (!currentSessionId && data.session_id) {
                currentSessionId = data.session_id;
                refreshSidebar();
            }
            document.getElementById("chatMessages").scrollTop = 9999;
        })
        .catch(function() {
            var el = document.getElementById(loadingId);
            if (el) el.innerHTML = "<strong>Bot:</strong> <span style='color:red;'>Could not reach server.</span>";
        });
    }

    //Build the file upload button for assignment submission inside chat
    function buildAssignmentUploadButton(assignId) {
        var uid = "assign-upload-" + Date.now();
        return (
            "<div style='margin-top:0.75rem; padding:0.75rem; background:#f0fdf4; border:1px solid #bbf7d0; border-radius:8px;'>" +
            "<label for='" + uid + "' style='cursor:pointer; color:#16a34a; font-weight:600; font-size:0.9rem;'>" +
            "📎 Click to upload your submission file (PDF or DOCX)</label><br>" +
            "<input type='file' id='" + uid + "' accept='.pdf,.docx' style='margin-top:0.5rem;' " +
            "onchange='submitAssignmentFile(this, " + assignId + ")'>" +
            "</div>"
        );
    }

    //Submit assignment file from chat
    function submitAssignmentFile(input, assignId) {
        if (!input.files || !input.files[0]) return;

        var file      = input.files[0];
        var loadingId = "file-submit-" + Date.now();
        addBubble("<strong>Bot:</strong> <span class='thinking'>Uploading your submission...</span>", "bot", loadingId);

        var formData = new FormData();
        formData.append("file", file);
        formData.append("assignment_id", assignId);

        fetch("${pageContext.request.contextPath}/chatsubmit", {
            method: "POST",
            body: formData
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var el = document.getElementById(loadingId);
            if (data.success) {
                if (el) el.innerHTML = "<strong>Bot:</strong> " + escapeHtml(data.message);
                submitState        = null;
                submitAssignmentId = null;
                //Remove all upload buttons from chat so student cannot resubmit
                var uploadBtns = document.querySelectorAll("[id^='assign-upload-']");
                uploadBtns.forEach(function(btn) {
                    btn.closest("div[style*='background:#f0fdf4']").remove();
                });
            } else {
                if (el) el.innerHTML = "<strong>Bot:</strong> <span style='color:red;'>" + escapeHtml(data.message) + "</span>";
            }
            document.getElementById("chatMessages").scrollTop = 9999;
        })
        .catch(function() {
            var el = document.getElementById(loadingId);
            if (el) el.innerHTML = "<strong>Bot:</strong> <span style='color:red;'>Upload failed. Please try again.</span>";
        });
    }

    function refreshSidebar() {
        fetch("${pageContext.request.contextPath}/chatSessions")
        .then(function(res) { return res.json(); })
        .then(function(data) { chatSessions = data; renderChatHistory(); });
    }

    // Summarizer
    var FLASK_URL = "http://localhost:5000";

    function toggleUploadPanel() {
        document.getElementById("uploadPanel").classList.toggle("upload-panel-visible");
    }

    function onFileSelected(input) {
        var btn = document.getElementById("summarizeBtn");
        if (input.files && input.files[0]) {
            document.getElementById("dropLabel").textContent = input.files[0].name;
            btn.disabled = false;
        } else {
            document.getElementById("dropLabel").textContent = "Click to choose or drag & drop a PDF / DOCX file";
            btn.disabled = true;
        }
    }

    function summarizeFile() {
        var fileInput = document.getElementById("fileInput");
        var style     = document.querySelector('input[name="summaryStyle"]:checked').value;
        if (!fileInput.files || !fileInput.files[0]) { alert("Please select a file first."); return; }

        var file = fileInput.files[0];
        var btn  = document.getElementById("summarizeBtn");
        btn.disabled = true;
        btn.textContent = "Summarizing...";
        toggleUploadPanel();

        var loadingId = "sum-" + Date.now();
        addBubble("<strong>You:</strong> Uploaded <em>" + escapeHtml(file.name) + "</em> - summarize (" + style + ")", "user");
        addBubble("<strong>Bot:</strong> <span class='thinking'>Reading your document...</span>", "bot", loadingId);

        var formData = new FormData();
        formData.append("file", file);
        formData.append("style", style);

        fetch(FLASK_URL + "/summarize", { method: "POST", body: formData })
        .then(function(res) { return res.json().then(function(d) { return { ok: res.ok, data: d }; }); })
        .then(function(result) {
            var el = document.getElementById(loadingId);
            if (!result.ok) {
                if (el) el.innerHTML = "<strong>Bot:</strong> <span style='color:red;'>Error: " + escapeHtml(result.data.error) + "</span>";
                return;
            }
            var d = result.data;
            if (el) el.innerHTML =
                "<strong>Bot:</strong> <strong>Summary of " + escapeHtml(d.filename) + "</strong>" +
                " <small style='color:#64748b;'>(" + d.word_count + " words, " + d.style + ")</small>" +
                "<hr style='margin:0.5rem 0; border-color:#e2e8f0;'>" +
                d.summary.replace(/\n/g, "<br>");
            document.getElementById("chatMessages").scrollTop = 9999;
        })
        .catch(function() {
            var el = document.getElementById(loadingId);
            if (el) el.innerHTML = "<strong>Bot:</strong> <span style='color:red;'>Could not reach the summarizer.</span>";
        })
        .finally(function() {
            fileInput.value = "";
            document.getElementById("dropLabel").textContent = "Click to choose or drag & drop a PDF / DOCX file";
            btn.disabled = true;
            btn.textContent = "Summarize";
        });
    }

    // Helpers
    function formatBotMessage(text) {
        //Convert markdown bold **text** to <strong>
        text = text.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
        //Convert newlines to <br>
        text = text.replace(/\n/g, "<br>");
        //Convert [text](url) to clickable links
        text = text.replace(/\[([^\]]+)\]\(([^)]+)\)/g, "<a href='$2' style='color:#2563eb;'>$1</a>");
        return text;
    }

    function addBubble(innerHTML, type, id) {
        var chatBox = document.getElementById("chatMessages");
        var div = document.createElement("div");
        div.className = "bubble " + (type === "user" ? "bubble-user" : "bubble-bot");
        div.innerHTML = innerHTML;
        if (id) div.id = id;
        chatBox.appendChild(div);
        chatBox.scrollTop = chatBox.scrollHeight;
        return div;
    }

    function escapeHtml(text) {
        if (!text) return "";
        return text.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
    }

    function formatDate(dateStr) {
        if (!dateStr) return "";
        var d   = new Date(dateStr);
        var now = new Date();
        var diff = Math.floor((now - d) / 86400000);
        if (diff === 0) return "Today, " + d.getHours() + ":" + String(d.getMinutes()).padStart(2, "0");
        if (diff === 1) return "Yesterday";
        if (diff < 7)  return diff + " days ago";
        return d.toLocaleDateString();
    }
    </script>

</body>
</html>
