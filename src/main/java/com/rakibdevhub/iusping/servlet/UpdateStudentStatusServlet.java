package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet({"/admin/approveStudent", "/admin/rejectStudent"})
public class UpdateStudentStatusServlet extends HttpServlet {

    private final String schema = DatabaseConfig.getSchema();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        String studentId = request.getParameter("id");

        if (studentId == null || studentId.isEmpty()) {
            request.setAttribute("error", "Invalid student ID.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        String newStatus = action.equals("/admin/approveStudent") ? "Approved" : "Rejected";

        try (Connection conn = DatabaseConfig.getConnectionUser(); PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + ".student_list_view SET status = ? WHERE id = ?")) {

            stmt.setString(1, newStatus);
            stmt.setString(2, studentId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                request.setAttribute("success", "Student " + newStatus.toLowerCase() + " successfully.");
            } else {
                request.setAttribute("error", "Failed to update student status.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
