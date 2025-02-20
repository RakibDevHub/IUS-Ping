package com.rakibdevhub.iusping.servlet;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import com.rakibdevhub.iusping.model.StudentModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object studentIdObj = session.getAttribute("id");

        if (studentIdObj instanceof Integer) {
            String studentId = String.valueOf((Integer) studentIdObj);
            processStudentData(request, response, studentId);
        } else if (studentIdObj instanceof String) {
            String studentId = (String) studentIdObj;
            processStudentData(request, response, studentId);
        } else {
            logger.error("Unexpected type of 'id' in session: {}",
                    studentIdObj != null ? studentIdObj.getClass() : "null");
            response.sendRedirect(request.getContextPath() + "/home?error=InvalidSession");
        }
    }

    private void processStudentData(HttpServletRequest request, HttpServletResponse response, String studentId)
            throws ServletException, IOException {

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students WHERE id = ?")) {

            stmt.setString(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
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
            }

        } catch (SQLException e) {
            logger.error("Database error retrieving student data.", e); // Log the exception
            response.sendRedirect(request.getContextPath() + "/home?error=DatabaseError");
        }
    }
}
