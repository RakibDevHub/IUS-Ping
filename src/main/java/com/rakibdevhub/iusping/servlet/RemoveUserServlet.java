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

@WebServlet({"/admin/removeStudent", "/admin/removeTeacher"})
public class RemoveUserServlet extends HttpServlet {
    
    private final String schema = DatabaseConfig.getSchema();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String userId = request.getParameter("id");
        String tableName = "";
        String errorParam = "";

        if (null == path) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=InvalidAction");
            return;
        } else {
            switch (path) {
                case "/admin/removeStudent" -> {
                    tableName = "student";
                    errorParam = "Student";
                }
                case "/admin/removeTeacher" -> {
                    tableName = "teacher";
                    errorParam = "Teacher";
                }
                default -> {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=InvalidAction");
                    return;
                }
            }
        }

        if (userId == null || userId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Missing" + errorParam + "ID");
            return;
        }

        try {
            int id = Integer.parseInt(userId);

            try (Connection conn = DatabaseConfig.getConnectionMaster(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + schema + "." + tableName + " WHERE id = ?")) {

                stmt.setInt(1, id);
                stmt.executeUpdate();
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");

            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Invalid" + errorParam + "ID");
        }
    }
}