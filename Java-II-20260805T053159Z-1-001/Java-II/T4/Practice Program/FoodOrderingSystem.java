/* Problem Definition for Food Ordering System

#### Overview
The Food Ordering System is designed to facilitate the management of a restaurant's menu and order processing. The system allows users to perform various operations such as adding new food items, retrieving food item details, updating food item information, deleting food items, placing orders, updating order status, and viewing database metadata. This system ensures that the restaurant's menu is well-organized and orders are efficiently processed.

#### Objectives
1. **Add Food Item**: Allow users to add new food items to the menu, including details such as name, category, price, and availability.
2. **Retrieve Food Item**: Enable users to retrieve and view details of a specific food item using its unique identifier (ID).
3. **Update Food Item**: Provide functionality to update the information of an existing food item in the menu.
4. **Delete Food Item**: Allow users to delete a food item from the menu using its unique identifier (ID).
5. **Place Order**: Allow users to place an order for a food item, updating the order details.
6. **Update Order Status**: Allow users to update the status of an order (e.g., pending, completed, canceled).
7. **Database Metadata**: Display metadata about the database, such as product name, version, driver details, URL, and username.
8. **ResultSet Metadata**: Display metadata about the result set, including table name, column count, and column names.
9. **Exit**: Safely close the database connection and exit the program.

#### Functional Requirements
1. **Add Food Item**:
   - Prompt the user for the food item's name, category, price, and availability.
   - Insert the provided details into the "food_items" table in the database.

2. **Retrieve Food Item**:
   - Prompt the user for the food item's unique identifier (ID).
   - Retrieve and display the details of the specified food item from the database.

3. **Update Food Item**:
   - Prompt the user for the food item's unique identifier (ID) and the new details (name, category, price, availability).
   - Update the existing record in the database with the provided information.

4. **Delete Food Item**:
   - Prompt the user for the food item's unique identifier (ID).
   - Delete the specified food item record from the database.

5. **Place Order**:
   - Prompt the user for the food item's unique identifier (ID).
   - Insert order details into the "orders" table.
   - Display a message indicating whether the order was successfully placed.

6. **Update Order Status**:
   - Prompt the user for the order's unique identifier (ID).
   - Prompt the user for the new status (e.g., pending, completed, canceled).
   - Update the order status in the database.

7. **Database Metadata**:
   - Retrieve and display metadata about the database, such as the product name, version, driver name, driver version, URL, and username.

8. **ResultSet Metadata**:
   - Execute a sample query to retrieve data from the "food_items" table.
   - Retrieve and display metadata about the result set, including the table name, column count, and column names.

9. **Exit**:
   - Close the database connection and exit the program.

#### Non-Functional Requirements
1. **Usability**: The system should have a simple and intuitive command-line interface to facilitate easy interaction.
2. **Performance**: The system should respond to user inputs promptly and execute database operations efficiently.
3. **Reliability**: The system should handle errors gracefully and ensure the integrity of the database.
4. **Maintainability**: The system should be designed in a modular fashion to allow easy updates and maintenance.

#### Database Schema
The system uses two tables, "food_items" and "orders," with the following structure:

**food_items**:
- `ID`: INT (Primary Key, Auto Increment) - Unique identifier for each food item.
- `Name`: VARCHAR(100) - Name of the food item.
- `Category`: VARCHAR(100) - Category of the food item.
- `Price`: DOUBLE - Price of the food item.
- `Available`: BOOLEAN - Availability status of the food item.

**orders**:
- `OrderID`: INT (Primary Key, Auto Increment) - Unique identifier for each order.
- `FoodID`: INT - ID of the ordered food item.
- `Quantity`: INT - Quantity of the ordered food item.
- `Status`: VARCHAR(50) - Status of the order (e.g., pending, completed, canceled).

#### Use Cases
1. **Add Food Item**: A manager wants to add a new food item to the restaurant's menu.
2. **Retrieve Food Item**: A manager or user wants to view the details of a specific food item.
3. **Update Food Item**: A manager wants to update the information of an existing food item.
4. **Delete Food Item**: A manager wants to remove a food item from the menu.
5. **Place Order**: A customer wants to place an order for a food item.
6. **Update Order Status**: A manager wants to update the status of an order.
7. **Database Metadata**: A manager or system administrator wants to view metadata about the database.
8. **ResultSet Metadata**: A manager or system administrator wants to view metadata about the data retrieved from the database.
9. **Exit**: A manager or user wants to safely close the system and end the session.

### Java Program

*/
import java.sql.*;
import java.util.Scanner;

public class FoodOrderingSystem {
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
            
            // Create the "food_items" table if it doesn't exist
            String createFoodItemsTableSQL = "CREATE TABLE IF NOT EXISTS food_items (" +
                                             "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                             "Name VARCHAR(100), " +
                                             "Category VARCHAR(100), " +
                                             "Price DOUBLE, " +
                                             "Available BOOLEAN)";
            st.executeUpdate(createFoodItemsTableSQL);
            
            // Create the "orders" table if it doesn't exist
            String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                                          "OrderID INT PRIMARY KEY AUTO_INCREMENT, " +
                                          "FoodID INT, " +
                                          "Quantity INT, " +
                                          "Status VARCHAR(50))";
            st.executeUpdate(createOrdersTableSQL);
            
            // Main menu loop
            while (true) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Add Food Item");
                System.out.println("2. Retrieve Food Item");
                System.out.println("3. Update Food Item");
                System.out.println("4. Delete Food Item");
                System.out.println("5. Place Order");
                System.out.println("6. Update Order Status");
                System.out.println("7. Database Metadata");
                System.out.println("8. ResultSet Metadata");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        addFoodItem(scanner);
                        break;
                    case 2:
                        retrieveFoodItem(scanner);
                        break;
                    case 3:
                        updateFoodItem(scanner);
                        break;
                    case 4:
                        deleteFoodItem(scanner);
                        break;
                    case 5:
                        placeOrder(scanner);
                        break;
                    case 6:
                        updateOrderStatus(scanner);
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

    // Method to add a food item to the database
    private static void addFoodItem(Scanner scanner) {
        try {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();
            System.out.print("Enter Price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter Availability (true/false): ");
            boolean available = scanner.nextBoolean();

            String insertSQL = "INSERT INTO food_items (Name, Category, Price, Available) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setBoolean(4, available);
            pstmt.executeUpdate();
            System.out.println("Food item added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve a food item's information
    private static void retrieveFoodItem(Scanner scanner) {
        try {
            System.out.print("Enter Food Item ID to retrieve: ");
            int id = scanner.nextInt();
            String selectSQL = "SELECT * FROM food_items WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt

.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Category: " + rs.getString("Category"));
                System.out.println("Price: " + rs.getDouble("Price"));
                System.out.println("Available: " + rs.getBoolean("Available"));
            } else {
                System.out.println("Food item not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a food item's information
    private static void updateFoodItem(Scanner scanner) {
        try {
            System.out.print("Enter Food Item ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new Category: ");
            String category = scanner.nextLine();
            System.out.print("Enter new Price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter new Availability (true/false): ");
            boolean available = scanner.nextBoolean();

            String updateSQL = "UPDATE food_items SET Name = ?, Category = ?, Price = ?, Available = ? WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSQL);
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setBoolean(4, available);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Food item updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a food item from the database
    private static void deleteFoodItem(Scanner scanner) {
        try {
            System.out.print("Enter Food Item ID to delete: ");
            int id = scanner.nextInt();
            String deleteSQL = "DELETE FROM food_items WHERE ID = ?";
            PreparedStatement pstmt = con.prepareStatement(deleteSQL);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Food item deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to place an order
    private static void placeOrder(Scanner scanner) {
        try {
            System.out.print("Enter Food Item ID to order: ");
            int foodId = scanner.nextInt();
            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();

            String insertOrderSQL = "INSERT INTO orders (FoodID, Quantity, Status) VALUES (?, ?, 'pending')";
            PreparedStatement pstmt = con.prepareStatement(insertOrderSQL);
            pstmt.setInt(1, foodId);
            pstmt.setInt(2, quantity);
            pstmt.executeUpdate();
            System.out.println("Order placed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update order status
    private static void updateOrderStatus(Scanner scanner) {
        try {
            System.out.print("Enter Order ID to update: ");
            int orderId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new Status (pending/completed/canceled): ");
            String status = scanner.nextLine();

            String updateOrderSQL = "UPDATE orders SET Status = ? WHERE OrderID = ?";
            PreparedStatement pstmt = con.prepareStatement(updateOrderSQL);
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            System.out.println("Order status updated successfully.");
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
            String query = "SELECT * FROM food_items";
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

