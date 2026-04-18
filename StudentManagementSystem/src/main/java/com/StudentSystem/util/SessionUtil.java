package com.StudentSystem.util;

import com.StudentSystem.model.StudentSystemModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    // Constant key used to store/retrieve the logged-in employee object in session
    public static final String SESSION_EMPLOYEE = "loggedInEmployee";

    /**
     * Stores an attribute in the current session associated with the request.
     * If no session exists, a new one is created.
     *
     * @param request the HttpServletRequest
     * @param key     the session attribute key
     * @param value   the attribute value to store
     */
    public static void setAttribute(HttpServletRequest request, String key, Object value) {
        HttpSession session = request.getSession();  // get or create session
        session.setAttribute(key, value);
    }

    /**
     * Retrieves an attribute from the current session.
     * Returns null if no session exists or if attribute is not present.
     *
     * @param request the HttpServletRequest
     * @param key     the session attribute key
     * @return the attribute value or null
     */
    public static Object getAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false); // get session if exists, else null
        return (session != null) ? session.getAttribute(key) : null;
    }

    /**
     * Removes an attribute from the current session if the session exists.
     *
     * @param request the HttpServletRequest
     * @param key     the session attribute key to remove
     */
    public static void removeAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false); // get session if exists
        if (session != null) {
            session.removeAttribute(key);
        }
    }

    /**
     * Invalidates the current session if it exists, effectively logging out the user.
     *
     * @param request the HttpServletRequest
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // get session if exists
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Retrieves the currently logged-in employee object from the session.
     * Returns null if no logged-in employee is found or type mismatch occurs.
     *
     * @param request the HttpServletRequest
     * @return the logged-in EmployeeSystemModel or null if none found
     */
    public static StudentSystemModel getLoggedInEmployee(HttpServletRequest request) {
        Object obj = getAttribute(request, SESSION_EMPLOYEE);
        return (obj instanceof StudentSystemModel) ? (StudentSystemModel) obj : null;
    }
}
