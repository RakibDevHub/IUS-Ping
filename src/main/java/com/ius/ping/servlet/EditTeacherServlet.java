package com.ius.ping.servlet;

import com.ius.ping.config.DatabaseConfig;
import com.ius.ping.model.TeacherModel;
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

@WebServlet("/admin/editTeacher")
public class EditTeacherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String teacherEmail = request.getParameter("id");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM teachers WHERE teacher_id = ?")) {

            stmt.setString(1, teacherEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TeacherModel teacher = new TeacherModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                request.setAttribute("teacher", teacher);
                request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=TeacherNotFound");
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
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE teachers SET name = ?, email = ? WHERE id = ?"
        )) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=DatabaseError");
        }
    }
}
