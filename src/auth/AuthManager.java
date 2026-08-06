package auth;

import db.DatabaseManager;
import model.User;

import java.sql.*;

public class AuthManager {

    /**
     * Validates a password against the project's signup rules.
     * Rules:
     *   - At least 8 characters long
     *   - At least one uppercase letter (A-Z)
     *   - At least one lowercase letter (a-z)
     *   - At least one digit (0-9)
     *
     * Returns null if valid; returns an error message string if invalid.
     * (Returning a message instead of throwing keeps it simple for Semester 2.)
     */
    public static String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter=false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c))  hasDigit = true;
            else if (Character.isLetterOrDigit(c))  hasSpecialCharacter = true;
        }
        if (!hasUpper) return "Password must contain at least one uppercase letter.";
        if (!hasLower) return "Password must contain at least one lowercase letter.";
        if (!hasDigit) return "Password must contain at least one digit.";
        if (!hasSpecialCharacter) return "Password must contain at least one special character digit.";
        return null; // null = valid
    }

    /**
     * Registers a new user after validating the password.
     * Passwords are stored as plain text (appropriate for this Semester 2 learning project).
     *
     * @return true on success, false if the username is taken or the password is invalid.
     */
    public static boolean signUp(String username, String password, String name) {
        String validationError = validatePassword(password);
        if (validationError != null) {
            System.out.println("Sign up failed: " + validationError);
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, name, balance, is_admin, created_at) VALUES (?, ?, ?, 100000.00, FALSE, NOW())";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // plain text
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Sign up failed: " + e.getMessage());
            return false;
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null)   { try { con.close();   } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    /**
     * Attempts to log in a regular (non-admin) user.
     * Compares the supplied password directly against the stored plain-text value.
     *
     * @return the User object on success, or null on failure.
     */
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // plain text comparison
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
            if (rs != null)    { try { rs.close();    } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null)   { try { con.close();   } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return null;
    }

    /**
     * Attempts to log in as an admin user.
     * Returns the User only if the credentials are valid AND the account has is_admin = true.
     */
    public static User adminLogin(String username, String password) {
        User user = login(username, password);
        if (user != null && user.isAdmin()) {
            return user;
        }
        return null;
    }
}
