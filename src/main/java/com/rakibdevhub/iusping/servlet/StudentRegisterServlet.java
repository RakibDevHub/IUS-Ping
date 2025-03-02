package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/student/register")
public class StudentRegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegisterServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/student_register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String batch = request.getParameter("batch");
        String department = request.getParameter("department");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");

        if (studentId == null || studentId.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || batch == null || batch.trim().isEmpty()
                || department == null || department.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute("error", "MissingFields");
            request.getRequestDispatcher("/student_register.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (studentExists(conn, studentId)) {
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
                return;
            }

            String hashedPassword = hashPassword(password);

            String insertQuery = "INSERT INTO student (student_id, name, batch, department, phone_number, password, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

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
        } catch (SQLException e) {
            logger.error("Database error during student registration", e);
            request.setAttribute("error", "DatabaseError");
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
