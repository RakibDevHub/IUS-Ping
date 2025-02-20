package com.rakibdevhub.iusping.utils;

import java.sql.Connection;
import java.sql.SQLException;
import com.rakibdevhub.iusping.config.DatabaseConfig;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                System.out.println("Success: Database Connection Successful!");
            } else {
                System.out.println("Error: Database Connection Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
