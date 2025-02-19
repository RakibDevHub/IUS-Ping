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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<StudentModel> students = getStudentsFromDatabase();

        request.setAttribute("students", students);
        request.setAttribute("role", session.getAttribute("role"));

        request.getRequestDispatcher("/teacher_dashboard.jsp").forward(request, response);
    }

    private List<StudentModel> getStudentsFromDatabase() {
        List<StudentModel> students = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT id, student_id, name, department, phone_number FROM students"); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StudentModel student = new StudentModel(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("phone_number")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
