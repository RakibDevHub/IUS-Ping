package com.rakibdevhub.iusping.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.rakibdevhub.iusping.config.DatabaseConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String role = request.getParameter("role");
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");

        if (role == null || identifier == null || password == null) {
            response.sendRedirect("login?error=Invalid input");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "";
            switch (role) {
                case "admin" ->
                    query = "SELECT * FROM admin WHERE email = ?";
                case "teacher" ->
                    query = "SELECT * FROM teacher WHERE email = ?";
                case "student" ->
                    query = "SELECT * FROM student WHERE student_id = ?";
                default -> {
                    response.sendRedirect("login?error=Invalid role");
                    return;
                }
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String enteredHashedPassword = hashPassword(password);

                if (storedHashedPassword.equals(enteredHashedPassword)) {
                    if ("student".equals(role)) {
                        String status = rs.getString("status");
                        if (!"Approved".equalsIgnoreCase(status)) {
                            response.sendRedirect("login?error=Student not approved");
                            return;
                        }
                    }
                    HttpSession session = request.getSession();
                    session.setAttribute("id", rs.getInt("id"));
                    session.setAttribute("role", role);
                    session.setAttribute("user", rs.getString("name"));
                    switch (role) {
                        case "admin" ->
                            response.sendRedirect("admin/dashboard");
                        case "teacher" ->
                            response.sendRedirect("teacher/dashboard");
                        default ->
                            response.sendRedirect("student/dashboard");
                    }
                } else {
                    response.sendRedirect("login?error=Invalid credentials");
                }
            } else {
                response.sendRedirect("login?error=Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Login Error", e);
            response.sendRedirect("login?error=Database error");
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