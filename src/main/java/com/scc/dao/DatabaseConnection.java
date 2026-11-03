package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:students.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found — check pom.xml");
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Connection Error: " + e.getMessage());
            return null;
        }
    }
}
