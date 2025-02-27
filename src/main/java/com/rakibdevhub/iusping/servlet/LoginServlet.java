package com.rakibdevhub.iusping.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.rakibdevhub.iusping.config.DatabaseConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String role = request.getParameter("role");
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        
        if (role == null || identifier == null || password == null) {
            response.sendRedirect("login?error=Invalid input");
            return;
        }
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "";
            switch (role) {
                case "admin" ->
                    query = "SELECT * FROM admin WHERE email = ? AND password = ?";
                case "teacher" ->
                    query = "SELECT * FROM teacher WHERE email = ? AND password = ?";                
                case "student" ->
                    query = "SELECT * FROM student WHERE student_id = ? AND password = ?";
                default -> {
                    response.sendRedirect("login?error=Invalid role");
                    return;
                }
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, identifier);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("id", rs.getInt("id"));
                session.setAttribute("role", role);
                session.setAttribute("user", rs.getString("name"));
                switch (role) {
                    case "admin" ->
                        response.sendRedirect("admin/dashboard");
                    case "teacher" ->
                        response.sendRedirect("teacher/dashboard");
                    default ->
                        response.sendRedirect("student/dashboard");
                }
            } else {
                response.sendRedirect("login?error=Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login?error=Database error");
        }
    }
}
