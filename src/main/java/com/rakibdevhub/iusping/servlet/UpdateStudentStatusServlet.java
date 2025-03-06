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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String status = "";

        if ("/admin/approveStudent".equals(path)) {
            status = "Approved";
        } else if ("/admin/rejectStudent".equals(path)) {
            status = "Rejected";
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=InvalidAction");
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=MissingStudentID");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE ius_admin.student_list_view SET status = ? WHERE id = ?")) {

                stmt.setString(1, status);
                stmt.setInt(2, id);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=StudentNotFound");
                }
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=InvalidStudentID");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }
}