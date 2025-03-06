package com.rakibdevhub.iusping.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet("/config")
public class ConfigServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Properties props = loadProperties(request);
        if (props != null) {
            request.setAttribute("dbUrl", props.getProperty("database.url"));
            request.setAttribute("dbUser", props.getProperty("database.username"));
            request.setAttribute("dbPassword", props.getProperty("database.password"));
            request.setAttribute("dbDriver", props.getProperty("database.driver"));
            request.setAttribute("smsEndpoint", props.getProperty("sms.endpoint"));
            request.setAttribute("smsToken", props.getProperty("sms.token"));
        }
        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (null != action) {
            switch (action) {
                case "testConnection" ->
                    testDatabaseConnection(request, response);
                case "createAdmin" ->
                    insertAdmin(request, response);
                case "saveDatabaseConfig" ->
                    saveDatabaseConfig(request, response);
                default -> {
                }
            }
        }
    }

    private void testDatabaseConnection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbUrl = request.getParameter("dbUrl");
        String dbUser = request.getParameter("dbUser");
        String dbPassword = request.getParameter("dbPassword");
        String dbDriver = request.getParameter("dbDriver");
        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        try {
            Class.forName(dbDriver);
            DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            request.setAttribute("connectionSuccess", true);
        } catch (ClassNotFoundException | SQLException e) {
            request.setAttribute("connectionError", e.getMessage());
        }

        request.setAttribute("dbUrl", dbUrl);
        request.setAttribute("dbUser", dbUser);
        request.setAttribute("dbPassword", dbPassword);
        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private void insertAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbUrl = request.getParameter("dbUrl");
        String dbUser = request.getParameter("dbUser");
        String dbPassword = request.getParameter("dbPassword");
        String dbDriver = request.getParameter("dbDriver");
        String adminEmail = request.getParameter("adminEmail");
        String adminPassword = request.getParameter("adminPassword");
        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        if (insertAdmin(dbUrl, dbUser, dbPassword, dbDriver, adminEmail, adminPassword)) {
            request.setAttribute("adminSuccess", true);
        } else {
            request.setAttribute("adminError", "Error inserting admin user.");
        }

        request.setAttribute("dbUrl", dbUrl);
        request.setAttribute("dbUser", dbUser);
        request.setAttribute("dbPassword", dbPassword);
        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private void saveDatabaseConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbUrl = request.getParameter("dbUrl");
        String dbUser = request.getParameter("dbUser");
        String dbPassword = request.getParameter("dbPassword");
        String dbDriver = request.getParameter("dbDriver");
        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        Properties props = loadProperties(request);
        if (props == null) {
            props = new Properties();
        }
        props.setProperty("database.url", dbUrl);
        props.setProperty("database.username", dbUser);
        props.setProperty("database.password", dbPassword);
        props.setProperty("database.driver", dbDriver);
        props.setProperty("sms.endpoint", smsEndpoint);
        props.setProperty("sms.token", smsToken);

        try (FileWriter writer = new FileWriter(getServletContext().getRealPath("/WEB-INF/classes/config.properties"))) {
            props.store(writer, "Database and SMS Configuration");
        } catch (IOException e) {
            request.setAttribute("error", "Error saving configuration.");
            request.getRequestDispatcher("/config.jsp").forward(request, response);
            return;
        }

        request.setAttribute("dbUrl", dbUrl);
        request.setAttribute("dbUser", dbUser);
        request.setAttribute("dbPassword", dbPassword);
        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private Properties loadProperties(HttpServletRequest request) {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(request.getServletContext().getRealPath("/WEB-INF/classes/config.properties"))) {
            props.load(reader);
            return props;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean insertAdmin(String dbUrl, String dbUser, String dbPassword, String dbDriver, String email, String password) {
        String hashedPassword = hashPassword(password);
        try {
            Class.forName(dbDriver);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ius_admin.admin (email, password) VALUES (?, ?)")) {
                pstmt.setString(1, email);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
