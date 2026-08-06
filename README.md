# 📈 Java Trading Simulation Engine

A comprehensive, multi-threaded stock market trading simulation built with **Java** and **PostgreSQL**. This project was developed as a Semester 2 academic project, focusing on core Object-Oriented Programming (OOP), Data Structures, Relational Databases (JDBC), and concurrency.

---

## ✨ Key Features

- **Custom Matching Engine**: A fully functional matching engine that continuously pairs Buy and Sell orders based on price-time priority.
- **Custom Data Structures**: 
  - Uses a **Binary Search Tree (BST)** to efficiently manage the `OrderBook` in memory, ensuring fast insertions and matching.
  - Implements a **Custom Linked List** for memory-efficient dynamic lists (e.g., portfolio holdings, stock watchlists) without relying on `java.util.ArrayList`.
- **Bot Traders**: Background daemon threads that simulate market activity by actively placing randomized `LIMIT` orders around the current market price.
- **Advanced Order Types**: Supports `MARKET`, `LIMIT`, and `STOP_LOSS` orders. A dedicated background `StopLossMonitor` thread constantly watches price movements to trigger stop-loss executions.
- **Corporate Actions**: Admin capabilities to launch **IPOs** (with proportional allotment logic) and declare **Dividends**.
- **User Authentication**: Plain-text password storage with signup validation (min 8 chars, uppercase, lowercase, digit). Kept simple for Semester 2 scope — no external security libraries.
- **Reporting System**: Generate and export full transaction history to CSV format.

---

## 🛠️ Technology Stack

- **Language:** Java 8+
- **Database:** PostgreSQL 12+
- **Database Connectivity:** JDBC (PostgreSQL Driver)
- **Concurrency:** Java Threads (Daemons, Synchronized blocks)

---

## ⚙️ Prerequisites

Before running this project, ensure you have the following installed on your machine:
- **Java Development Kit (JDK)**
- **IntelliJ IDEA** (or any preferred Java IDE)
- **PostgreSQL** 

---

## 🗄️ Database Setup

This project requires a local PostgreSQL database to function correctly. 

1. Open your PostgreSQL tool (like pgAdmin or `psql`) and create a new database named `tradingdb`:
   ```sql
   CREATE DATABASE tradingdb;
   ```
2. Connect to the `tradingdb` database.
3. Run the provided SQL scripts in the root directory to set up the tables and initial data:
   - Execute `schema.sql` to construct the tables, views, and foreign key relationships.
   - Execute `seed_price_history.sql` to populate the initial stocks and historical data.

---

## 🔧 Configuration

Before running the application, configure your database connection settings:
1. Open the `config.properties` file in the root directory.
2. Update `db.password` (and `db.user` if necessary) to match your local PostgreSQL credentials:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/tradingdb
   db.user=postgres
   db.password=YOUR_LOCAL_PASSWORD_HERE
   ```

---

## 🚀 Running the Project

1. Clone this repository to your local machine.
2. Open IntelliJ IDEA.
3. Select **File > Open** and select the cloned project folder.
4. IntelliJ will automatically detect the project structure from the included `.idea` folder and `.iml` file. Ensure the `postgresql-42.x.x.jar` in the `/libs` folder is added as a Project Library.
5. In the Project window, navigate to `src` and locate `Main.java`.
6. Right-click on `Main.java` and select **Run 'Main.main()'**.

---

## 👥 Usage

Upon launching, the background Simulation Engine (Matching Engine, Stop-loss Monitor, and Bot Traders) will initialize.

**Default Login Credentials:**
| Role  | Username  | Password             |
|-------|-----------|----------------------|
| Admin | `admin`   | `AdminPassword123`   |
| User  | `alice`   | `AlicePassword123`   |
| User  | `bob`     | `BobPassword123`     |
| User  | `charlie` | `CharliePassword123` |

**Password Rules (Sign Up):**
- Minimum 8 characters
- At least one uppercase letter (A–Z)
- At least one lowercase letter (a–z)
- At least one digit (0–9)

**Admin Menu:**
Allows the admin to add new stocks, declare dividends, list IPOs, and view market reports.

**User Menu:**
Allows standard users to view their portfolio, search stocks, place buy/sell orders, apply for IPOs, manage watchlists, and export their trading history.
