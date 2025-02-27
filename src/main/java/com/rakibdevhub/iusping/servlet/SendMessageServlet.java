package com.rakibdevhub.iusping.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        String studentId = request.getParameter("id");
        if (studentId == null || studentId.trim().isEmpty()) {
            logger.warn("Invalid student ID (GET).");
            request.setAttribute("errorMessage", "Invalid student ID.");
            request.getRequestDispatcher("/send_message.jsp").forward(request, response);
            return;
        }

        logger.info("Student ID (GET): {}", studentId);

        StudentModel student = (StudentModel) session.getAttribute("student");
        if (student == null || student.getId() != Integer.parseInt(studentId)) {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = DatabaseConfig.getConnection();
                stmt = conn.prepareStatement(
                        "SELECT id, student_id, name, department, phone_number FROM students WHERE id = ?");
                stmt.setString(1, studentId);

                rs = stmt.executeQuery();

                if (rs.next()) {
                    student = new StudentModel(
                            rs.getInt("id"),
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("phone_number")
                    );
                    session.setAttribute("student", student);
                } else {
                    logger.warn("Student not found (GET): studentId={}", studentId);
                    request.setAttribute("errorMessage", "Student not found.");
                    request.getRequestDispatcher("/send_message.jsp").forward(request, response);
                    return;
                }

            } catch (SQLException e) {
                logger.error("Database error (GET): studentId={}", studentId, e);
                request.setAttribute("errorMessage", "Database error occurred. Please try again later.");
                request.getRequestDispatcher("/send_message.jsp").forward(request, response);
                return;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("Error closing ResultSet", e);
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        logger.error("Error closing PreparedStatement", e);
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error("Error closing Connection", e);
                    }
                }
            }
        }

        request.setAttribute("student", student);
        session.removeAttribute("student");

        String message = (String) session.getAttribute("message");
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }

        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

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

        String studentId = request.getParameter("id");
        String message = request.getParameter("message");

        if (studentId == null || studentId.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            logger.warn("Invalid input (POST): studentId or message is empty.");
            session.setAttribute("errorMessage", "Student ID and message cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/teacher/sendMessage?id=" + studentId);
            return;
        }

        String studentPhoneNumber = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT phone_number FROM students WHERE id = ?");
            stmt.setString(1, studentId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                studentPhoneNumber = rs.getString("phone_number");
            } else {
                logger.warn("Student not found (POST): studentId={}", studentId);
                session.setAttribute("errorMessage", "Student not found.");
                response.sendRedirect(request.getContextPath() + "/teacher/sendMessage?id=" + studentId);
                return;
            }

            boolean smsSent = false;
            if (studentPhoneNumber != null && !studentPhoneNumber.isEmpty()) {
                smsSent = SmsManager.sendSMS(studentPhoneNumber, message);
            } else {
                logger.warn("Invalid phone number for studentId={}", studentId);
                session.setAttribute("errorMessage", "Invalid phone number.");
                response.sendRedirect(request.getContextPath() + "/teacher/sendMessage?id=" + studentId);
                return;
            }

            if (smsSent) {
                logger.info("Message sent successfully: studentId={}", studentId);
                session.setAttribute("successMessage", "Message sent successfully!");
            } else {
                logger.error("Failed to send SMS (POST): studentId= {}", studentId);
                session.setAttribute("errorMessage", "Failed to send SMS.");
            }

        } catch (SQLException e) {
            logger.error("Database error (POST): studentId= {}", studentId, e);
            session.setAttribute("errorMessage", "Database error occurred. Please try again later.");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("Error closing ResultSet", e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("Error closing PreparedStatement", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing Connection", e);
                }
            }
        }

        session.setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/teacher/sendMessage?id=" + studentId);
    }
}