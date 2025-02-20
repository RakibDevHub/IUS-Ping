package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import com.rakibdevhub.iusping.model.StudentModel;
import com.rakibdevhub.iusping.model.TeacherModel;
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

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<StudentModel> students = getStudentsFromDatabase();
        List<TeacherModel> teachers = getTeachersFromDatabase();

        request.setAttribute("students", students);
        request.setAttribute("teachers", teachers);
        request.setAttribute("role", session.getAttribute("role"));

        request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
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

    private List<TeacherModel> getTeachersFromDatabase() {
        List<TeacherModel> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT teacher_id, name, email FROM teachers"); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TeacherModel teacher = new TeacherModel(
                        rs.getInt("teacher_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }
}
