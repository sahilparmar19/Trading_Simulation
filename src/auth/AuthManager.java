package auth;

import db.DatabaseManager;
import model.User;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class AuthManager {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("SHA-256 digest failed", ex);
        }
    }

    public static boolean signUp(String username, String password, String name) {
        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash, name, balance, is_admin, created_at) VALUES (?, ?, ?, 100000.00, FALSE, NOW())";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Sign Up failed: " + e.getMessage());
            return false;
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    public static User login(String username, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("name"),
                    rs.getDouble("balance"),
                    rs.getBoolean("is_admin"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return null;
    }

    public static User adminLogin(String username, String password) {
        User user = login(username, password);
        if (user != null && user.isAdmin()) {
            return user;
        }
        return null;
    }
}
