package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@WebServlet("/student/register")
public class StudentRegisterServlet extends HttpServlet {

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

        // Check if any field is missing
        if (studentId == null || studentId.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || batch == null || batch.trim().isEmpty()
                || department == null || department.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            response.sendRedirect(request.getContextPath() + "/student/register?error=MissingFields");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (studentExists(conn, studentId)) {
                response.sendRedirect(request.getContextPath() + "/student/register?error=StudentExists");
                return;
            }

            String hashedPassword = hashPassword(password);

            // ✅ Use CallableStatement to properly handle RETURNING INTO in Oracle
            String insertQuery = "BEGIN INSERT INTO student (student_id, name, batch, department, phone_number, password) "
                    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id INTO ?; END;";

            try (CallableStatement stmt = conn.prepareCall(insertQuery)) {

                stmt.setString(1, studentId);
                stmt.setString(2, name);
                stmt.setString(3, batch);
                stmt.setString(4, department);
                stmt.setString(5, phoneNumber);
                stmt.setString(6, hashedPassword);

                // ✅ Register the output parameter correctly
                stmt.registerOutParameter(7, java.sql.Types.INTEGER);

                // ✅ Execute the statement
                stmt.execute();

                // ✅ Retrieve the generated ID
                int studentDbId = stmt.getInt(7);

                // ✅ Set session attributes
                HttpSession session = request.getSession();
                session.setAttribute("id", studentDbId);
                session.setAttribute("role", "student");

                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/register?error=DatabaseError");
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
