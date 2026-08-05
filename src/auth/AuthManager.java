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
     *   - Multiplies a running total by 31 (standard mixing trick)
     *   - Adds the character's ASCII value and its position
     *   - Returns the result as a plain number string
     *
     * Note: This is a simple hash for learning purposes, not for real security.
     */
    public static String hashPassword(String password) {
        long hash = 0;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            hash = (hash * 31) + (int) c + (i + 1);
        }

        // Same password always gives same number — store it as a string
        return String.valueOf(hash);
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
