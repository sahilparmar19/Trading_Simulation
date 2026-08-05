/* Problem Definition for Train Ticket Booking System

#### Overview
The Train Ticket Booking System is designed to facilitate the management of train schedules, bookings, cancellations, and updates. This system provides functionalities similar to IRCTC, allowing users to book, cancel, and update train tickets. The system also manages train details, including source and destination stations.

#### Objectives
1. **Add Train**: Allow users to add new trains with details like train number, source, destination, and available seats.
2. **Retrieve Train**: Enable users to retrieve and view details of a specific train using its unique identifier (train number).
3. **Update Train**: Provide functionality to update the details of an existing train, such as source, destination, and available seats.
4. **Delete Train**: Allow users to remove a train from the system.
5. **Book Ticket**: Allow users to book tickets for a specific train.
6. **Cancel Booking**: Allow users to cancel an existing booking.
7. **Update Booking**: Allow users to update booking details, such as the number of seats.
8. **View Booking Details**: Enable users to view details of their bookings.
9. **Database Metadata**: Display metadata about the database, such as product name, version, driver details, URL, and username.
10. **ResultSet Metadata**: Display metadata about the result set, including table name, column count, and column names.
11. **Exit**: Safely close the database connection and exit the program.

#### Functional Requirements
1. **Add Train**:
   - Prompt the user for the train number, source, destination, and available seats.
   - Insert the provided details into the "trains" table in the database.

2. **Retrieve Train**:
   - Prompt the user for the train number.
   - Retrieve and display the details of the specified train from the database.

3. **Update Train**:
   - Prompt the user for the train number and the new details (source, destination, available seats).
   - Update the existing record in the database with the new information.

4. **Delete Train**:
   - Prompt the user for the train number.
   - Delete the specified train record from the database.

5. **Book Ticket**:
   - Prompt the user for the train number and the number of seats to book.
   - Insert booking details into the "bookings" table.
   - Update the available seats in the "trains" table.

6. **Cancel Booking**:
   - Prompt the user for the booking ID.
   - Delete the booking record from the "bookings" table.
   - Update the available seats in the "trains" table.

7. **Update Booking**:
   - Prompt the user for the booking ID and the new number of seats.
   - Update the booking record in the "bookings" table.
   - Adjust the available seats in the "trains" table accordingly.

8. **View Booking Details**:
   - Prompt the user for the booking ID.
   - Retrieve and display the details of the specified booking from the "bookings" table.

9. **Database Metadata**:
   - Retrieve and display metadata about the database, such as the product name, version, driver name, driver version, URL, and username.

10. **ResultSet Metadata**:
    - Execute a sample query to retrieve data from the "trains" table.
    - Retrieve and display metadata about the result set, including the table name, column count, and column names.

11. **Exit**:
    - Close the database connection and exit the program.

#### Non-Functional Requirements
1. **Usability**: The system should have a user-friendly command-line interface.
2. **Performance**: The system should handle multiple bookings and cancellations efficiently.
3. **Reliability**: The system should handle errors and ensure data integrity.
4. **Maintainability**: The system should be designed to allow easy updates and maintenance.

#### Database Schema
The system uses two tables: "trains" and "bookings".

**trains**:
- `TrainNumber`: INT (Primary Key) - Unique identifier for each train.
- `Source`: VARCHAR(100) - Source station.
- `Destination`: VARCHAR(100) - Destination station.
- `AvailableSeats`: INT - Number of available seats on the train.

**bookings**:
- `BookingID`: INT (Primary Key, Auto Increment) - Unique identifier for each booking.
- `TrainNumber`: INT - Train number for the booking.
- `SeatsBooked`: INT - Number of seats booked.
- `BookingDate`: DATE - Date of the booking.

### Java Program
*/

import java.sql.*;
import java.util.Scanner;

public class TrainTicketBookingSystem {
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
            
            // Create the "trains" table if it doesn't exist
            String createTrainsTableSQL = "CREATE TABLE IF NOT EXISTS trains (" +
                                          "TrainNumber INT PRIMARY KEY, " +
                                          "Source VARCHAR(100), " +
                                          "Destination VARCHAR(100), " +
                                          "AvailableSeats INT)";
            st.executeUpdate(createTrainsTableSQL);
            
            // Create the "bookings" table if it doesn't exist
            String createBookingsTableSQL = "CREATE TABLE IF NOT EXISTS bookings (" +
                                             "BookingID INT PRIMARY KEY AUTO_INCREMENT, " +
                                             "TrainNumber INT, " +
                                             "SeatsBooked INT, " +
                                             "BookingDate DATE)";
            st.executeUpdate(createBookingsTableSQL);
            
            // Main menu loop
            while (true) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Add Train");
                System.out.println("2. Retrieve Train");
                System.out.println("3. Update Train");
                System.out.println("4. Delete Train");
                System.out.println("5. Book Ticket");
                System.out.println("6. Cancel Booking");
                System.out.println("7. Update Booking");
                System.out.println("8. View Booking Details");
                System.out.println("9. Database Metadata");
                System.out.println("10. ResultSet Metadata");
                System.out.println("11. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        addTrain(scanner);
                        break;
                    case 2:
                        retrieveTrain(scanner);
                        break;
                    case 3:
                        updateTrain(scanner);
                        break;
                    case 4:
                        deleteTrain(scanner);
                        break;
                    case 5:
                        bookTicket(scanner);
                        break;
                    case 6:
                        cancelBooking(scanner);
                        break;
                    case 7:
                        updateBooking(scanner);
                        break;
                    case 8:
                        viewBookingDetails(scanner);
                        break;
                    case 9:
                        printDatabaseMetadata();
                        break;
                    case 10:
                        printResultSetMetadata();
                        break;
                    case 11:
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

    // Method to add a train to the database
    static void addTrain(Scanner scanner) {
        try {
            System.out.print("Enter Train Number: ");
            int trainNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Source: ");
            String source = scanner.nextLine();
            System.out.print("Enter Destination: ");
            String destination = scanner.nextLine();
            System.out.print("Enter Available Seats: ");
            int availableSeats = scanner.nextInt();

            String insertSQL = "INSERT INTO trains (TrainNumber, Source, Destination, AvailableSeats) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            pstmt.setInt(1, trainNumber);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.setInt(4, availableSeats);
            pstmt.executeUpdate();
            System.out.println("Train added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve a train's information
    static void retrieveTrain(Scanner scanner) {
        try {
            System.out.print("Enter Train Number to retrieve: ");
            int trainNumber = scanner.nextInt();
            String selectSQL = "SELECT * FROM trains WHERE TrainNumber = ?";
            PreparedStatement pstmt = con.prepareStatement(selectSQL);
            pstmt.setInt(1, trainNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Train Number: " + rs.getInt("TrainNumber"));
                System.out.println("Source: " + rs.getString("Source"));
                System.out.println("Destination: " + rs.getString("Destination"));
                System.out.println("Available Seats: " + rs.getInt("AvailableSeats"));
            } else {
                System.out.println("Train not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a train's information
    static void updateTrain(Scanner scanner) {
        try {
            System.out.print("Enter Train Number to update: ");
            int trainNumber =

 scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new Source: ");
            String source = scanner.nextLine();
            System.out.print("Enter new Destination: ");
            String destination = scanner.nextLine();
            System.out.print("Enter new Available Seats: ");
            int availableSeats = scanner.nextInt();

            String updateSQL = "UPDATE trains SET Source = ?, Destination = ?, AvailableSeats = ? WHERE TrainNumber = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSQL);
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            pstmt.setInt(3, availableSeats);
            pstmt.setInt(4, trainNumber);
            pstmt.executeUpdate();
            System.out.println("Train updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a train from the database
    static void deleteTrain(Scanner scanner) {
        try {
            System.out.print("Enter Train Number to delete: ");
            int trainNumber = scanner.nextInt();
            String deleteSQL = "DELETE FROM trains WHERE TrainNumber = ?";
            PreparedStatement pstmt = con.prepareStatement(deleteSQL);
            pstmt.setInt(1, trainNumber);
            pstmt.executeUpdate();
            System.out.println("Train deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to book a ticket
    static void bookTicket(Scanner scanner) {
        try {
            System.out.print("Enter Train Number to book: ");
            int trainNumber = scanner.nextInt();
            System.out.print("Enter Number of Seats to book: ");
            int seats = scanner.nextInt();

            // Check available seats
            String checkSeatsSQL = "SELECT AvailableSeats FROM trains WHERE TrainNumber = ?";
            PreparedStatement pstmt = con.prepareStatement(checkSeatsSQL);
            pstmt.setInt(1, trainNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int availableSeats = rs.getInt("AvailableSeats");
                if (availableSeats >= seats) {
                    // Book the ticket
                    String insertBookingSQL = "INSERT INTO bookings (TrainNumber, SeatsBooked, BookingDate) VALUES (?, ?, CURDATE())";
                    pstmt = con.prepareStatement(insertBookingSQL);
                    pstmt.setInt(1, trainNumber);
                    pstmt.setInt(2, seats);
                    pstmt.executeUpdate();

                    // Update available seats
                    String updateSeatsSQL = "UPDATE trains SET AvailableSeats = AvailableSeats - ? WHERE TrainNumber = ?";
                    pstmt = con.prepareStatement(updateSeatsSQL);
                    pstmt.setInt(1, seats);
                    pstmt.setInt(2, trainNumber);
                    pstmt.executeUpdate();

                    System.out.println("Ticket booked successfully.");
                } else {
                    System.out.println("Not enough seats available.");
                }
            } else {
                System.out.println("Train not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to cancel a booking
    static void cancelBooking(Scanner scanner) {
        try {
            System.out.print("Enter Booking ID to cancel: ");
            int bookingId = scanner.nextInt();

            // Retrieve booking details
            String selectBookingSQL = "SELECT TrainNumber, SeatsBooked FROM bookings WHERE BookingID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectBookingSQL);
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int trainNumber = rs.getInt("TrainNumber");
                int seatsBooked = rs.getInt("SeatsBooked");

                // Delete the booking
                String deleteBookingSQL = "DELETE FROM bookings WHERE BookingID = ?";
                pstmt = con.prepareStatement(deleteBookingSQL);
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();

                // Update available seats
                String updateSeatsSQL = "UPDATE trains SET AvailableSeats = AvailableSeats + ? WHERE TrainNumber = ?";
                pstmt = con.prepareStatement(updateSeatsSQL);
                pstmt.setInt(1, seatsBooked);
                pstmt.setInt(2, trainNumber);
                pstmt.executeUpdate();

                System.out.println("Booking canceled successfully.");
            } else {
                System.out.println("Booking not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a booking
    static void updateBooking(Scanner scanner) {
        try {
            System.out.print("Enter Booking ID to update: ");
            int bookingId = scanner.nextInt();
            System.out.print("Enter new number of seats: ");
            int newSeats = scanner.nextInt();

            // Retrieve current booking details
            String selectBookingSQL = "SELECT TrainNumber, SeatsBooked FROM bookings WHERE BookingID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectBookingSQL);
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int trainNumber = rs.getInt("TrainNumber");
                int oldSeats = rs.getInt("SeatsBooked");

                // Update the booking
                String updateBookingSQL = "UPDATE bookings SET SeatsBooked = ? WHERE BookingID = ?";
                pstmt = con.prepareStatement(updateBookingSQL);
                pstmt.setInt(1, newSeats);
                pstmt.setInt(2, bookingId);
                pstmt.executeUpdate();

                // Adjust available seats
                String updateSeatsSQL = "UPDATE trains SET AvailableSeats = AvailableSeats + ? - ? WHERE TrainNumber = ?";
                pstmt = con.prepareStatement(updateSeatsSQL);
                pstmt.setInt(1, oldSeats);
                pstmt.setInt(2, newSeats);
                pstmt.setInt(3, trainNumber);
                pstmt.executeUpdate();

                System.out.println("Booking updated successfully.");
            } else {
                System.out.println("Booking not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to view booking details
    static void viewBookingDetails(Scanner scanner) {
        try {
            System.out.print("Enter Booking ID to view details: ");
            int bookingId = scanner.nextInt();
            String selectBookingSQL = "SELECT * FROM bookings WHERE BookingID = ?";
            PreparedStatement pstmt = con.prepareStatement(selectBookingSQL);
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("BookingID"));
                System.out.println("Train Number: " + rs.getInt("TrainNumber"));
                System.out.println("Seats Booked: " + rs.getInt("SeatsBooked"));
                System.out.println("Booking Date: " + rs.getDate("BookingDate"));
            } else {
                System.out.println("Booking not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print database metadata
    static void printDatabaseMetadata() {
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
    static void printResultSetMetadata() {
        try {
            String query = "SELECT * FROM trains";
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