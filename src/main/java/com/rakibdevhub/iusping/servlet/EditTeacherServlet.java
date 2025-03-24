package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import com.rakibdevhub.iusping.model.TeacherModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet("/admin/editTeacher")
public class EditTeacherServlet extends HttpServlet {

    private final String schema = DatabaseConfig.getSchema();

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String teacherEmail = request.getParameter("id");

        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schema + ".teacher WHERE email = ?")) {

            stmt.setString(1, teacherEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TeacherModel teacher = new TeacherModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email")
                );
                request.setAttribute("teacher", teacher);
                request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=TeacherNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String department = request.getParameter("department");

        // Validate input fields
        if (!validateInput(request, name, department, email)) {
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE " + schema + ".teacher SET name = ?, department = ?, email = ? WHERE id = ?"
        )) {

            stmt.setString(1, name);
            stmt.setString(2, department.toUpperCase());
            stmt.setString(3, email);
            stmt.setInt(4, id);

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }

    // Validate name, email, and department fields
    private boolean validateInput(HttpServletRequest request, String name, String department, String email) {
        // Check for empty fields
        if (name == null || name.trim().isEmpty()
                || department == null || department.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {

            request.setAttribute("error", "Please fill in all required fields.");
            return false;
        }

        // Validate name
        if (!NAME_PATTERN.matcher(name).matches()) {
            request.setAttribute("error", "Invalid name. Only letters and spaces allowed (2-50 characters).");
            return false;
        }

        // Validate department
        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            request.setAttribute("error", "Invalid department name. Only letters and spaces allowed (2-50 characters).");
            return false;
        }

        // Validate email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Invalid email format.");
            return false;
        }

        return true;
    }
}
