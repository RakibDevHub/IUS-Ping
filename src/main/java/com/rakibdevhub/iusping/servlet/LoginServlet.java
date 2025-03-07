package com.rakibdevhub.iusping.servlet;

import java.io.IOException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final String schema  = DatabaseConfig.getSchema();
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
            request.setAttribute("error", "Please provide all login details.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnectionUser()) {
            String query = "";
            switch (role) {
                case "admin" ->
                    query = "SELECT * FROM " + schema + ".admin WHERE email = ?";
                case "teacher" ->
                    query = "SELECT * FROM " + schema + ".teacher WHERE email = ?";
                case "student" ->
                    query = "SELECT * FROM " + schema + ".student_list_view WHERE student_id = ?";
                default -> {
                    request.setAttribute("error", "Invalid user role selected.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && passwordEncoder.matches(password, rs.getString("password"))) {
                if ("student".equals(role)) {
                    String status = rs.getString("status");
                    if (!"Approved".equalsIgnoreCase(status)) {
                        request.setAttribute("error", "Your student account is not yet approved.");
                        request.getRequestDispatcher("/login.jsp").forward(request, response);
                        return;
                    }
                }
                HttpSession session = request.getSession();
                session.setAttribute("id", rs.getInt("id"));
                session.setAttribute("role", role);
                switch (role) {
                    case "admin" ->
                        response.sendRedirect("admin/dashboard");
                    case "teacher" ->
                        response.sendRedirect("teacher/dashboard");
                    default ->
                        response.sendRedirect("student/dashboard");
                }
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.error("Login Error", e);
            request.setAttribute("error", "A database error occurred. Please try again later.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
