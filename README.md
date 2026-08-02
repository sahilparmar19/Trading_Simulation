# Trading Simulation

A Java-based trading simulation application that uses PostgreSQL for database management.

## Prerequisites
Before running this project, you must have the following installed on your machine:
- **Java Development Kit (JDK)** (Version 8 or higher)
- **IntelliJ IDEA** (or any other preferred Java IDE)
- **PostgreSQL** (Version 12 or higher)

## Database Setup
This project requires a local PostgreSQL database to function correctly. 

1. Open your PostgreSQL tool (like pgAdmin or psql) and create a new database named `tradingdb`:
   ```sql
   CREATE DATABASE tradingdb;
   ```
2. Connect to the `tradingdb` database.
3. Run the provided SQL scripts in the root directory to set up the tables and initial data:
   - First, execute `schema.sql` to create the tables.
   - Second, execute `seed_price_history.sql` to populate the initial data.

## Configuration
Before running the application, you need to configure the database connection settings.
1. Open the `config.properties` file in the root directory.
2. Update the `db.password` (and `db.user` if necessary) to match your local PostgreSQL credentials:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/tradingdb
   db.user=postgres
   db.password=YOUR_LOCAL_PASSWORD_HERE
   ```

## Running the Project in IntelliJ IDEA
1. Clone this repository to your local machine.
2. Open IntelliJ IDEA.
3. Select **File > Open** (or **Get from VCS**) and select the cloned project folder.
4. IntelliJ will automatically detect the project structure from the included `.idea` folder and `.iml` file.
5. In the Project window, navigate to `src` and locate `Main.java`.
6. Right-click on `Main.java` and select **Run 'Main.main()'**.
