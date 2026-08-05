/*
 * Table = tcs
 * column = empid,empname;empsalary,empnumber
 *          do the crud operation by using class and object of tcs
 ## Detailed Problem Definition

### Aim:
Develop a Java application to manage employee records in a table named `tcs` using JDBC. The table has the following columns: `empid`, `empname`, `empsalary`, and `empnumber`. Implement the CRUD (Create, Read, Update, Delete) operations for the employee records.

### Problem Statement:
You are required to create a Java program that interacts with a MySQL database to perform CRUD operations on a table called `tcs`. The table structure is as follows:

- `empid` (int, primary key, auto-increment)
- `empname` (varchar)
- `empsalary` (double)
- `empnumber` (varchar)

### Requirements:
1. **Class Definition:**
    - Create a class named `tcs` with the following attributes:
        - `int empid`
        - `String empname`
        - `double empsalary`
        - `String empnumber`
    - Provide constructors for initializing these attributes.
    - Implement getter and setter methods for each attribute.
    - Override the `toString` method to display the details of an employee.

2. **Database Connectivity:**
    - Establish a connection to a MySQL database using JDBC.
    - Use the following connection parameters:
        - Database URL: `jdbc:mysql://localhost:3308/basic`
        - Username: `root`
        - Password: ``
    - Load the MySQL JDBC driver.

3. **CRUD Operations:**
    - Implement the following static methods in a class named `FacultyManagement`:
        - `insertData(tcs t)`: Insert a new employee record into the `tcs` table.
        - `deleteData(int empid)`: Delete an employee record from the `tcs` table based on `empid`.
        - `updateData(tcs t)`: Update an existing employee record in the `tcs` table based on `empid`.
        - `showData()`: Retrieve and display all employee records from the `tcs` table.

4. **User Interface:**
    - Provide a menu-driven interface in the `main` method to perform the following actions:
        - Insert a new employee record.
        - Delete an existing employee record based on `empid`.
        - Update an existing employee record based on `empid`.
        - Display all employee records.
        - Exit the application.

5. **Exception Handling:**
    - Implement appropriate exception handling to manage database connectivity issues and SQL errors.

### Expected Output:
- The application should display messages indicating the success or failure of each operation.
- The application should allow the user to repeatedly perform different operations until they choose to exit.

### Example Menu:
```
Enter 1 for Insertion
Enter 2 for Deletion
Enter 3 for Update
Enter 4 for Select
Enter 5 for Exit
```

### Sample Execution:
1. User chooses to insert a new employee record:
    ```
    Enter name: John Doe
    Enter salary: 50000
    Enter number: 1234567890
    Insertion successful
    ```

2. User chooses to display all employee records:
    ```
    empid: 1, empname: John Doe, empsalary: 50000, empnumber: 1234567890
    ```

### Submission Guidelines:
- Submit the complete Java code including the class definition, database connectivity, CRUD operations, and user interface.
- Ensure that your code is well-commented and follows standard coding conventions.
 */
 
import java.sql.*;
import java.util.Scanner;

class tcs {
    int empid;
    String empname;
    double empsalary;
    String empnumber;

    public tcs() {}

    public tcs(String empname, double empsalary, String empnumber) {
        this.empname = empname;
        this.empsalary = empsalary;
        this.empnumber = empnumber;
    }

    public tcs(int empid, String empname, double empsalary, String empnumber) {
        this.empid = empid;
        this.empname = empname;
        this.empsalary = empsalary;
        this.empnumber = empnumber;
    }

    public int getEmpid() {
        return empid;
    }

    public void setEmpid(int empid) {
        this.empid = empid;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public double getEmpsalary() {
        return empsalary;
    }

    public void setEmpsalary(double empsalary) {
        this.empsalary = empsalary;
    }

    public String getEmpnumber() {
        return empnumber;
    }

    public void setEmpnumber(String empnumber) {
        this.empnumber = empnumber;
    }

    @Override
    public String toString() {
        return "tcs [empid=" + empid + ", empname=" + empname + ", empsalary=" + empsalary + ", empnumber=" + empnumber + "]";
    }
}

public class FacultyManagement {
    static Scanner sc = new Scanner(System.in);
    static Connection con = null;
    static PreparedStatement pst = null;

    static void insertData(tcs t) throws Exception {
        String sql = "insert into tcs (empname, empsalary, empnumber) values(?, ?, ?)";
        pst = con.prepareStatement(sql);
        pst.setString(1, t.getEmpname());
        pst.setDouble(2, t.getEmpsalary());
        pst.setString(3, t.getEmpnumber());
        int rs = pst.executeUpdate();
        if (rs > 0)
            System.out.println("Insertion successful");
        else
            System.out.println("Insertion failed");
    }

    static void deleteData(int empid) throws Exception {
        String sql = "delete from tcs where empid = ?";
        pst = con.prepareStatement(sql);
        pst.setInt(1, empid);
        int rs = pst.executeUpdate();
        if (rs > 0)
            System.out.println("Deletion successful");
        else
            System.out.println("Deletion failed");
    }

    static void updateData(tcs t) throws Exception {
        String sql = "update tcs set empname = ?, empsalary = ?, empnumber = ? where empid = ?";
        pst = con.prepareStatement(sql);
        pst.setString(1, t.getEmpname());
        pst.setDouble(2, t.getEmpsalary());
        pst.setString(3, t.getEmpnumber());
        pst.setInt(4, t.getEmpid());
        int rs = pst.executeUpdate();
        if (rs > 0)
            System.out.println("Update successful");
        else
            System.out.println("Update failed");
    }

    static void showData() throws Exception {
        String sql = "select * from tcs";
        pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            int empid = rs.getInt("empid");
            String empname = rs.getString("empname");
            double empsalary = rs.getDouble("empsalary");
            String empnumber = rs.getString("empnumber");
            System.out.println("empid: " + empid + ", empname: " + empname + ", empsalary: " + empsalary + ", empnumber: " + empnumber);
        }
    }

    public static void main(String[] args) throws Exception {
        String dburl = "jdbc:mysql://localhost:3308/basic";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.cj.jdbc.Driver";
        Class.forName(drivername);
        con = DriverManager.getConnection(dburl, dbuser, dbpass);

        if (con != null) {
            System.out.println("Connection Successful");
        } else {
            System.out.println("Connection Failed");
        }

        int ch;
        do {
            System.out.println("Enter 1 for Insertion \nEnter 2 for Deletion \nEnter 3 for Update \nEnter 4 for Select \nEnter 5 for Exit");
            ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1:
                    System.out.println("Enter name");
                    String empname = sc.nextLine();
                    System.out.println("Enter salary");
                    double empsalary = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Enter number");
                    String empnumber = sc.nextLine();
                    tcs t1 = new tcs(empname, empsalary, empnumber);
                    insertData(t1);
                    break;
                case 2:
                    System.out.println("Enter empid to delete");
                    int empid = sc.nextInt();
                    deleteData(empid);
                    break;
                case 3:
                    System.out.println("Enter empid to update");
                    int empidToUpdate = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter new name");
                    String newEmpname = sc.nextLine();
                    System.out.println("Enter new salary");
                    double newEmpsalary = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Enter new number");
                    String newEmpnumber = sc.nextLine();
                    tcs t2 = new tcs(empidToUpdate, newEmpname, newEmpsalary, newEmpnumber);
                    updateData(t2);
                    break;
                case 4:
                    showData();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        } while (ch != 5);
    }
}
