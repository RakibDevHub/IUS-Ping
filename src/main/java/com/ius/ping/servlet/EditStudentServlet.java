package com.ius.ping.servlet;

import com.ius.ping.config.DatabaseConfig;
import com.ius.ping.model.StudentModel;
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

@WebServlet("/admin/editStudent")
public class EditStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("id");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students WHERE id = ?")) {

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
                request.getRequestDispatcher("/edit_student.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=StudentNotFound");
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
        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String phoneNumber = request.getParameter("phoneNumber");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE students SET student_id = ?, name = ?, department = ?, phone_number = ? WHERE id = ?"
        )) {

            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, id);

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }
}
