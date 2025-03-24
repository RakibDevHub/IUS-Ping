package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/admin/addTeacher")
public class AddTeacherServlet extends HttpServlet {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String schema = DatabaseConfig.getSchema();

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input fields
        if (!validateInput(request, name, department, email, password)) {
            request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnectionUser()) {
            if (teacherEmailExists(conn, email)) {
                request.setAttribute("error", "A teacher with this email address already exists.");
                request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
                return;
            }

            String hashedPassword = passwordEncoder.encode(password);

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO " + schema + ".teacher (name, department, email, password) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, name);
                stmt.setString(2, department.toUpperCase()); // Convert to uppercase
                stmt.setString(3, email);
                stmt.setString(4, hashedPassword);

                stmt.executeUpdate();
                request.setAttribute("success", "Teacher added successfully.");
                request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "A database error occurred. Please try again later.");
            request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
        }
    }

    private boolean teacherEmailExists(Connection conn, String email) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM " + schema + ".teacher WHERE email = ?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean validateInput(HttpServletRequest request, String name, String department, String email, String password) {
        if (name == null || name.trim().isEmpty() ||
            department == null || department.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            request.setAttribute("error", "Please fill in all required fields.");
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            request.setAttribute("error", "Invalid name. Only letters and spaces allowed (2-50 characters).");
            return false;
        }

        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            request.setAttribute("error", "Invalid department name. Only letters and spaces allowed (2-50 characters).");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Invalid email format.");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            request.setAttribute("error", "Password must be at least 8 characters long and contain at least one letter and one number.");
            return false;
        }

        return true;
    }
}
