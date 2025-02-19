package com.ius.ping.servlet;

import com.ius.ping.config.DatabaseConfig;
import com.ius.ping.model.StudentModel;
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

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String studentId = (String) session.getAttribute("studentId"); // Retrieve studentId from session

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?")) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StudentModel student = new StudentModel(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("phone_number")
                );
                request.setAttribute("student", student);
                request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=StudentNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?error=DatabaseError");
        }
    }
}
