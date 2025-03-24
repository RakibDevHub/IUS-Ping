package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

@WebServlet("/student/register")
public class StudentRegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegisterServlet.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String schema = DatabaseConfig.getSchema();

    // Validation patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern BATCH_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/student_register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String batch = request.getParameter("batch");
        String department = request.getParameter("department");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");

        if (!validateInput(request, studentId, name, batch, department, phoneNumber, password)) {
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnectionUser()) {
            if (studentExists(conn, studentId)) {
                handleExistingStudent(request, response, conn, studentId);
                return;
            }

            String hashedPassword = passwordEncoder.encode(password);
            insertStudent(request, response, conn, studentId, name, batch, department, phoneNumber, hashedPassword);

        } catch (SQLException e) {
            logger.error("Database error during student registration", e);
            request.setAttribute("error", "DatabaseError");
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
        } catch (RuntimeException e) {
            logger.error("Error hashing password", e);
            request.setAttribute("error", "Error hashing password");
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
        }
    }

    private boolean validateInput(HttpServletRequest request, String studentId, String name, String batch, String department, String phoneNumber, String password) {
        if (studentId == null || studentId.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || batch == null || batch.trim().isEmpty()
                || department == null || department.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            return false;
        }

        if (!STUDENT_ID_PATTERN.matcher(studentId).matches()) {
            request.setAttribute("error", "Student ID must contain only numbers.");
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            request.setAttribute("error", "Name must contain only alphabets and spaces.");
            return false;
        }

        if (!BATCH_PATTERN.matcher(batch).matches()) {
            request.setAttribute("error", "Batch must contain only numbers.");
            return false;
        }

        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            request.setAttribute("error", "Department must contain only alphabets and spaces.");
            return false;
        }

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            request.setAttribute("error", "Invalid phone number format. Must be 11 digits and start with 01.");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            request.setAttribute("error", "Password must be at least 8 characters long and contain at least one letter and one number.");
            return false;
        }

        return true;
    }

    private void handleExistingStudent(HttpServletRequest request, HttpServletResponse response, Connection conn, String studentId) throws ServletException, IOException, SQLException {
        String existingStatus = getStudentStatus(conn, studentId);
        if (existingStatus != null) {
            switch (existingStatus.toUpperCase()) {
                case "PENDING" ->
                    request.setAttribute("error", "Your registration is pending approval. Please check back later.");
                case "REJECTED" ->
                    request.setAttribute("error", "Your previous registration request was rejected. Please contact the administrator for further assistance.");
                default ->
                    request.setAttribute("error", "This student ID is already registered.");
            }
        } else {
            request.setAttribute("error", "StudentExists");
        }
        request.getRequestDispatcher("/student_register.jsp").forward(request, response);
    }

    private void insertStudent(HttpServletRequest request, HttpServletResponse response, Connection conn, String studentId, String name, String batch, String department, String phoneNumber, String hashedPassword) throws ServletException, IOException, SQLException {
        String insertQuery = "INSERT INTO " + schema + ".student (student_id, name, batch, department, phone_number, password, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, batch);
            stmt.setString(4, department.toUpperCase()); // Ensure uppercase department
            stmt.setString(5, phoneNumber);
            stmt.setString(6, hashedPassword);
            stmt.executeUpdate();
            request.setAttribute("success", "Registration successful. Please wait for admin approval.");
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
        }
    }

    private boolean studentExists(Connection conn, String studentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM " + schema + ".student_list_view WHERE student_id = ?")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String getStudentStatus(Connection conn, String studentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT status FROM " + schema + ".student_list_view WHERE student_id = ?")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        }
    }
}
