package com.rakibdevhub.iusping.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.rakibdevhub.iusping.config.DatabaseConfig;
import com.rakibdevhub.iusping.model.StudentModel;
import com.rakibdevhub.iusping.utils.SmsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/teacher/sendMessage")
public class SendMessageServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SendMessageServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            logger.warn("Unauthorized access to SendMessageServlet (GET).");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String[] studentIds = request.getParameterValues("selectedStudents");
        if (studentIds == null || studentIds.length == 0) {
            logger.warn("No students selected (GET).");
            request.setAttribute("errorMessage", "Please select at least one student.");
            request.getRequestDispatcher("/teacher/dashboard").forward(request, response);
            return;
        }

        List<StudentModel> students = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            for (String studentId : studentIds) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM student WHERE id = ?")) {
                    stmt.setString(1, studentId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            students.add(new StudentModel(
                                    rs.getInt("id"),
                                    rs.getString("student_id"),
                                    rs.getString("name"),
                                    rs.getString("batch"),
                                    rs.getString("department"),
                                    null,
                                    null
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Database error (GET):", e);
            request.setAttribute("errorMessage", "Database error occurred. Please try again later.");
            request.getRequestDispatcher("/teacher/dashboard").forward(request, response);
            return;
        }

        request.setAttribute("students", students);
        request.getRequestDispatcher("/send_message.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            logger.warn("Unauthorized access to SendMessageServlet (POST).");
            response.sendRedirect("index.jsp");
            return;
        }

        String[] studentIds = request.getParameterValues("selectedStudents");
        String message = request.getParameter("message");

        if (studentIds == null || studentIds.length == 0 || message == null || message.trim().isEmpty()) {
            logger.warn("Invalid input (POST): studentIds or message is empty.");
            session.setAttribute("errorMessage", "Please select students and enter a message.");
            response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
            return;
        }

        List<String> phoneNumbers = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection()) {
            for (String studentId : studentIds) {
                String studentPhoneNumber = null;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT phone_number FROM student WHERE id = ?")) {
                    stmt.setString(1, studentId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            studentPhoneNumber = rs.getString("phone_number");
                        }
                    }
                }

                if (studentPhoneNumber != null && !studentPhoneNumber.isEmpty()) {
                    phoneNumbers.add(studentPhoneNumber);
                } else {
                    logger.warn("Invalid phone number for studentId={}", studentId);
                    // You might want to store the IDs of students with invalid numbers for more specific feedback
                }
            }

            if (!phoneNumbers.isEmpty()) {
                boolean allSent = SmsManager.sendBulkSMS(phoneNumbers, message);
                if (!allSent) {
                    session.setAttribute("errorMessage", "Failed to send SMS to some students.");
                } else {
                    session.setAttribute("successMessage", "Message sent to selected students!");
                }
            } else {
                session.setAttribute("errorMessage", "No valid phone numbers found for selected students.");
            }

        } catch (SQLException e) {
            logger.error("Database error (POST):", e);
            session.setAttribute("errorMessage", "Database error occurred. Please try again later.");
        }

        response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
    }
}
