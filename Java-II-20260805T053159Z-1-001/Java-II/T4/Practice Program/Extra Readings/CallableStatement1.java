/*
Callable Statement

--> to call stored procedure
--> Returns Multiple values
--> Create in database
--> IN-input parameter  OUT- Returned value

STEPS:
1. Open XAMPP & phpMyAdmin
2. click on database
3. click on Routines
4. Create New Routine
5. Select Procedure
6. write basic syntax

CallableStatement cst = con.prepareCall("{call funName()}")

Stored Procedure:

BEGIN
    SELECT....
    FROM....
    WHERE...
END

String sql = "{call Procedure(?,?)}";
Callable Statement cst = con.prepareCall(sql);
cst.executeUpdate();

 */


 import java.sql.*;
 import java.util.Scanner;
 
 public class CallableStatement1 {
     public static void main(String[] args) throws Exception {
         Scanner sc = new Scanner(System.in);
 
         String dburl = "jdbc:mysql://localhost:3306/lju";
         String dbuser = "root";
         String dbpass = "";
         String drivername = "com.mysql.jdbc.Driver";
 
         // Step 1: Arrange and Load Drivers:
 
         Class.forName(drivername);
 
         // Step 2: Establish Connection:
 
         Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
 
         // Check is Connection established?
 
         if (con != null) 
         {
             System.out.println("Connection Sucessful");
         }
 
         else 
         {
             System.out.println("Connection Failed");
         }
 
        String sql ="{call getFacultyData()}";
         CallableStatement cst = con.prepareCall(sql);
         ResultSet rs= cst.executeQuery();

         while(rs.next())
        {
            System.out.println("\n");
           System.out.println("Faculty Id= "+rs.getInt(1));
           System.out.println("Faculty Name= "+rs.getString(2));
           System.out.println("Faculty salary= "+rs.getFloat(3));
           System.out.println("Faculty Number= "+rs.getString(4));
           System.out.println("\n");
        }


        

    }
}
