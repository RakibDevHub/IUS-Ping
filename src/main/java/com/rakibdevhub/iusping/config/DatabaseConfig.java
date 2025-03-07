package com.rakibdevhub.iusping.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private static String DRIVER;
    private static String URL;

    private static String USER_USERNAME;
    private static String USER_PASSWORD;

    private static String ADMIN_USERNAME;
    private static String ADMIN_PASSWORD;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("Database configuration file not found!");
            }

            // Load properties file
            Properties properties = new Properties();
            properties.load(input);

            // Get properties
            DRIVER = properties.getProperty("database.driver");
            URL = properties.getProperty("database.url");

            USER_USERNAME = properties.getProperty("database.user.username");
            USER_PASSWORD = properties.getProperty("database.user.password");

            ADMIN_USERNAME = properties.getProperty("database.admin.username");
            ADMIN_PASSWORD = properties.getProperty("database.admin.password");

            // Load database driver
            Class.forName(DRIVER);
            logger.info("Database driver loaded successfully.");

        } catch (IOException e) {
            logger.error("Error loading database properties: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load database configuration due to IOException", e);
        } catch (ClassNotFoundException e) {
            logger.error("Database driver class not found: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load database driver", e);
        } catch (Exception e) {
            logger.error("Unexpected error during database configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load database configuration due to an unexpected exception", e);
        }
    }

    public static Connection getConnectionUser() throws SQLException {
        logger.debug("Creating connection for USER.");
        return DriverManager.getConnection(URL, USER_USERNAME, USER_PASSWORD);
    }

    public static Connection getConnectionMaster() throws SQLException {
        logger.debug("Creating connection for MASTER (Admin).");
        return DriverManager.getConnection(URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public static String getSchema() {
        return ADMIN_USERNAME;
    }
}
