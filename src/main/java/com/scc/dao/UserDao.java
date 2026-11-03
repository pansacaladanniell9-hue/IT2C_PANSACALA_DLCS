package com.scc.dao;

import com.scc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean registerUser(String username, String password, String role) {
        // Get fresh connection every time
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;

            String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, 'PENDING')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Username already exists or error: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(int userId, String newStatus) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET status = ? WHERE id = ?")) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, userId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public List<User> getAllPendingUsers() {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return list;

            try (ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT * FROM users WHERE status = 'PENDING'")) {
                while (rs.next()) {
                    list.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            "",
                            rs.getString("role"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {}
        return list;
    }
}
