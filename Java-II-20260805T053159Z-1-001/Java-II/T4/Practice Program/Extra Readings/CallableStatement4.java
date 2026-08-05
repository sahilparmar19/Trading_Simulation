import java.sql.*;
import java.util.Scanner;

public class CallableStatement4 {
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

     

       // IN-OUT PARAMETER CALL

       System.out.print("Enter name of faculty which you want to insert: ");
       String nName = sc.next();
       System.out.print("Enter Salary of faculty which you want to insert: ");
       float nSalary = sc.nextFloat();
       System.out.print("Enter Number of faculty which you want to insert: ");
       String nNumber = sc.next();
       System.out.print("Enter id of faculty for which you want to update faculty name: ");
       int upID= sc.nextInt();
       System.out.print("Enter name of faculty which you want to update: ");
       String upName= sc.next();



       String sql = "{call manipulateFacData(?,?,?,?,?)}";
       CallableStatement cst = con.prepareCall(sql);
       cst.setString(1,nName );
       cst.setFloat(2,nSalary );
       cst.setString(3,nNumber );
       cst.setInt(4, upID);
       cst.setString(5, upName);
       cst.executeUpdate();
       System.out.println("Executed Successfully");
    
   }
}


