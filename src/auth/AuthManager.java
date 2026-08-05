package auth;

import db.DatabaseManager;
import model.User;

import java.sql.*;

public class AuthManager {

    /**
     * Simple password hashing using character manipulation.
     *
     * Java Concept (Sem 2 - Strings & Loops):
     *   - Iterates over each character of the password
     *   - Mixes position, ASCII value, and a fixed salt number
     *   - Converts result to a hex-like string
     *
     * Note: This is a custom hash for learning purposes.
     *       Real systems use SHA-256 or bcrypt (not in Sem 2 syllabus).
     */
    public static String hashPassword(String password) {
        int salt = 31;          // mixing constant (like in Java's String.hashCode)
        long hash = 5381;       // starting seed value

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            // Mix: multiply hash, add char value, mix with position
            hash = (hash * salt) + (int) c + (i + 1);
        }

        // Convert to a fixed-length hex string (always 16 characters)
        String hex = Long.toHexString(Math.abs(hash));

        // Pad with zeros if shorter than 16 characters
        while (hex.length() < 16) {
            hex = "0" + hex;
        }

        // Keep only last 16 characters if too long
        if (hex.length() > 16) {
            hex = hex.substring(hex.length() - 16);
        }

        return hex;
    }

    public static boolean signUp(String username, String password, String name) {
        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash, name, balance, is_admin, created_at) VALUES (?, ?, ?, 100000.00, FALSE, NOW())";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
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
            if (con != null)  { try { con.close();   } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    public static User login(String username, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
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
            if (rs != null)   { try { rs.close();   } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null)  { try { con.close();   } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
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
