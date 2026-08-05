/* =============ONLY FOR UNDERSTANDING PURPOSE OF T3 CONCEPTS=========
Certainly! Here's a structured definition of the flight management system in an instructional format:

### Flight Management System Definition

#### Classes and Data Structures

1. **Flight Class**
   - **Attributes:** `flightNumber` (String), `origin` (String), `destination` (String)
   - **Methods:**
     - Constructor: Initializes `flightNumber`, `origin`, and `destination`.
     - Getters and setters for `flightNumber`, `origin`, and `destination`.
     - `toString()` method to return a string representation of the flight details.

2. **Customer Class**
   - **Attributes:** `customerId` (String), `name` (String)
   - **Methods:**
     - Constructor: Initializes `customerId` and `name`.
     - Getters and setters for `customerId` and `name`.
     - `toString()` method to return a string representation of the customer details.

3. **Booking Class**
   - **Attributes:** `bookingId` (String), `customer` (Customer), `flight` (Flight), `seatNumber` (String)
   - **Methods:**
     - Constructor: Initializes `bookingId`, `customer`, `flight`, and `seatNumber`.
     - Getters and setters for `bookingId`, `customer`, `flight`, and `seatNumber`.
     - `toString()` method to return a string representation of the booking details.

4. **FlightManager Class**
   - **Attributes:** 
     - `flights`: `HashMap` to store flights (`flightNumber` as key, `Flight` as value).
     - `customers`: `HashSet` to store customers.
     - `bookingsQueue`: `ArrayDeque` to manage bookings in FIFO order.
     - `bookingsPriorityQueue`: `PriorityQueue` to manage bookings based on booking ID.
   - **Methods:**
     - `addFlight(String flightNumber, String origin, String destination)`: Adds a new flight to the `flights` map.
     - `getFlight(String flightNumber)`: Retrieves a flight from the `flights` map based on `flightNumber`.
     - `listFlights()`: Prints details of all flights stored in the `flights` map.
     - `addCustomer(String customerId, String name)`: Adds a new customer to the `customers` set.
     - `getCustomer(String customerId)`: Retrieves a customer from the `customers` set based on `customerId`.
     - `listCustomers()`: Prints details of all customers stored in the `customers` set.
     - `bookCustomer(String bookingId, Customer customer, Flight flight, String seatNumber)`: Books a customer on a flight and adds the booking to both `bookingsQueue` and `bookingsPriorityQueue`.
     - `listBookings()`: Prints details of all bookings stored in the `bookingsQueue`.
   
5. **FileManager Class**
   - **Attributes:** Constants for file paths (`FLIGHTS_FILE`, `CUSTOMERS_FILE`, `BOOKINGS_FILE`).
   - **Methods:**
     - `saveFlights(Map<String, Flight> flights)`: Saves flights data to a text file (`FLIGHTS_FILE`).
     - `loadFlights()`: Loads flights data from the text file (`FLIGHTS_FILE`) into a `HashMap`.
     - `saveCustomers(Set<Customer> customers)`: Saves customers data to a text file (`CUSTOMERS_FILE`).
     - `loadCustomers()`: Loads customers data from the text file (`CUSTOMERS_FILE`) into a `HashSet`.
     - `saveBookings(Queue<Booking> bookings)`: Saves bookings data to a text file (`BOOKINGS_FILE`).
     - `loadBookings()`: Loads bookings data from the text file (`BOOKINGS_FILE`) into a `PriorityQueue`.

#### Vacation Class (`Vacation.java`)

- **Vacation Class:**
  - Acts as the entry point for the flight management system.
  - Initializes a `FlightManager` instance.
  - Loads initial data from files (`flights.txt`, `customers.txt`, `bookings.txt`) using methods from `FileManager`.
  - Performs operations such as adding flights, customers, booking customers on flights, and saving data back to files using methods from `FlightManager`.
  - Displays lists of flights, customers, and bookings using methods from `FlightManager`.

### Explanation

- **Encapsulation:** Each class encapsulates its data (`Flight`, `Customer`, `Booking`) and provides methods to access and modify this data, ensuring data integrity and abstraction.
- **Data Structures:** Utilizes `HashMap`, `HashSet`, `ArrayDeque`, and `PriorityQueue` for efficient storage and management of flights, customers, and bookings.
- **File Handling:** `FileManager` class handles file operations (`load` and `save`) using `BufferedReader`, `BufferedWriter` for text-based file handling and `RandomAccessFile` for direct access operations.
- **Main Program Flow:** The `Vacation` class demonstrates typical operations of initializing the system, performing CRUD operations on flights, customers, and bookings, and persisting data to files.

*/

import java.io.*;
import java.util.*;

// Flight class
class Flight {
    String flightNumber;
    String origin;
    String destination;

    public Flight(String flightNumber, String origin, String destination) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
    }

    // Getters and setters
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}

// Customer class
class Customer {
    String customerId;
    String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    // Getters and setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

// Booking class
class Booking {
    String bookingId;
    Customer customer;
    Flight flight;
    String seatNumber;

    public Booking(String bookingId, Customer customer, Flight flight, String seatNumber) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.flight = flight;
        this.seatNumber = seatNumber;
    }

    // Getters and setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", customer=" + customer +
                ", flight=" + flight +
                ", seatNumber='" + seatNumber + '\'' +
                '}';
    }
}

// FlightManager class
class FlightManager {
    HashMap<String, Flight> flights = new HashMap<>();
    HashSet<Customer> customers = new HashSet<>();
    ArrayDeque<Booking> bookingsQueue = new ArrayDeque<>();
    PriorityQueue<Booking> bookingsPriorityQueue = new PriorityQueue<>(Comparator.comparing(Booking::getBookingId));

    // Method to add a flight
    public void addFlight(String flightNumber, String origin, String destination) {
        Flight flight = new Flight(flightNumber, origin, destination);
        flights.put(flightNumber, flight);
    }

    // Method to retrieve a flight
    public Flight getFlight(String flightNumber) {
        return flights.get(flightNumber);
    }

    // Method to list all flights
    public void listFlights() {
        System.out.println("List of Flights:");
        for (Flight flight : flights.values()) {
            System.out.println(flight);
        }
    }

    // Method to add a customer
    public void addCustomer(String customerId, String name) {
        Customer customer = new Customer(customerId, name);
        customers.add(customer);
    }

    // Method to retrieve a customer
    public Customer getCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null; // Customer not found
    }

    // Method to list all customers
    public void listCustomers() {
        System.out.println("List of Customers:");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    // Method to book a customer on a flight
    public void bookCustomer(String bookingId, Customer customer, Flight flight, String seatNumber) {
        Booking booking = new Booking(bookingId, customer, flight, seatNumber);
        bookingsQueue.offer(booking);
        bookingsPriorityQueue.offer(booking);
    }

    // Method to list all bookings
    public void listBookings() {
        System.out.println("List of Bookings:");
        for (Booking booking : bookingsQueue) {
            System.out.println(booking);
        }
    }
}

// FileManager class for file handling
class FileManager {
    static final String FLIGHTS_FILE = "flights.txt";
    static final String CUSTOMERS_FILE = "customers.txt";
    static final String BOOKINGS_FILE = "bookings.txt";

    // Method to save flights to file
    public static void saveFlights(Map<String, Flight> flights) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FLIGHTS_FILE));
        for (Flight flight : flights.values()) {
            writer.write(flight.getFlightNumber() + "," + flight.getOrigin() + "," + flight.getDestination());
            writer.newLine();
        }
        writer.close();
    }

    // Method to load flights from file
    public static HashMap<String, Flight> loadFlights() throws IOException {
        HashMap<String, Flight> flights = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String flightNumber = parts[0];
            String origin = parts[1];
            String destination = parts[2];
            Flight flight = new Flight(flightNumber, origin, destination);
            flights.put(flightNumber, flight);
        }
        reader.close();
        return flights;
    }

    // Method to save customers to file
    public static void saveCustomers(Set<Customer> customers) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE));
        for (Customer customer : customers) {
            writer.write(customer.getCustomerId() + "," + customer.getName());
            writer.newLine();
        }
        writer.close();
    }

    // Method to load customers from file
    public static HashSet<Customer> loadCustomers() throws IOException {
        HashSet<Customer> customers = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String customerId = parts[0];
            String name = parts[1];
            Customer customer = new Customer(customerId, name);
            customers.add(customer);
        }
        reader.close();
        return customers;
    }

    // Method to save bookings to file
    public static void saveBookings(Queue<Booking> bookings) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE));
        for (Booking booking : bookings) {
            writer.write(booking.getBookingId() + "," +
                    booking.getCustomer().getCustomerId() + "," +
                    booking.getFlight().getFlightNumber() + "," +
                    booking.getSeatNumber());
            writer.newLine();
        }
        writer.close();
    }

    // Method to load bookings from file
    public static PriorityQueue<Booking> loadBookings() throws IOException {
        PriorityQueue<Booking> bookings = new PriorityQueue<>(Comparator.comparing(Booking::getBookingId));
        BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String bookingId = parts[0];
            String customerId = parts[1];
            String flightNumber = parts[2];
            String seatNumber = parts[3];
            Customer customer = new Customer(customerId, ""); // Dummy name, to be updated after loading customers
            Flight flight = new Flight(flightNumber, "", ""); // Dummy origin/destination, to be updated after loading flights
            Booking booking = new Booking(bookingId, customer, flight, seatNumber);
            bookings.offer(booking);
        }
        reader.close();
        return bookings;
    }
}

// Main class
public class Vacation {
    public static void main(String[] args) {
        FlightManager manager = new FlightManager();

        try {
            // Load initial data from files
            manager.addFlights(FileManager.loadFlights());
            manager.addCustomers(FileManager.loadCustomers());
            manager.addBookings(FileManager.loadBookings());
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        // Sample operations
        manager.addFlight("AI101", "DEL", "BOM");
        manager.addFlight("AI102", "BOM", "DEL");

        manager.addCustomer("C001", "Rahul");
        manager.addCustomer("C002", "Priya");

        manager.bookCustomer("B001", manager.getCustomer("C001"), manager.getFlight("AI101"), "A1");
        manager.bookCustomer("B002", manager.getCustomer("C002"), manager.getFlight("AI102"), "B2");

        // Save data to files
        try {
            FileManager.saveFlights(manager.getFlights());
            FileManager.saveCustomers(manager.getCustomers());
            FileManager.saveBookings(manager.getBookings());
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }

        // Display flights, customers, and bookings
        manager.listFlights();
        manager.listCustomers();
        manager.listBookings();
    }
}
