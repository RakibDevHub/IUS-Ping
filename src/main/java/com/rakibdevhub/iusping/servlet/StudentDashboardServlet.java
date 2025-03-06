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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object studentIdObj = session.getAttribute("id");

        if (studentIdObj instanceof Integer) {
            processStudentData(request, response, String.valueOf(studentIdObj));
        } else if (studentIdObj instanceof String) {
            processStudentData(request, response, (String) studentIdObj);
        } else {
            logger.error("Unexpected type of 'id' in session: {}",
                    studentIdObj != null ? studentIdObj.getClass() : "null");
            response.sendRedirect(request.getContextPath() + "/home?error=InvalidSession");
        }
    }

    private void processStudentData(HttpServletRequest request, HttpServletResponse response, String studentId)
            throws ServletException, IOException {

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ius_admin.student_profile_view WHERE id = ?")) {

            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StudentModel student = new StudentModel(
                            rs.getInt("id"),
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("batch"),
                            rs.getString("department"),
                            rs.getString("phone_number"),
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
            case "updatePassword" ->
                updatePassword(request, response);
            default ->
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String batch = request.getParameter("batch");
        String phoneNumber = request.getParameter("phoneNumber");

        if (studentId == null || studentId.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || department == null || department.trim().isEmpty()
                || batch == null || batch.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        if (!phoneNumber.matches("01\\d{9}")) {
            request.setAttribute("error", "Invalid phone number format. Must be 11 digits and start with 01.");
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE ius_admin.student_profile_view SET student_id = ?, name = ?, department = ?, batch = ?, phone_number = ? WHERE id = ?")) {

            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, batch);
            stmt.setString(5, phoneNumber);
            stmt.setInt(6, (Integer) request.getSession().getAttribute("id"));

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                request.setAttribute("success", "Profile updated successfully!");
                processStudentData(request, response, String.valueOf(request.getSession().getAttribute("id")));
            } else {
                request.setAttribute("error", "Failed to update profile.");
            }

        } catch (SQLException e) {
            logger.error("Database error updating student profile.", e);
            request.setAttribute("error", "Database error occurred.");
        }

        request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
    }

    private void updatePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || currentPassword.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirm password do not match.");
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT password FROM ius_admin.student_profile_view WHERE id = ?")) {

            stmt.setInt(1, (Integer) request.getSession().getAttribute("id"));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");

                    String hashedCurrentPassword = hashPassword(currentPassword);

                    if (hashedCurrentPassword.equals(storedHashedPassword)) {
                        String hashedNewPassword = hashPassword(newPassword);

                        try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE ius_admin.student_profile_view SET password = ? WHERE id = ?")) {
                            updateStmt.setString(1, hashedNewPassword);
                            updateStmt.setInt(2, (Integer) request.getSession().getAttribute("id"));

                            int rowsUpdated = updateStmt.executeUpdate();
                            request.setAttribute(rowsUpdated > 0 ? "success" : "error",
                                    rowsUpdated > 0 ? "Password updated successfully!" : "Failed to update password.");
                        }
                    } else {
                        request.setAttribute("error", "Incorrect current password.");
                    }
                } else {
                    request.setAttribute("error", "Student not found.");
                }
            }

        } catch (SQLException e) {
            logger.error("Database error updating student password.", e);
            request.setAttribute("error", "Database error occurred.");
        }

        request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
