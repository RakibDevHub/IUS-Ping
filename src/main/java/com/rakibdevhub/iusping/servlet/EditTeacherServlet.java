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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/admin/editTeacher")
public class EditTeacherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EditTeacherServlet.class);
    private final String schema = DatabaseConfig.getSchema();
    
        // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            request.setAttribute("error", "Teacher ID is required.");
            request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
            return;
        }

        processTeacherData(request, response, id);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Teacher ID.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String department = request.getParameter("department");

        // Validate input
        if (!validateInput(request, name, email, department)) {
            request.setAttribute("teacher", new TeacherModel(
                    id, name, department, email));
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            return;
        }

        // Update the teacher information in the database
        updateTeacherData(request, response, id, name, department, email);
    }

    private void processTeacherData(HttpServletRequest request, HttpServletResponse response, String id)
            throws ServletException, IOException {
        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schema + ".teacher WHERE id = ?")) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
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
                    request.setAttribute("error", "Teacher not found.");
                    request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
                }
            }

        } catch (SQLException e) {
            logger.error("Database error retrieving teacher data.", e);
            request.setAttribute("error", "Database error occurred.");
            request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
        }
    }

    private void updateTeacherData(HttpServletRequest request, HttpServletResponse response, int id, String name, String department, String email)
            throws ServletException, IOException {
        try (Connection conn = DatabaseConfig.getConnectionMaster(); PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + ".teacher SET name = ?, department = ?, email = ?  WHERE id = ?")) {

            stmt.setString(1, name);
            stmt.setString(2, department.toUpperCase());
            stmt.setString(3, email);
            stmt.setInt(4, id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                request.setAttribute("success", "Teacher updated successfully!");
                processTeacherData(request, response, String.valueOf(id)); // Reload the teacher data to reflect the changes
            } else {
                request.setAttribute("error", "Failed to update teacher.");
                processTeacherData(request, response, String.valueOf(id)); // Reload the data with error
            }

        } catch (SQLException e) {
            logger.error("Database error updating teacher data.", e);
            request.setAttribute("error", "Database error occurred.");
            processTeacherData(request, response, String.valueOf(id)); // Reload the data with error
        }
    }

    private boolean validateInput(HttpServletRequest request, String name, String email, String department) {
        if (name == null || department == null || email == null || name.trim().isEmpty() || department.trim().isEmpty() || email.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            request.setAttribute("error", "Invalid name. Only letters, spaces, and dots allowed.");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Invalid email format.");
            return false;
        }
        
        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            request.setAttribute("error", "Invalid department name. Only letters and spaces allowed.");
            return false;
        }

        return true;
    }
}
