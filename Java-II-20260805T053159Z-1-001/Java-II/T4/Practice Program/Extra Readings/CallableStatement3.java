
//OUT PARAMETER 

/*

IN: FAC_ID -INT
OUT: FAC_NAME -VARCHAR

getFacByID()

 */

import java.sql.*;
import java.util.Scanner;

public class CallableStatement3 {
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

       System.out.print("Enter id of faculty which name is required: ");
       int i = sc.nextInt();

       String sql = "{call getFacByID(?,?)}";
       CallableStatement cst = con.prepareCall(sql);
       cst.setInt(1, i);
       cst.execute();
       String s = cst.getString(2);
       System.out.println("Faculty Name: "+s);
       


       

   }
}


