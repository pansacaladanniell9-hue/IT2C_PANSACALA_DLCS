package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("No database connection.");
                return;
            }
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
                VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')
                """);

            stmt.close();
            System.out.println("✅ Database ready");
        } catch (Exception e) {
            System.err.println("❌ Init Error: " + e.getMessage());
        }
    }
}
