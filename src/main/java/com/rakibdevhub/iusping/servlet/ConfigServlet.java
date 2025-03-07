package com.rakibdevhub.iusping.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebServlet("/config")
public class ConfigServlet extends HttpServlet {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Properties props = loadProperties(request);
        if (props != null) {
            request.setAttribute("dbDriver", props.getProperty("database.driver"));
            request.setAttribute("dbUrl", props.getProperty("database.url"));

            request.setAttribute("dbUser_User", props.getProperty("database.user.username"));
            request.setAttribute("dbPassword_User", props.getProperty("database.user.password"));

            request.setAttribute("dbUser_Admin", props.getProperty("database.admin.username"));
            request.setAttribute("dbPassword_Admin", props.getProperty("database.admin.password"));

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
                case "testConnection_User" ->
                    testDatabaseConnection_User(request, response);
                case "testConnection_Admin" ->
                    testDatabaseConnection_Admin(request, response);
                case "createAdmin" ->
                    insertAdmin(request, response);
                case "saveDatabaseConfig" ->
                    saveDatabaseConfig(request, response);
                default -> {
                }
            }
        }
    }

    private void testDatabaseConnection_User(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbDriver = request.getParameter("dbDriver");
        String dbUrl = request.getParameter("dbUrl");

        String dbUser_User = request.getParameter("dbUser_User");
        String dbPassword_User = request.getParameter("dbPassword_User");

        String dbUser_Admin = request.getParameter("dbUser_Admin");
        String dbPassword_Admin = request.getParameter("dbPassword_Admin");

        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        try {
            Class.forName(dbDriver);
            DriverManager.getConnection(dbUrl, dbUser_User, dbPassword_User);
            request.setAttribute("connectionSuccess", true);
        } catch (ClassNotFoundException | SQLException e) {
            String errorMessage = "Database connection error: " + e.getMessage();
            request.setAttribute("connectionError", errorMessage);
            e.printStackTrace();
        }

        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("dbUrl", dbUrl);

        request.setAttribute("dbUser_User", dbUser_User);
        request.setAttribute("dbPassword_User", dbPassword_User);

        request.setAttribute("dbUser_Admin", dbUser_Admin);
        request.setAttribute("dbPassword_Admin", dbPassword_Admin);

        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private void testDatabaseConnection_Admin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbDriver = request.getParameter("dbDriver");
        String dbUrl = request.getParameter("dbUrl");

        String dbUser_User = request.getParameter("dbUser_User");
        String dbPassword_User = request.getParameter("dbPassword_User");

        String dbUser_Admin = request.getParameter("dbUser_Admin");
        String dbPassword_Admin = request.getParameter("dbPassword_Admin");

        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        try {
            Class.forName(dbDriver);
            DriverManager.getConnection(dbUrl, dbUser_Admin, dbPassword_Admin);
            request.setAttribute("connectionSuccess", true);
        } catch (ClassNotFoundException | SQLException e) {
            String errorMessage = "Database connection error: " + e.getMessage();
            request.setAttribute("connectionError", errorMessage);
            e.printStackTrace();
        }

        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("dbUrl", dbUrl);

        request.setAttribute("dbUser_User", dbUser_User);
        request.setAttribute("dbPassword_User", dbPassword_User);

        request.setAttribute("dbUser_Admin", dbUser_Admin);
        request.setAttribute("dbPassword_Admin", dbPassword_Admin);

        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private void insertAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbDriver = request.getParameter("dbDriver");
        String dbUrl = request.getParameter("dbUrl");

        String dbUser_User = request.getParameter("dbUser_User");
        String dbPassword_User = request.getParameter("dbPassword_User");

        String dbUser_Admin = request.getParameter("dbUser_Admin");
        String dbPassword_Admin = request.getParameter("dbPassword_Admin");

        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        String adminEmail = request.getParameter("adminEmail");
        String adminPassword = request.getParameter("adminPassword");

        if (insertAdmin(dbDriver, dbUrl, dbUser_Admin, dbPassword_Admin, adminEmail, adminPassword)) {
            request.setAttribute("adminSuccess", true);
        } else {
            request.setAttribute("adminError", "Error inserting admin user.");
        }

        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("dbUrl", dbUrl);

        request.setAttribute("dbUser_User", dbUser_User);
        request.setAttribute("dbPassword_User", dbPassword_User);

        request.setAttribute("dbUser_Admin", dbUser_Admin);
        request.setAttribute("dbPassword_Admin", dbPassword_Admin);

        request.setAttribute("smsEndpoint", smsEndpoint);
        request.setAttribute("smsToken", smsToken);

        request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

    private void saveDatabaseConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dbDriver = request.getParameter("dbDriver");
        String dbUrl = request.getParameter("dbUrl");

        String dbUser_User = request.getParameter("dbUser_User");
        String dbPassword_User = request.getParameter("dbPassword_User");

        String dbUser_Admin = request.getParameter("dbUser_Admin");
        String dbPassword_Admin = request.getParameter("dbPassword_Admin");

        String smsEndpoint = request.getParameter("smsEndpoint");
        String smsToken = request.getParameter("smsToken");

        Properties props = loadProperties(request);
        if (props == null) {
            props = new Properties();
        }
        props.setProperty("database.driver", dbDriver);
        props.setProperty("database.url", dbUrl);

        props.setProperty("database.user.username", dbUser_User);
        props.setProperty("database.user.password", dbPassword_User);

        props.setProperty("database.admin.username", dbUser_Admin);
        props.setProperty("database.admin.password", dbPassword_Admin);

        props.setProperty("sms.endpoint", smsEndpoint);
        props.setProperty("sms.token", smsToken);

        try (FileWriter writer = new FileWriter(getServletContext().getRealPath("/WEB-INF/classes/config.properties"))) {
            props.store(writer, "Database and SMS Configuration");
        } catch (IOException e) {
            request.setAttribute("error", "Error saving configuration.");
            request.getRequestDispatcher("/config.jsp").forward(request, response);
            return;
        }

        request.setAttribute("dbDriver", dbDriver);
        request.setAttribute("dbUrl", dbUrl);

        request.setAttribute("dbUser_User", dbUser_User);
        request.setAttribute("dbPassword_User", dbPassword_User);

        request.setAttribute("dbUser_Admin", dbUser_Admin);
        request.setAttribute("dbPassword_Admin", dbPassword_Admin);

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

    private boolean insertAdmin(String dbDriver, String dbUrl, String dbUser_Admin, String dbPassword_Admin, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        try {
            Class.forName(dbDriver);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser_Admin, dbPassword_Admin); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO "+dbUser_Admin+".admin (email, password) VALUES (?, ?)")) {
                pstmt.setString(1, email);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
