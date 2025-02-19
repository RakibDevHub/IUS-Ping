package com.ius.ping.servlet;

import com.ius.ping.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/admin/deleteTeacher")
public class DeleteTeacherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String teacherEmail = request.getParameter("id");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM teachers WHERE teacher_id = ?")) {

            stmt.setString(1, teacherEmail);
            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }
}
