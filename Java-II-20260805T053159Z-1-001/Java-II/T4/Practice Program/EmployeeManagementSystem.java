
/*
 here are five sample employee records 

| ID  | Name             | Department   | Designation          | Profile Image |
| --- | ---------------- | ------------ | -------------------- | ------------- |
| 1   | Rajesh Sharma    | HR           | Manager              | NULL          |
| 2   | Priya Patel      | Finance      | Accountant           | NULL          |
| 3   | Sanjay Verma     | Engineering  | Software Developer   | NULL          |
| 4   | Nandini Gupta    | Marketing    | Marketing Specialist | NULL          |
| 5   | Arjun Khanna     | Sales        | Sales Representative | NULL          |

You can use these sample records to populate your "employees" table in XAMPP MySQL and test your Employee Management System.

1. Import necessary Java libraries:
   - for database operations.
   - for file handling.
   - for user input.

2. Declare a Java class named `EmployeeManagementSystem`.

3. Inside the class, declare two static variables:
   - `Connection con` for database connection.
   - `Statement st` for creating SQL statements.

4. Define the `main` method, the entry point of the program:
   - Set up database connection parameters (URL, username, password).
   - Establish a connection to the MySQL database.
   - Create the "employees" table if it doesn't exist.
   - Enter an infinite loop to display a menu and wait for user input.

5. Display the main menu with the following options:
   - Add Employee
   - Retrieve Employee
   - Delete Employee
   - Database Metadata
   - ResultSet Metadata
   - Exit

6. Read the user's choice using a `Scanner`.

7. Use a `switch` statement to handle user choices:
   - Case 1: Call `addEmployee()` to add a new employee to the database.
   - Case 2: Call `retrieveEmployee()` to retrieve employee information.
   - Case 3: Call `deleteEmployee()` to delete an employee from the database.
   - Case 4: Call `printDatabaseMetadata()` to display database metadata.
   - Case 5: Call `printResultSetMetadata()` to display result set metadata.
   - Case 6: Close the database connection and exit the program.

8. Implement the `addEmployee()` method to:
   - Prompt the user for employee details (name, department, designation).
   - Prompt for the path to the employee's profile image.
   - Prepare an SQL statement to insert the employee's data into the database, including the profile image as a BLOB.

9. Implement the `retrieveEmployee()` method to:
   - Prompt the user for an employee ID.
   - Prepare an SQL statement to select the employee's data from the database.
   - Retrieve the profile image and save it as a file.
   - Display the employee's information and the saved profile image's filename.

10. Implement the `deleteEmployee()` method to:
    - Prompt the user for an employee ID to delete.
    - Prepare an SQL statement to delete the employee's data from the database.

11. Implement the `printDatabaseMetadata()` method to:
    - Retrieve and display database metadata, including product name, username, and URL.

12. Implement the `printResultSetMetadata()` method to:
    - Execute a sample query to the database.
    - Retrieve and display metadata about the result set, including table name, column count, and column names.

13. The program allows users to perform basic employee management tasks, retrieve database metadata, and retrieve result set metadata.

14. Users can exit the program by choosing the "Exit" option, which closes the database connection and exits the program.

 create your own Java program for an Employee Management System with required functionalities and features.
*/

import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class EmployeeManagementSystem {
    static Connection con;
    static Statement st;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Database connection parameters
            String url = "jdbc:mysql://localhost:3306/your_database_name";
            String username = "your_username";
            String password = "your_password";
            
            // Establish a connection to the MySQL database
            con = DriverManager.getConnection(url, username, password);
            st = con.createStatement();
            
            // Create the "employees" table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                                    "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                    "Name VARCHAR(50), " +
                                    "Department VARCHAR(50), " +
                                    "Designation VARCHAR(50), " +
                                    "ProfileImage LONGBLOB)";
            st.executeUpdate(createTableSQL);
            
            // Main menu loop
            while (true) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Add Employee");
                System.out.println("2. Retrieve Employee");
                System.out.println("3. Delete Employee");
                System.out.println("4. Database Metadata");
                System.out.println("5. ResultSet Metadata");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        addEmployee(scanner);
                        break;
                    case 2:
                        retrieveEmployee(scanner);
                        break;
                    case 3:
                        deleteEmployee(scanner);
                        break;
                    case 4:
                        printDatabaseMetadata();
                        break;
                    case 5:
                        printResultSetMetadata();
                        break;
                    case 6:
                        con.close();
                        System.out.println("Connection closed. Exiting program.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add an employee to the database
    private static void addEmployee(Scanner scanner) {
        try {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Department: ");
            String department = scanner.nextLine();
            System.out.print("Enter Designation: ");
            String designation = scanner.nextLine();
            System.out.print("Enter path to Profile Image: ");
            String imagePath = scanner.nextLine();

            String insertSQL = "INSERT INTO employees (Name, Department, Designation, ProfileImage) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            pstmt.setString(1, name);
            pstmt.setString(2, department);
            pstmt.setString(3, designation);
            if (!imagePath.isEmpty()) {
                File image = new File(imagePath);
                FileInputStream fis = new FileInputStream(image);
                pstmt.setBinaryStream(4, fis, (int) image.length());
            } else {
                pstmt.setNull(4, Types.BLOB);
            }
            pstmt.executeUpdate();
            System.out.println("Employee added successfully.");
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve an employee's information
    private static void retrieveEmployee(Scanner scanner) {
        try {
            System.out.print("Enter Employee ID to retrieve: ");
            int id = scanner.nextInt();
            String selectSQL = "SELECT * FROM employees WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Department: " + rs.getString("Department"));
                System.out.println("Designation: " + rs.getString("Designation"));
                
                Blob blob = rs.getBlob("ProfileImage");
                if (blob != null) {
                    InputStream is = blob.getBinaryStream();
                    OutputStream os = new FileOutputStream("profile_image_" + id + ".jpg");
                    byte[] buffer = new byte[1024];
                    while (is.read(buffer) > 0) {
                        os.write(buffer);
                    }
                    is.close();
                    os.close();
                    System.out.println("Profile Image saved as profile_image_" + id + ".jpg");
                } else {
                    System.out.println("Profile Image: NULL");
                }
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to delete an employee from the database
    private static void deleteEmployee(Scanner scanner) {
        try {
            System.out.print("Enter Employee ID to delete: ");
            int id = scanner.nextInt();
            String deleteSQL = "DELETE FROM employees WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(deleteSQL);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print database metadata
    private static void printDatabaseMetadata() {
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            System.out.println("Database Product Name: " + dbmd.getDatabaseProductName());
            System.out.println("Database Product Version: " + dbmd.getDatabaseProductVersion());
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());
            System.out.println("URL: " + dbmd.getURL());
            System.out.println("Username: " + dbmd.getUserName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print ResultSet metadata
    private static void printResultSetMetadata() {
        try {
            String querySQL = "SELECT * FROM employees";
            PreparedStatement pstmt = con.prepareStatement(querySQL);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("Table Name: " + rsmd.getTableName(1));
            System.out.println("Column Count: " + rsmd.getColumnCount());
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println("Column " + i + ": " + rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
