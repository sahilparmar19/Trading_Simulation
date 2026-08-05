----------  CHAP 9 - JDBC-1  -------------- 
BEFORE RUNNING ANY JAVA CODE, ensure that you have the MySQL JDBC driver JAR file in your classpath/reference library.

//JDBC Connectivity	
//Write the java program that do connection with database named "LJU"

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        if (connection != null) {
            System.out.println("Connected to the database!");
            connection.close();
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}

//-------------------------------------------------------------------------
----------------------------------
       //CallableStatement	
----------------------------------
//Write a java program that fetch all details of employees from employee table.Table name is employee. And Database is : LJU. Now, fetch record using CallableStatement. - Create procedure with name : getEmployees() Emp id is auto incremented. 


//Step 1:
//Before running this code, make sure you have created the getEmployees() stored procedure in your MySQL database. The procedure should retrieve all details from the "employee" table. The structure of the procedure might look something like this:

DELIMITER //
CREATE PROCEDURE getEmployees()
BEGIN
    SELECT * FROM employee;
END //
DELIMITER ;

//Execute the above SQL code in your MySQL database to create the stored procedure.

//Step 2: In Java file:
import java.sql.*;
import java.util.*;

public class FetchEmployeesUsingCallableStatement {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        if (connection != null) {
            System.out.println("Connected to the database!");

            try {
                // Call the stored procedure using CallableStatement
                CallableStatement callableStatement = connection.prepareCall("{CALL getEmployees()}");
                ResultSet resultSet = callableStatement.executeQuery();

                // Display the fetched employee details
                System.out.println("Employee Details:");
                System.out.println("------------------");

                while (resultSet.next()) {
                    int empId = resultSet.getInt("emp_id");
                    String empName = resultSet.getString("emp_name");
                    String empRole = resultSet.getString("emp_role");

                    System.out.println("Employee ID: " + empId);
                    System.out.println("Employee Name: " + empName);
                    System.out.println("Employee Role: " + empRole);
                    System.out.println("------------------");
                }

                resultSet.close();
                callableStatement.close();
            } catch (SQLException e) {
                System.out.println("Error executing stored procedure.");
                e.printStackTrace();
            }

            // Close the connection
            connection.close();
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}


//----------------------------------------------------------------------------------------------

//CallableStatement
//Write a java program that inserts record of employees like emp_id, emp_name, emp_designation, emp_salary. Here emp_id is primary key and auto incremented. Table name is employee. And Database is : LJU. 
//Now, insert one record using CallableStatement. - Create procedure with name : insertEmployee(?,?,?)  Emp id is auto incremented. Consider all parameters as IN parameter"

//Before using this program, you need to create the stored procedure in your MySQL database. Here's an example of how you could create the "insertEmployee" procedure:

DELIMITER //
CREATE PROCEDURE insertEmployee(IN empName VARCHAR(255), IN empDesignation VARCHAR(255), IN empSalary DOUBLE)
BEGIN
    INSERT INTO employee (emp_name, emp_designation, emp_salary) VALUES (empName, empDesignation, empSalary);
END;
//
DELIMITER ;

//Run this SQL code in your MySQL database to create the stored procedure. Once you've done that, you can run the Java program to insert a record into the "employee" table using the stored procedure.

import java.sql.*;

public class JDBCExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Database URL and credentials
        String url = "jdbc:mysql://localhost/LJU";
        String username = "root";
        String password = "";

        // Establish the database connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Prepare the CallableStatement to call the stored procedure
        String storedProcedureCall = "{CALL insertEmployee(?, ?, ?)}";
        CallableStatement callableStatement = connection.prepareCall(storedProcedureCall);

        // Set the parameters for the stored procedure
        callableStatement.setString(1, "John Doe");
        callableStatement.setString(2, "Software Engineer");
        callableStatement.setDouble(3, 75000.0);

        // Execute the stored procedure
        callableStatement.execute();

        System.out.println("Record inserted successfully!");

        // Close the resources
        callableStatement.close();
        connection.close();
    }
}


//---------------------------------------------------------------------------

//CallableStatement	
//Write a java program that update record of emp_designation, and emp_salary where emp_name is rahul. Here emp_id is primary key and auto incremented. Table name is employee. And Database is : LJU. 
//Now, update record using CallableStatement. - Create procedure with name : updateEmployeeByName(?,?,?)  Emp id is auto incremented. Consider all parameters as IN parameter. 

DELIMITER //
CREATE PROCEDURE updateEmployeeByName(IN empName VARCHAR(255), IN newDesignation VARCHAR(255), IN newSalary DECIMAL(10, 2))
BEGIN
    UPDATE employee SET emp_designation = newDesignation, emp_salary = newSalary WHERE emp_name = empName;
END //
DELIMITER ;

import java.sql.*;

public class DatabaseConnection {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";
        
        Connection connection = DriverManager.getConnection(url, username, password);
        
        String empName = "rahul";
        String newDesignation = "New Designation";
        double newSalary = 50000;
        
            CallableStatement callableStatement = connection.prepareCall("{call updateEmployeeByName(?,?,?)}");
            callableStatement.setString(1, empName);
            callableStatement.setString(2, newDesignation);
            callableStatement.setDouble(3, newSalary);
            callableStatement.executeUpdate();
            callableStatement.close();
            System.out.println("Employee record updated successfully.");        
        connection.close();
    }
}


//---------------------------------------------------------------------------
//CallableStatement	
//Write a java program that select record emp_designation, and emp_salary where emp_name is rahul. Here emp_id is primary key and auto incremented. Table name is employee. And Database is : LJU. 
//Now, update record using CallableStatement. - Create procedure with name : selectEmployeeByName(?,?,?)  Emp id is auto incremented. 
//Here : emp_name is IN parameter. emp_designation and emp_salary are OUT parameter. "

/*
DELIMITER //

CREATE PROCEDURE selectEmployeeByName(
    IN empName VARCHAR(255),
    OUT empDesignation VARCHAR(255),
    OUT empSalary DOUBLE
)
BEGIN
    SELECT emp_designation, emp_salary INTO empDesignation, empSalary
    FROM employee
    WHERE emp_name = empName;
END //

DELIMITER ;

*/

import java.sql.*;

public class DatabaseDemo {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Database credentials
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Establish the connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Select query
        String selectQuery = "SELECT emp_designation, emp_salary FROM employee WHERE emp_name = ?";

        // Prepare and execute the SELECT statement
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "rahul");
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                String designation = resultSet.getString("emp_designation");
                double salary = resultSet.getDouble("emp_salary");
                System.out.println("Emp Designation: " + designation + ", Emp Salary: " + salary);
            }
        }

        // Calling the stored procedure
        String callProcedure = "{CALL selectEmployeeByName(?, ?, ?)}";

        try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
            callableStatement.setString(1, "rahul");
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.DOUBLE);

            callableStatement.execute();

            String empDesignation = callableStatement.getString(2);
            double empSalary = callableStatement.getDouble(3);

            System.out.println("Stored Procedure Result - Emp Designation: " + empDesignation + ", Emp Salary: " + empSalary);
        }

        // Close the connection
        connection.close();
    }
}

//-----------------------------------------------------------------------
//CallableStatement	
/*"Write a stroed procedure with name multiProduct(?,?,?)
This do the follwong. 
- update product price by product id 
- return the product name which is updated by us in previous query. 
- So here write both queries in a single procedure call. Use 3 - IN and OUT parameter as per your understanding. " */

DELIMITER //

CREATE PROCEDURE multiProduct(
    IN product_id INT,
    IN new_price DECIMAL(10, 2),
    OUT updated_product_name VARCHAR(255)
)
BEGIN
    -- Declare a variable to store the product name
    DECLARE product_name VARCHAR(255);

    -- Update the product price by product_id
    UPDATE products
    SET price = new_price
    WHERE id = product_id;

    -- Retrieve the updated product name
    SELECT name INTO product_name
    FROM products
    WHERE id = product_id;

    -- Set the output parameter
    SET updated_product_name = product_name;
END //

DELIMITER ;

//-----------------------------------------------------------------------
//CallableStatement	
/*Write a Java code for the following task.
- Table Name : student  : std_id, std_roll_no, std_name, std_marks, std_div 
- DB : LJU 
- Insert 5 Records using prepared statement.
- Update Div of students having marks > 50  - To - Div B - Use callable statement for this  */

DELIMITER //
CREATE PROCEDURE UpdateDivForMarksAbove50()
BEGIN
    UPDATE student SET std_div = 'B' WHERE std_marks > 50;
END //
DELIMITER ;


import java.sql.*;

public class DatabaseDemo {

    static final String JDBC_URL = "jdbc:mysql://localhost:3306/LJU";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establishing connection
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        
        // Inserting records using PreparedStatement
        String insertQuery = "INSERT INTO student (std_id, std_roll_no, std_name, std_marks, std_div) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        
        for (int i = 1; i <= 5; i++) {
            insertStatement.setInt(1, i);
            insertStatement.setInt(2, 1000 + i);
            insertStatement.setString(3, "Student" + i);
            insertStatement.setInt(4, 60 + i);
            insertStatement.setString(5, "A");
            insertStatement.executeUpdate();
        }

        // Using CallableStatement to update records
        String updateProcedure = "{CALL UpdateDivForMarksAbove50}";
        CallableStatement callableStatement = connection.prepareCall(updateProcedure);
        callableStatement.executeUpdate();

        // Closing connections
        insertStatement.close();
        callableStatement.close();
        connection.close();
    }
}

//---------------------------------------------------------------------------------
//CallableStatement	
/*
"Write a java code for the below tasks. 
- Table Name : student  : std_id, std_roll_no, std_name, std_marks, std_div 
- DB Name : LJU 
- Write tow callable stored procedure with below names. 
A) getMakrs(): this procedure will give you marks of students from  name. 
B) updateRollNo() : this procedure will update the roll no  by giving the name of the student.*/


Procedure to get marks:

DELIMITER //
CREATE PROCEDURE getMarks(IN studentName VARCHAR(255), OUT studentMarks INT)
BEGIN
    SELECT std_marks INTO studentMarks FROM student WHERE std_name = studentName;
END //
DELIMITER ;

Procedure to update roll number:

DELIMITER //
CREATE PROCEDURE updateRollNo(IN studentName VARCHAR(255), IN newRollNo INT)
BEGIN
    UPDATE student SET std_roll_no = newRollNo WHERE std_name = studentName;
END //
DELIMITER ;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class DatabaseExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // JDBC connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Register the JDBC driver (assuming you have the MySQL JDBC driver JAR in your classpath)
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        // Call the stored procedures
   
            // Call the getMarks stored procedure
            CallableStatement getMarksProcedure = connection.prepareCall("{CALL getMarks(?, ?)}");
            getMarksProcedure.setString(1, "John"); // Replace with the student name
            getMarksProcedure.registerOutParameter(2, java.sql.Types.INTEGER);
            getMarksProcedure.execute();
            int marks = getMarksProcedure.getInt(2);
            System.out.println("Marks for John: " + marks);

            // Call the updateRollNo stored procedure
            CallableStatement updateRollNoProcedure = connection.prepareCall("{CALL updateRollNo(?, ?)}");
            updateRollNoProcedure.setString(1, "Jane"); // Replace with the student name
            updateRollNoProcedure.setInt(2, 101); // Replace with the new roll number
            updateRollNoProcedure.execute();
            System.out.println("Roll number updated for Jane");
   
            connection.close();
        }
    }
}

//----------------------------------------------------------------------------
// statement
/*
Write a java code to fetch all student's marks from the table(student) and do addition of all marks. */

import java.sql.*;

public class JDBCExample {
    // JDBC URL, username, and password
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/LJU";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish a connection to the database
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

        // Create a statement
        Statement statement = connection.createStatement();

        // SQL query to fetch student marks
        String query = "SELECT marks FROM student";

        // Execute the query
        ResultSet resultSet = statement.executeQuery(query);

        // Calculate the sum of marks
        int totalMarks = 0;
        while (resultSet.next()) {
            int marks = resultSet.getInt("marks");
            totalMarks += marks;
        }

        // Close the resources
        resultSet.close();
        statement.close();
        connection.close();

        System.out.println("Total marks of all students: " + totalMarks);
    }
}

//--------------------------------------------------------------------------------
--------------------------
   // PreparedStatement 
--------------------------
/*
Write a java program that inserts record of employees like emp_id, emp_name, emp_designation, emp_salary. Here emp_id is primary key and auto incremented. Table name is employee. And Database is : LJU. 
Now, insert one record using PreparedStatement */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnectionAndInsert {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Database credentials
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Establish the connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Insert record using PreparedStatement
        String insertQuery = "INSERT INTO employee (emp_name, emp_designation, emp_salary) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, "John Doe");
        preparedStatement.setString(2, "Software Engineer");
        preparedStatement.setDouble(3, 75000.0);
        
        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted.");

        // Close resources
        preparedStatement.close();
        connection.close();
    }
}

//----------------------------------------------------------------------------------
// PreparedStatement
/*
"Write a java program that inserts record of employees like emp_id, emp_name, emp_designation, emp_salary. Here emp_id is primary key and auto incremented. Table name is employee. And Database is : LJU. 
Now, Ask User how many records you want to enter. And insert n - records using prepared statement "
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeInsertion {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Initialize the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";
        
        // Establish the database connection
        Connection connection = DriverManager.getConnection(url, username, password);
        
        // Create a prepared statement
        String insertQuery = "INSERT INTO employee (emp_name, emp_designation, emp_salary) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        
        // Get input from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of records you want to insert: ");
        int numRecords = scanner.nextInt();
        
        for (int i = 0; i < numRecords; i++) {
            System.out.println("Enter details for Employee " + (i + 1));
            System.out.print("Name: ");
            String empName = scanner.next();
            System.out.print("Designation: ");
            String empDesignation = scanner.next();
            System.out.print("Salary: ");
            double empSalary = scanner.nextDouble();
            
            // Set parameters for the prepared statement
            preparedStatement.setString(1, empName);
            preparedStatement.setString(2, empDesignation);
            preparedStatement.setDouble(3, empSalary);
            
            // Execute the insertion
            preparedStatement.executeUpdate();
            System.out.println("Employee record inserted successfully.");
        }
        
        // Close resources
        preparedStatement.close();
        connection.close();
    }
}

//---------------------------------------------------------------------------
// PreparedStatement
/*
Write a java program for the above same employee table - to do upate emp designation and salary BY emp_name. Take all details from user and update this by using PreparedStatement  */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeUpdate {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Initialize the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Establish the database connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Create a prepared statement
        String updateQuery = "UPDATE employee SET emp_designation = ?, emp_salary = ? WHERE emp_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

        // Get input from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the employee name: ");
        String empName = scanner.nextLine();
        System.out.print("Enter the new designation: ");
        String newDesignation = scanner.nextLine();
        System.out.print("Enter the new salary: ");
        double newSalary = scanner.nextDouble();

        // Set parameters for the prepared statement
        preparedStatement.setString(1, newDesignation);
        preparedStatement.setDouble(2, newSalary);
        preparedStatement.setString(3, empName);

        // Execute the update
        int rowsAffected = preparedStatement.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Employee details updated successfully.");
        } else {
            System.out.println("No employee found with the given name.");
        }

        // Close resources
        preparedStatement.close();
        connection.close();
    }
}

//-----------------------------------------------------------------------------------------------------
// PreparedStatement
/*
Write a java program for the above same employee table - to do delete employee those having salary more than salary entered by user.  from the table.  Take all details from user and update this by using PreparedStatement. - Take salary from user. */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeDelete {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Initialize the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Establish the database connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Create a prepared statement
        String deleteQuery = "DELETE FROM employee WHERE emp_salary > ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);

        // Get input from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the maximum salary for deletion: ");
        double maxSalary = scanner.nextDouble();

        // Set parameter for the prepared statement
        preparedStatement.setDouble(1, maxSalary);

        // Execute the delete operation
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " employees with salary greater than " + maxSalary + " deleted successfully.");
        } else {
            System.out.println("No employees found with salary greater than " + maxSalary + ".");
        }

        // Close resources
        preparedStatement.close();
        connection.close();
    }
}

//-----------------------------------------------------------------------------------
// PreparedStatement
/*
"Write a java code to update student's marks in the follwing manner.
Students having makrs > 50  add + 5 in total 
marks > 60 add + 10 
marks > 80 add + 15 in the marks and update the table in it. 
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class StudentMarksUpdater {

    public static void main(String[] args) throws Exception {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Establishing database connection
        Connection connection = DriverManager.getConnection(url, username, password);

        // Update student marks
        String updateQuery = "UPDATE students SET marks = marks + "
                + "CASE "
                + "WHEN marks > 80 THEN 15 "
                + "WHEN marks > 60 THEN 10 "
                + "WHEN marks > 50 THEN 5 "
                + "ELSE 0 "
                + "END";

        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.executeUpdate();

        // Close the resources
        preparedStatement.close();
        connection.close();

        System.out.println("Student marks updated successfully.");
    }
}

//----------------------------------------------------------------------------------------------
// PreparedStatement
/*
Consider a table with name – student and database as admission. The student table have fields like stdId, stdName, stdMarks(float). Write a java program that adds 2 records using PreparedStatement. [have to write connection code here.]*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JDBCExample {
    public static void main(String[] args) throws Exception {
        // Database connection details
        String jdbcUrl = "jdbc:mysql://localhost:3306/admission";
        String username = "root";
        String password = "";

        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        // SQL query to insert records
        String insertQuery = "INSERT INTO student (stdId, stdName, stdMarks) VALUES (?, ?, ?)";

        // Prepare and execute the insert statements
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        // Insert record 1
        preparedStatement.setInt(1, 1);
        preparedStatement.setString(2, "John Doe");
        preparedStatement.setFloat(3, 85.5f);
        preparedStatement.executeUpdate();

        // Insert record 2
        preparedStatement.setInt(1, 2);
        preparedStatement.setString(2, "Jane Smith");
        preparedStatement.setFloat(3, 92.0f);
        preparedStatement.executeUpdate();

        // Close the PreparedStatement and connection
        preparedStatement.close();
        connection.close();

        System.out.println("Records inserted successfully.");
    }
}

//----------------------------------------------------------------------------------------
// PreparedStatement & CallableStatement
/*
"Management of the LJ wants to do compilation of total marks of the students. For this they do the following process. 
1. Table - mse_marks with columns  - roll_no, t1, t2, bonus.
2. Table – final_marks with roll_no, total ( where total = (t1+t2)/2 + bonus )  
Process 1: Need to fetch all the details from the table mse_marks using CallableStatement. Write SQL code an Java code for the same.  Procedure name is getMarks().

Process 2: Need to insert same roll_no fetched from the mse_marks to final_marks table and total after calculating it perfectly from the table mse_marks. 
This insertion query you have to write using PreparedStatement. */

/*
DELIMITER //
CREATE PROCEDURE getMarks()
BEGIN
    SELECT * FROM mse_marks;
END //
DELIMITER ;
*/
import java.sql.*;

public class StudentMarksManagement {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Database connection details
        String jdbcUrl = "jdbc:mysql://localhost:3306/admission";
        String username = "root";
        String password = "";

        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the connection
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
			// Process 1: Fetching data using CallableStatement
            callableStatement = connection.prepareCall("{call getMarks()}");
            ResultSet resultSet = callableStatement.executeQuery();

            // Process 2: Inserting data using PreparedStatement
            String insertQuery = "INSERT INTO final_marks (roll_no, total) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);

            while (resultSet.next()) {
                int rollNo = resultSet.getInt("roll_no");
                double t1 = resultSet.getDouble("t1");
                double t2 = resultSet.getDouble("t2");
                double bonus = resultSet.getDouble("bonus");

                double total = (t1 + t2) / 2 + bonus;

                preparedStatement.setInt(1, rollNo);
                preparedStatement.setDouble(2, total);
                preparedStatement.executeUpdate();
            }

            System.out.println("Data inserted successfully.");
    }
}

//----------------------------------------------------------------------------------------
----------------------------------------
//ResultSetMetaData, DatabaseMetaData
----------------------------------------
/*
Write a java code snippet that checks weather the database connection is established or not? If the connection is established perfectly then it should give us the version of the driver which I have used for the connection. */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionExample {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Establish database connection
        String url = "jdbc:mysql://localhost:3306/your_database_name";
        String username = "root";
        String password = "";
		Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
        Connection connection = DriverManager.getConnection(url, username, password);

        // Check if connection is established
        DatabaseMetaData metaData = connection.getMetaData();
        if (metaData != null) {
            System.out.println("Database connection established.");

            // Retrieve and display driver version
            String driverVersion = metaData.getDriverVersion();
            System.out.println("JDBC Driver Version: " + driverVersion);
        }
        // Close the connection
        connection.close();
    }
}

//----------------------------------------------------------------------------------------
//ResultSetMetaData, DatabaseMetaData
/*
Write database application that prints different records from a table. Find out the name of table, no. of columns, no. of records and type of the columns. Here DB name is LJU and Table is employee with emp_id, emp_name, emp_designation, emp_salary */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class DatabaseInfo {
    public static void main(String[] args) throws Exception {
        // Database connection details
        String dbUrl = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";

        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Create a connection to the database
        Connection connection = DriverManager.getConnection(dbUrl, username, password);

        // Create a statement
        Statement statement = connection.createStatement();

        // Get table information
        String tableName = "employee";
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

        // Get metadata for the result set
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Print table information
        System.out.println("Table Name: " + tableName);
        System.out.println("Number of Columns: " + metaData.getColumnCount());

        // Count the number of records
        int recordCount = 0;
        while (resultSet.next()) {
            recordCount++;
        }
        System.out.println("Number of Records: " + recordCount);

        // Print column information
        System.out.println("Column Types:");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            System.out.println(metaData.getColumnName(i) + " - " + metaData.getColumnTypeName(i));
        }

        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    }
}

//----------------------------------------------------------------------------------------
//ResultSetMetaData, DatabaseMetaData
/*
Table name: product 
Columns: pId, pName, pPrice, pCategory

Create a java program through which user can ask for the following.
Enter 1 to know Number of Column 
Enter 2 to know the Table Name 
Enter 3 to know Column Name & Ask for Index of column 
Enter 4 to know the type of column of Index given by user  */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ProductTableInfo {
    public static void main(String[] args) throws Exception {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LJU","root", "");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter 1 to know Number of Columns");
                System.out.println("Enter 2 to know the Table Name");
                System.out.println("Enter 3 to know Column Name & Ask for Index of Column");
                System.out.println("Enter 4 to know the type of Column of Index given by user");
                System.out.println("Enter 0 to exit");
                int choice = scanner.nextInt();

                if (choice == 0) {
                    break;
                }

                switch (choice) {
                    case 1:
                        DatabaseMetaData metaData1 = connection.getMetaData();
                        ResultSet resultSet1 = metaData1.getColumns(null, null, "product", null);

                        int count1 = 0;
                        while (resultSet1.next()) {
                            count1++;
                        }

                        System.out.println("Number of columns in the table: " + count1);
                        break;

                    case 2:
                        DatabaseMetaData metaData2 = connection.getMetaData();
                        ResultSet resultSet2 = metaData2.getTables(null, null, "product", null);

                        while (resultSet2.next()) {
                            String tableName = resultSet2.getString("TABLE_NAME");
                            System.out.println("Table Name: " + tableName);
                        }
                        break;

                    case 3:
                        System.out.println("Enter the index of the column:");
                        int columnIndex = scanner.nextInt();

                        DatabaseMetaData metaData3 = connection.getMetaData();
                        ResultSet resultSet3 = metaData3.getColumns(null, null, "product", null);

                        int count3 = 0;
                        while (resultSet3.next()) {
                            count3++;
                            if (count3 == columnIndex) {
                                String columnName = resultSet3.getString("COLUMN_NAME");
                                System.out.println("Column Name at index " + columnIndex + ": " + columnName);
                                break;
                            }
                        }

                        if (count3 < columnIndex) {
                            System.out.println("Column not found at index " + columnIndex);
                        }
                        break;

                    case 4:
                        System.out.println("Enter the index of the column:");
                        int columnIndex4 = scanner.nextInt();

                        DatabaseMetaData metaData4 = connection.getMetaData();
                        ResultSet resultSet4 = metaData4.getColumns(null, null, "product", null);

                        int count4 = 0;
                        while (resultSet4.next()) {
                            count4++;
                            if (count4 == columnIndex4) {
                                String columnName = resultSet4.getString("COLUMN_NAME");
                                String dataType = resultSet4.getString("TYPE_NAME");
                                System.out.println("Data Type of column " + columnName + ": " + dataType);
                                break;
                            }
                        }

                        if (count4 < columnIndex4) {
                            System.out.println("Column not found at index " + columnIndex4);
                        }
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
            connection.close();
    }
}

//----------------------------------------------------------------------------------------
//ResultSetMetaData, DatabaseMetaData
/*
DB Name : LJU 

Create a java program through which user can ask for the following.
Enter 1 to know Driver Name 
Enter 2 to know the USer Name 
Enter 3 to know Database Product Name 
Enter 4 to know the Driver Version 
*/

import java.sql.*;

public class DatabaseInfoWithoutTryCatch {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        Connection connection = connection = DriverManager.getConnection(url, username, password);
      
        DatabaseMetaData db = connection.getMetaData();

            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            
            do {
                System.out.println("Enter 1 to know Driver Name");
                System.out.println("Enter 2 to know the User Name");
                System.out.println("Enter 3 to know Database Product Name");
                System.out.println("Enter 4 to know the Driver Version");
                System.out.println("Enter 0 to exit");
                
				choice = scanner.nextInt();
                
                switch (choice) {
                    case 1:
                        System.out.println("Driver Name: " + db.getDriverName());
                        break;
                    case 2:
                        System.out.println("User Name: " + db.getUserName());
                        break;
                    case 3:
                        System.out.println("Database Product Name: " + db.getDatabaseProductName());
                        break;
                    case 4:
                        System.out.println("Driver Version: " + db.getDriverVersion());
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please select a valid option.");
                }
            } while (choice != 0);
        } 
    }
}

//----------------------------------------------------------------------------------------------
-------------------------------
//Transaction Management
-------------------------------

/*
"Write a  java program for the following tasks 
The program should establish a connection to the database using JDBC.
The program should allow users to enter the source account number, destination account number, and the amount to be transferred.
The program should begin a transaction and deduct the amount from the source account and add the amount to the destination account.
The program should commit the transaction if the transaction is successful.
The program should rollback the transaction if any exception occurs during the transaction.
Functionality:

The program should provide a menu to the user to perform the transfer of funds.
The program should handle exceptions such as SQLException and ClassNotFoundException.

Create Two Tables: 
account : acc_no & acc_balance 
transaction : trans_id, ,src_acc_no, dest_acc_no, trans_amt  */

import java.sql.*;

public class FundsTransferProgram {

    // Database connection details
    static String DB_URL = "jdbc:mysql://localhost/LJU";
    static String DB_USER = "root";
    static String DB_PASS = "";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish the database connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement statement = connection.createStatement();

        // Create tables if not exist
		String sql1 = "CREATE TABLE IF NOT EXISTS account (acc_no INT PRIMARY KEY, acc_balance DECIMAL(10, 2))";
		String sql2 = "CREATE TABLE IF NOT EXISTS transaction (trans_id INT PRIMARY KEY, src_acc_no INT, dest_acc_no INT, trans_amt DECIMAL(10, 2))";
        statement.executeUpdate(sql1);
        statement.executeUpdate(sql2);

        // Menu-driven program
        while (true) {
            System.out.println("1. Transfer Funds");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
			Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    transferFunds(connection);
                    break;
                case 2:
                    connection.close();
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please select again.");
            }
        }
    }

    public static void transferFunds(Connection connection) throws SQLException {
        System.out.print("Enter source account number: ");
        int srcAccNo = scanner.nextInt();
        System.out.print("Enter destination account number: ");
        int destAccNo = scanner.nextInt();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();

        connection.setAutoCommit(false);

        try {
            Statement statement = connection.createStatement();
            // Deduct amount from source account
            statement.executeUpdate("UPDATE account SET acc_balance = acc_balance - " + amount + " WHERE acc_no = " + srcAccNo);

            // Add amount to destination account
            statement.executeUpdate("UPDATE account SET acc_balance = acc_balance + " + amount + " WHERE acc_no = " + destAccNo);

            // Insert transaction record
            statement.executeUpdate("INSERT INTO transaction (src_acc_no, dest_acc_no, trans_amt) VALUES (" + srcAccNo + ", " + destAccNo + ", " + amount + ")");

            connection.commit();
            System.out.println("Transaction successful.");
        } catch (SQLException e) {
            connection.rollback();
            System.out.println("Transaction failed. Rolling back changes.");
        }
    }
}

//-------------------------------------------------------------------------------------------------
//Transaction Management
/*
Create a Transaction Management System that uses Thread, Where one thread is used to deposite in account and other thread is used to withdraw from the account. You need to run both thread on the wishes of user in database. Try to use synchronized keywotrd to avoid deadlock. */

import java.sql.*;

class DatabaseConnector {

    static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/LJU";
        String username = "root";
        String password = "";
		Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}

class Account {
    private int balance;

    public Account(int initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Deposited: " + amount + " | Balance: " + balance);
    }

    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + " | Balance: " + balance);
        } else {
            System.out.println("Insufficient funds!");
        }
    }
}

class DepositThread extends Thread {
    private Account account;
    private int amount;

    public DepositThread(Account account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    public void run() {
        account.deposit(amount);
    }
}

class WithdrawThread extends Thread {
    private Account account;
    private int amount;

    public WithdrawThread(Account account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    public void run() {
        account.withdraw(amount);
    }
}

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        Account account = new Account(1000); // Initial account balance

        // Create deposit and withdraw threads
        DepositThread depositThread = new DepositThread(account, 200);
        WithdrawThread withdrawThread = new WithdrawThread(account, 300);

        // Start the threads based on user input
        depositThread.start();
        withdrawThread.start();
    }
}
