package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@WebServlet("/student/register")
public class StudentRegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegisterServlet.class);

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

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (studentExists(conn, studentId)) {
                handleExistingStudent(request, response, conn, studentId);
                return;
            }

            String hashedPassword = hashPassword(password);
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
        if (!phoneNumber.matches("01\\d{9}")) {
            request.setAttribute("error", "Invalid phone number format. Must be 11 digits and start with 01.");
            return false;
        }
        return true;
    }

    private void handleExistingStudent(HttpServletRequest request, HttpServletResponse response, Connection conn, String studentId) throws ServletException, IOException, SQLException {
        String existingStatus = getStudentStatus(conn, studentId);
        if (existingStatus != null) {
            switch (existingStatus.toUpperCase()) {
                case "PENDING" -> request.setAttribute("error", "Your registration is pending approval. Please check back later.");
                case "REJECTED" -> request.setAttribute("error", "Your previous registration request was rejected. Please contact the administrator for further assistance.");
                default -> request.setAttribute("error", "This student ID is already registered.");
            }
        } else {
            request.setAttribute("error", "StudentExists");
        }
        request.getRequestDispatcher("/student_register.jsp").forward(request, response);
    }

    private void insertStudent(HttpServletRequest request, HttpServletResponse response, Connection conn, String studentId, String name, String batch, String department, String phoneNumber, String hashedPassword) throws ServletException, IOException, SQLException {
        String insertQuery = "INSERT INTO student (student_id, name, batch, department, phone_number, password, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, batch);
            stmt.setString(4, department);
            stmt.setString(5, phoneNumber);
            stmt.setString(6, hashedPassword);
            stmt.executeUpdate();
            request.setAttribute("success", "Registration successful. Please wait for admin approval.");
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
        }
    }

    private boolean studentExists(Connection conn, String studentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM student WHERE student_id = ?")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String getStudentStatus(Connection conn, String studentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT status FROM student WHERE student_id = ?")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
                return null;
            }
        }
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
