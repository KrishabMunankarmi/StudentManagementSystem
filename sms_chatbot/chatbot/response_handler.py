from db.database import execute_query
from datetime import date


def get_response(intent: str, student_id: int) -> str:
    handlers = {
        'attendance':        _attendance,
        'grades':            _grades,
        'timetable':         _timetable,
        'exams':             _exams,
        'assignments':       _assignments,
        'submit_assignment': _submit_assignment,
        'teacher':           _teachers,
        'fees':              _fees,
        'cgpa':              _cgpa,
        'failing':           _failing,
        'low_attendance':    _low_attendance,
        'greeting':          _greeting,
        'unknown':           _unknown,
    }
    handler = handlers.get(intent, _unknown)
    return handler(student_id)


#Individual handlers

def _attendance(student_id):
    rows = execute_query("""
        SELECT s.subject_name,
               a.attended,
               a.total_classes,
               ROUND((a.attended / a.total_classes) * 100, 1) AS percentage
        FROM   attendance a
        JOIN   subjects   s ON a.subject_id = s.subject_id
        WHERE  a.student_id = %s
    """, (student_id,))

    if not rows:
        return "No attendance records found for your account."

    lines = ["📋 **Your Attendance:**\n"]
    for r in rows:
        flag = " ⚠️ Low!" if r['percentage'] < 75 else ""
        lines.append(
            f"• {r['subject_name']}: {r['attended']}/{r['total_classes']} "
            f"classes ({r['percentage']}%){flag}"
        )
    return '\n'.join(lines)


def _grades(student_id):
    rows = execute_query("""
        SELECT s.subject_name, g.marks, g.grade, g.semester
        FROM   grades   g
        JOIN   subjects s ON g.subject_id = s.subject_id
        WHERE  g.student_id = %s
        ORDER  BY g.semester DESC
    """, (student_id,))

    if not rows:
        return "No grade records found."

    lines = ["📊 **Your Grades:**\n"]
    for r in rows:
        lines.append(
            f"• {r['subject_name']} [{r['semester']}]: "
            f"{r['marks']} marks — Grade {r['grade']}"
        )
    return '\n'.join(lines)


def _timetable(student_id):
    today = date.today().strftime('%A')
    rows = execute_query("""
        SELECT s.subject_name, t.day_of_week,
               t.start_time, t.end_time, t.room
        FROM   timetable t
        JOIN   subjects  s ON t.subject_id = s.subject_id
        WHERE  t.student_id = %s
        ORDER  BY FIELD(t.day_of_week,
                        'Monday','Tuesday','Wednesday','Thursday','Friday'),
                  t.start_time
    """, (student_id,))

    if not rows:
        return "No timetable found."

    lines = [f"📅 **Your Timetable** (today is {today}):\n"]
    current_day = None
    for r in rows:
        if r['day_of_week'] != current_day:
            current_day = r['day_of_week']
            marker = " ← today" if current_day == today else ""
            lines.append(f"\n**{current_day}{marker}**")
        lines.append(
            f"  {str(r['start_time'])[:-3]}–{str(r['end_time'])[:-3]}  "
            f"{r['subject_name']}  (Room {r['room']})"
        )
    return '\n'.join(lines)


def _exams(student_id):
    rows = execute_query("""
        SELECT s.subject_name, e.exam_date,
               e.start_time, e.room, e.exam_type
        FROM   exams    e
        JOIN   subjects s ON e.subject_id = s.subject_id
        JOIN   grades   g ON g.subject_id = e.subject_id
                          AND g.student_id = %s
        WHERE  e.exam_date >= CURDATE()
        ORDER  BY e.exam_date, e.start_time
        LIMIT  10
    """, (student_id,))

    if not rows:
        return "No upcoming exams found."

    lines = ["📝 **Upcoming Exams:**\n"]
    for r in rows:
        lines.append(
            f"• {r['subject_name']} ({r['exam_type']})  "
            f"— {r['exam_date'].strftime('%d %b %Y')} at "
            f"{str(r['start_time'])[:-3]}  Room {r['room']}"
        )
    return '\n'.join(lines)


def _assignments(student_id):
    rows = execute_query("""
        SELECT s.subject_name, a.title, a.due_date
        FROM   assignments a
        JOIN   subjects    s ON a.subject_id = s.subject_id
        WHERE  a.student_id = %s
          AND  a.submitted  = FALSE
          AND  a.due_date  >= CURDATE()
        ORDER  BY a.due_date
    """, (student_id,))

    if not rows:
        return "🎉 No pending assignments — you're all caught up!"

    lines = ["📌 **Pending Assignments:**\n"]
    for r in rows:
        days_left = (r['due_date'] - date.today()).days
        urgency = " 🔴 Due soon!" if days_left <= 2 else ""
        lines.append(
            f"• [{r['subject_name']}] {r['title']} "
            f"— due {r['due_date'].strftime('%d %b')} "
            f"({days_left}d left){urgency}"
        )
    return '\n'.join(lines)


def _submit_assignment(student_id):
    #Return a trigger word that ChatController.java detects
    #to start the interactive submission flow with file upload
    rows = execute_query("""
        SELECT a.id, a.title, s.subject_name, a.due_date
        FROM   assignments a
        JOIN   subjects    s ON a.subject_id = s.subject_id
        WHERE  a.student_id = %s
          AND  a.submitted  = FALSE
          AND  a.due_date  >= CURDATE()
        ORDER  BY a.due_date ASC
    """, (student_id,))

    if not rows:
        return "🎉 You have no pending assignments to submit!"

    #Return trigger keyword so ChatController starts the submission flow
    return "submitassignment"


def _teachers(student_id):
    rows = execute_query("""
        SELECT DISTINCT s.subject_name, t.name AS teacher_name
        FROM   timetable tt
        JOIN   subjects  s  ON tt.subject_id = s.subject_id
        JOIN   teachers  t  ON t.subject_id  = s.subject_id
        WHERE  tt.student_id = %s
        ORDER  BY s.subject_name
    """, (student_id,))

    if not rows:
        return "No teacher information found."

    lines = ["👨‍🏫 **Your Teachers:**\n"]
    for r in rows:
        lines.append(f"• {r['subject_name']}: {r['teacher_name']}")
    return '\n'.join(lines)


def _fees(student_id):
    rows = execute_query("""
        SELECT amount, due_date, paid
        FROM   fees
        WHERE  student_id = %s
        ORDER  BY due_date DESC
    """, (student_id,))

    if not rows:
        return "No fee records found."

    lines = ["💳 **Fee Status:**\n"]
    for r in rows:
        status = "✅ Paid" if r['paid'] else "❌ Unpaid"
        lines.append(
            f"• NPR {r['amount']:.2f} — due {r['due_date'].strftime('%d %b %Y')}  {status}"
        )
    return '\n'.join(lines)


def _cgpa(student_id):
    rows = execute_query("""
        SELECT s.subject_name, g.marks, g.grade
        FROM   grades   g
        JOIN   subjects s ON g.subject_id = s.subject_id
        WHERE  g.student_id = %s
    """, (student_id,))

    if not rows:
        return "No grade records found to calculate CGPA."

    total_marks = sum(r['marks'] for r in rows)
    avg_marks   = total_marks / len(rows)

    if avg_marks >= 90:   gpa = 4.0
    elif avg_marks >= 80: gpa = 3.7
    elif avg_marks >= 75: gpa = 3.3
    elif avg_marks >= 70: gpa = 3.0
    elif avg_marks >= 65: gpa = 2.7
    elif avg_marks >= 60: gpa = 2.3
    elif avg_marks >= 55: gpa = 2.0
    elif avg_marks >= 50: gpa = 1.7
    else:                 gpa = 0.0

    lines = ["🎓 **Your Academic Performance:**\n"]
    lines.append(f"• Average Marks: {avg_marks:.1f}%")
    lines.append(f"• Estimated CGPA: {gpa:.1f} / 4.0\n")
    lines.append("**Subject Breakdown:**")
    for r in rows:
        lines.append(f"• {r['subject_name']}: {r['marks']} marks — Grade {r['grade']}")
    return '\n'.join(lines)


def _failing(student_id):
    rows = execute_query("""
        SELECT s.subject_name, g.marks, g.grade
        FROM   grades   g
        JOIN   subjects s ON g.subject_id = s.subject_id
        WHERE  g.student_id = %s
        ORDER  BY g.marks ASC
    """, (student_id,))

    if not rows:
        return "No grade records found."

    failing = [r for r in rows if r['marks'] < 50]
    at_risk = [r for r in rows if 50 <= r['marks'] < 60]

    if not failing and not at_risk:
        return "🎉 Great news! You are passing all your subjects!"

    lines = ["⚠️ **Academic Alert:**\n"]

    if failing:
        lines.append("**Failing subjects (below 50%):**")
        for r in failing:
            lines.append(f"• {r['subject_name']}: {r['marks']} marks — Grade {r['grade']} ❌")

    if at_risk:
        lines.append("\n**At risk subjects (50-59%):**")
        for r in at_risk:
            lines.append(f"• {r['subject_name']}: {r['marks']} marks — Grade {r['grade']} ⚠️")

    lines.append("\nPlease speak to your lecturer or academic advisor for support.")
    return '\n'.join(lines)


def _low_attendance(student_id):
    rows = execute_query("""
        SELECT s.subject_name,
               a.attended,
               a.total_classes,
               ROUND((a.attended / a.total_classes) * 100, 1) AS percentage
        FROM   attendance a
        JOIN   subjects   s ON a.subject_id = s.subject_id
        WHERE  a.student_id = %s
          AND  (a.attended / a.total_classes) * 100 < 75
        ORDER  BY percentage ASC
    """, (student_id,))

    if not rows:
        return "✅ Good news! Your attendance is above 75% in all subjects."

    lines = ["⚠️ **Low Attendance Warning:**\n"]
    lines.append("The following subjects have attendance below 75%:\n")
    for r in rows:
        lines.append(
            f"• {r['subject_name']}: {r['attended']}/{r['total_classes']} "
            f"classes ({r['percentage']}%) ⚠️"
        )
    lines.append("\n⚠️ You may be at risk of being barred from exams.")
    lines.append("Please attend classes regularly.")
    return '\n'.join(lines)


def _greeting(student_id):
    rows = execute_query(
        "SELECT name FROM students WHERE student_id = %s", (student_id,)
    )
    name = rows[0]['name'].split()[0] if rows else "there"
    return (
        f"👋 Hello, {name}! I'm your SMS assistant.\n\n"
        "You can ask me about:\n"
        "• Attendance  • Grades  • Timetable\n"
        "• Exams  • Assignments  • Teachers  • Fees\n"
        "• CGPA  • Failing Subjects  • Low Attendance\n\n"
        "You can also say 'submit my assignment' to upload your work!"
    )


def _unknown(_):
    return (
        "I'm not sure I understood that. Try asking:\n"
        "• 'What is my attendance?'\n"
        "• 'Show my grades'\n"
        "• 'When is my next exam?'\n"
        "• 'What assignments are due?'\n"
        "• 'What is my CGPA?'\n"
        "• 'Which subjects am I failing?'\n"
        "• 'Submit my assignment'"
    )