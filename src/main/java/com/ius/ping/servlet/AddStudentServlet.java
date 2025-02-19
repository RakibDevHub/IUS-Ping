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

@WebServlet("/admin/addStudent")
public class AddStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/add_student.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");


        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO students (student_id, name, department, phone_number, password) VALUES (?, ?, ?, ?, ?)"
        )) {

            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, password);

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard"); // Redirect to dashboard
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly (e.g., show an error message)
            response.sendRedirect(request.getContextPath() + "/admin/addStudent?error=DatabaseError");
        }
    }
}
