BEFORE RUNNING ANY JAVA CODE, ensure that you have the MySQL JDBC driver JAR file in your classpath/reference library.

//JDBC Connectivity	
//Write the java program that do connection with database named "LJU"

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(url, username, password);

        if (connection != null) {
            System.out.println("Connected to the database!");
            connection.close();
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}
















-----------

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentCRUD {
    // Database configuration - replace with your database name, username, and password
    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        
        // 1. Establish the connection using Try-with-resources to prevent memory leaks
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to MySQL database successfully.\n");

            // ==========================================
            // OPERATION 1: CREATE TABLE
            // ==========================================
            String createQuery = "CREATE TABLE IF NOT EXISTS student ("
                               + "sid INT PRIMARY KEY, "
                               + "sname VARCHAR(100) NOT NULL, "
                               + "smark DECIMAL(4,1) NOT NULL"
                               + ")";
            stmt.executeUpdate(createQuery);
            System.out.println("[1] Table 'student' created or already exists.");

            // ==========================================
            // OPERATION 2: 3 INSERT QUERIES
            // ==========================================
            String insert1 = "INSERT INTO student (sid, sname, smark) VALUES (1, 'Alice', 85.5)";
            String insert2 = "INSERT INTO student (sid, sname, smark) VALUES (2, 'Bob', 20.5)";
            String insert3 = "INSERT INTO student (sid, sname, smark) VALUES (3, 'Charlie', 92.0)";
            
            // Using executeUpdate for each insert statement
            stmt.executeUpdate(insert1);
            stmt.executeUpdate(insert2);
            stmt.executeUpdate(insert3);
            System.out.println("[2] 3 Student records inserted successfully.");

            // ==========================================
            // OPERATION 3: 1 UPDATE QUERY
            // ==========================================
            // Updating Bob's mark from 20.5 to 95.0
            String updateQuery = "UPDATE student SET smark = 95.0 WHERE sid = 2";
            int rowsUpdated = stmt.executeUpdate(updateQuery);
            System.out.println("[3] Update complete. Rows affected: " + rowsUpdated);

            // ==========================================
            // OPERATION 4: 1 DELETE QUERY
            // ==========================================
            // Removing Charlie from the database
            String deleteQuery = "DELETE FROM student WHERE sid = 3";
            int rowsDeleted = stmt.executeUpdate(deleteQuery);
            System.out.println("[4] Delete complete. Rows affected: " + rowsDeleted);

            // ==========================================
            // OPERATION 5: 1 SELECT * WITH RESULT SET
            // ==========================================
            System.out.println("\n[5] Fetching current database records:");
            String selectQuery = "SELECT * FROM student";
            
            // executeQuery returns a ResultSet containing the database data
            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                // Print table header borders
                System.out.println("------------------------------------");
                System.out.printf("%-5s | %-15s | %-5s%n", "SID", "SNAME", "SMARK");
                System.out.println("------------------------------------");
                
                // Loop through row-by-row until no rows are left
                while (rs.next()) {
                    int id = rs.getInt("sid");
                    String name = rs.getString("sname");
                    double mark = rs.getDouble("smark");
                    
                    System.out.printf("%-5d | %-15s | %-5.1f%n", id, name, mark);
                }
                System.out.println("------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println("A database error occurred!");
            e.printStackTrace();
        }
    }
}
