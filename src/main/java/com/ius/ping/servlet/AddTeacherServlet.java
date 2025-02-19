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

@WebServlet("/admin/addTeacher")
public class AddTeacherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/add_teacher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO teachers (name, email, password) VALUES (?, ?, ?)"
        )) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard"); // Redirect to dashboard
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly (e.g., show an error message)
            response.sendRedirect(request.getContextPath() + "/admin/addTeacher?error=DatabaseError");
        }
    }
}
