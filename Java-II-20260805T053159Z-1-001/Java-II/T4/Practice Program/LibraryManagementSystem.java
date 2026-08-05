/*
*** Problem Definition for Library Management System with Book Issue and Return Functionality

**** Overview
The Library Management System is designed to help manage the library's book inventory efficiently. The system allows users to perform various operations such as adding new books, retrieving book details, updating book information, deleting books, issuing books to users, returning books, and viewing database metadata. This system ensures that the library's records are well-organized and easily accessible, thereby improving the overall management and operation of the library.

**** Objectives
1. **Add Book**: Allow users to add new books to the library database, including details such as title, author, publisher, year of publication, and quantity.
2. **Retrieve Book**: Enable users to retrieve and view details of a specific book using its unique identifier (ID).
3. **Update Book**: Provide functionality to update the information of an existing book in the database.
4. **Delete Book**: Allow users to delete a book from the database using its unique identifier (ID).
5. **Issue Book**: Allow users to issue a book to a borrower, updating the quantity available.
6. **Return Book**: Allow users to return a book, updating the quantity available.
7. **Database Metadata**: Display metadata about the database, such as product name, version, driver details, URL, and username.
8. **ResultSet Metadata**: Display metadata about the result set, including table name, column count, and column names.
9. **Exit**: Safely close the database connection and exit the program.

**** Functional Requirements
1. **Add Book**:
   - Prompt the user for the book's title, author, publisher, year of publication, and quantity.
   - Insert the provided details into the "books" table in the database.

2. **Retrieve Book**:
   - Prompt the user for the book's unique identifier (ID).
   - Retrieve and display the details of the specified book from the database.

3. **Update Book**:
   - Prompt the user for the book's unique identifier (ID) and the new details (title, author, publisher, year, quantity).
   - Update the existing record in the database with the provided information.

4. **Delete Book**:
   - Prompt the user for the book's unique identifier (ID).
   - Delete the specified book record from the database.

5. **Issue Book**:
   - Prompt the user for the book's unique identifier (ID).
   - Check if the book is available (quantity > 0).
   - Decrement the quantity by 1 if the book is available.
   - Display a message indicating whether the book was successfully issued or not.

6. **Return Book**:
   - Prompt the user for the book's unique identifier (ID).
   - Increment the quantity by 1.
   - Display a message indicating the book was successfully returned.

7. **Database Metadata**:
   - Retrieve and display metadata about the database, such as the product name, version, driver name, driver version, URL, and username.

8. **ResultSet Metadata**:
   - Execute a sample query to retrieve data from the "books" table.
   - Retrieve and display metadata about the result set, including the table name, column count, and column names.

9. **Exit**:
   - Close the database connection and exit the program.

**** Non-Functional Requirements
1. **Usability**: The system should have a simple and intuitive command-line interface to facilitate easy interaction.
2. **Performance**: The system should respond to user inputs promptly and execute database operations efficiently.
3. **Reliability**: The system should handle errors gracefully and ensure the integrity of the database.
4. **Maintainability**: The system should be designed in a modular fashion to allow easy updates and maintenance.

**** Database Schema
The system uses a single table, "books," with the following structure:
- `ID`: INT (Primary Key, Auto Increment) - Unique identifier for each book.
- `Title`: VARCHAR(100) - Title of the book.
- `Author`: VARCHAR(100) - Author of the book.
- `Publisher`: VARCHAR(100) - Publisher of the book.
- `Year`: INT - Year of publication.
- `QTY`: INT - Quantity of the book available in the library.

By implementing this Library Management System, libraries can manage their book inventory more efficiently and provide better service to users. The system streamlines the process of adding, retrieving, updating, and deleting book records, issuing and returning books, ensuring that the library's inventory is always accurate and up-to-date.

*** Java Program
*/
```java
import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
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
            
            // Create the "books" table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                                    "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                    "Title VARCHAR(100), " +
                                    "Author VARCHAR(100), " +
                                    "Publisher VARCHAR(100), " +
                                    "Year INT, " +
                                    "QTY INT)";
            st.executeUpdate(createTableSQL);
            
            // Main menu loop
            while (true) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Add Book");
                System.out.println("2. Retrieve Book");
                System.out.println("3. Update Book");
                System.out.println("4. Delete Book");
                System.out.println("5. Issue Book");
                System.out.println("6. Return Book");
                System.out.println("7. Database Metadata");
                System.out.println("8. ResultSet Metadata");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        addBook(scanner);
                        break;
                    case 2:
                        retrieveBook(scanner);
                        break;
                    case 3:
                        updateBook(scanner);
                        break;
                    case 4:
                        deleteBook(scanner);
                        break;
                    case 5:
                        issueBook(scanner);
                        break;
                    case 6:
                        returnBook(scanner);
                        break;
                    case 7:
                        printDatabaseMetadata();
                        break;
                    case 8:
                        printResultSetMetadata();
                        break;
                    case 9:
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

    // Method to add a book to the database
    private static void addBook(Scanner scanner) {
        try {
            System.out.print("Enter Title: ");
            String title = scanner.nextLine();
            System.out.print("Enter Author: ");
            String author = scanner.nextLine();
            System.out.print("Enter Publisher: ");
            String publisher = scanner.nextLine();
            System.out.print("Enter Year: ");
            int year = scanner.nextInt();
            System.out.print("Enter Quantity: ");
            int qty = scanner.nextInt();

            String insertSQL = "INSERT INTO books (Title, Author, Publisher, Year, QTY) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, year);
            pstmt.setInt(5, qty);
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve a book's information
    private static void retrieveBook(Scanner scanner) {
        try {
            System.out.print("Enter Book ID to retrieve: ");
            int id = scanner.nextInt();
            String selectSQL = "SELECT * FROM books WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID"));
                System.out.println("Title: " + rs.getString("Title"));
                System.out.println("Author: " + rs.getString("Author"));
                System.out.println("Publisher: " + rs.getString("Publisher"));
                System.out.println("

Year: " + rs.getInt("Year"));
                System.out.println("Quantity: " + rs.getInt("QTY"));
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a book's information
    private static void updateBook(Scanner scanner) {
        try {
            System.out.print("Enter Book ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new Title: ");
            String title = scanner.nextLine();
            System.out.print("Enter new Author: ");
            String author = scanner.nextLine();
            System.out.print("Enter new Publisher: ");
            String publisher = scanner.nextLine();
            System.out.print("Enter new Year: ");
            int year = scanner.nextInt();
            System.out.print("Enter new Quantity: ");
            int qty = scanner.nextInt();

            String updateSQL = "UPDATE books SET Title = ?, Author = ?, Publisher = ?, Year = ?, QTY = ? WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSQL);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, year);
            pstmt.setInt(5, qty);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            System.out.println("Book updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a book from the database
    private static void deleteBook(Scanner scanner) {
        try {
            System.out.print("Enter Book ID to delete: ");
            int id = scanner.nextInt();
            String deleteSQL = "DELETE FROM books WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(deleteSQL);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Book deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to issue a book to a borrower
    private static void issueBook(Scanner scanner) {
        try {
            System.out.print("Enter Book ID to issue: ");
            int id = scanner.nextInt();
            String selectSQL = "SELECT QTY FROM books WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int qty = rs.getInt("QTY");
                if (qty > 0) {
                    String updateSQL = "UPDATE books SET QTY = QTY - 1 WHERE ID = ?";
                    pstmt = con.prepareStatement(updateSQL);
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    System.out.println("Book issued successfully.");
                } else {
                    System.out.println("Book not available.");
                }
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to return a book
    private static void returnBook(Scanner scanner) {
        try {
            System.out.print("Enter Book ID to return: ");
            int id = scanner.nextInt();
            String updateSQL = "UPDATE books SET QTY = QTY + 1 WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSQL);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print database metadata
    private static void printDatabaseMetadata() {
        try {
            DatabaseMetaData dbMetaData = con.getMetaData();
            System.out.println("Database Product Name: " + dbMetaData.getDatabaseProductName());
            System.out.println("Database Product Version: " + dbMetaData.getDatabaseProductVersion());
            System.out.println("Database Driver Name: " + dbMetaData.getDriverName());
            System.out.println("Database Driver Version: " + dbMetaData.getDriverVersion());
            System.out.println("Database URL: " + dbMetaData.getURL());
            System.out.println("Database Username: " + dbMetaData.getUserName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print result set metadata
    private static void printResultSetMetadata() {
        try {
            String query = "SELECT * FROM books";
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsMetaData = rs.getMetaData();

            System.out.println("Table Name: " + rsMetaData.getTableName(1));
            System.out.println("Column Count: " + rsMetaData.getColumnCount());

            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                System.out.println("Column " + i + ": " + rsMetaData.getColumnName(i) + " (" + rsMetaData.getColumnTypeName(i) + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


