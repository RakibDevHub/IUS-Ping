package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import com.rakibdevhub.iusping.model.StudentModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardServlet.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String schema = DatabaseConfig.getSchema();

    // Precompiled validation patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern BATCH_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[0-9]{9}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object studentIdObj = session.getAttribute("id");
        if (studentIdObj == null) {
            logger.error("Session 'id' is null. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String studentId = studentIdObj.toString();
        processStudentData(request, response, studentId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        switch (action) {
            case "updateProfile" ->
                updateProfile(request, response);
            case "updateNumber" ->
                updatePhoneNumber(request, response);
            case "updatePassword" ->
                updatePassword(request, response);
            default ->
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void processStudentData(HttpServletRequest request, HttpServletResponse response, String studentId)
            throws ServletException, IOException {
        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schema + ".student_list_view WHERE id = ?")) {

            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StudentModel student = new StudentModel(
                            rs.getInt("id"),
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("batch"),
                            rs.getString("department"),
                            null
                    );
                    request.setAttribute("student", student);
                    request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Student not found.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
            }

        } catch (SQLException e) {
            logger.error("Database error retrieving student data.", e);
            request.setAttribute("error", "Database error occurred.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String batch = request.getParameter("batch");

        if (!validateInput(request, studentId, name, department, batch)) {
            // Set the updated data to request before forwarding to the JSP
            request.setAttribute("student", new StudentModel(
                    Integer.parseInt(studentId), studentId, name, batch, department, null));
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        department = department.toUpperCase(); // Convert department to uppercase

        // Fetch the original student data from the database
        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE " + schema + ".student_list_view SET student_id = ?, name = ?, department = ?, batch = ? WHERE id = ?")) {

            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, batch);
            stmt.setInt(5, (Integer) request.getSession().getAttribute("id"));

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                request.setAttribute("success", "Profile updated successfully!");
                // Fetch the updated student data from the database and pass it to the JSP
                processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id"))); //only on success
            } else {
                request.setAttribute("error", "Failed to update profile.");
                processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
            }

        } catch (SQLException e) {
            logger.error("Database error updating student profile.", e);
            request.setAttribute("error", "Database error occurred.");
            // Fetch the original student data from the database
            processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
        }

        request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
    }

    private void updatePhoneNumber(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String phoneNumber = request.getParameter("newNumber");

        if (phoneNumber == null || phoneNumber.trim().isEmpty() || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            request.setAttribute("error", "Invalid phone number. Must be 11 digits and start with 01.");
            // Fetch the original student data from the database
            processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        int studentId = (Integer) request.getSession().getAttribute("id");

        try (Connection conn = DatabaseConfig.getConnectionMaster(); PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE " + schema + ".student SET phone_number = ? WHERE id = ?")) {

            updateStmt.setString(1, phoneNumber);
            updateStmt.setInt(2, studentId);
            updateStmt.executeUpdate();
            request.setAttribute("success", "Phone number updated successfully!");
        } catch (SQLException e) {
            logger.error("Database error updating phone number.", e);
            request.setAttribute("error", "Database error occurred.");
        }

        // Fetch the updated student data from the database and pass it to the JSP
        processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));

        request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
    }

    private void updatePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null
                || currentPassword.trim().isEmpty() || newPassword.trim().isEmpty()
                || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            // Fetch the original student data from the database
            processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirm password do not match.");
            // Fetch the original student data from the database
            processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement("SELECT password FROM " + schema + ".student_list_view WHERE id = ?")) {

            int studentId = (Integer) request.getSession().getAttribute("id");
            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && passwordEncoder.matches(currentPassword, rs.getString("password"))) {
                    String hashedNewPassword = passwordEncoder.encode(newPassword);

                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE " + schema + ".student_list_view SET password = ? WHERE id = ?")) {
                        updateStmt.setString(1, hashedNewPassword);
                        updateStmt.setInt(2, studentId);
                        updateStmt.executeUpdate();
                        request.setAttribute("success", "Password updated successfully!");
                    }
                } else {
                    request.setAttribute("error", "Incorrect current password. Password update failed");
                }
            }

        } catch (SQLException e) {
            logger.error("Database error updating student password.", e);
            request.setAttribute("error", "Database error occurred.");
        }

        // Fetch the updated student data from the database and pass it to the JSP
        processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));

        request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
    }

    private boolean validateInput(HttpServletRequest request, String studentId, String name, String department, String batch) {
        if (studentId == null || name == null || department == null || batch == null
                || studentId.trim().isEmpty() || name.trim().isEmpty()
                || department.trim().isEmpty() || batch.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            return false;
        }

        if (!STUDENT_ID_PATTERN.matcher(studentId).matches()) {
            request.setAttribute("error", "Student ID must contain only numbers.");
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            request.setAttribute("error", "Invalid name format. Only letters and spaces allowed.");
            return false;
        }

        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            request.setAttribute("error", "Invalid department format. Only letters and spaces allowed.");
            return false;
        }

        if (!BATCH_PATTERN.matcher(batch).matches()) {
            request.setAttribute("error", "Invalid batch format. Must be a number.");
            return false;
        }

        return true;
    }

}
