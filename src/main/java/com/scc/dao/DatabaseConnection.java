package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:loyalty.db";
    private static Connection connection = null;

    static {
        try {
            // Load SQLite driver explicitly
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found! Check pom.xml.");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                initializeTables(connection);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        return connection;
    }

    private static void initializeTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('ADMIN','CASHIER','CUSTOMER')),
                status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','APPROVED','DEACTIVATED'))
            )
            """);

        stmt.execute("""
            INSERT OR IGNORE INTO users (username, password, role, status)
            VALUES ('admin','admin123','ADMIN','APPROVED')
            """);

        stmt.close();
    }
}
