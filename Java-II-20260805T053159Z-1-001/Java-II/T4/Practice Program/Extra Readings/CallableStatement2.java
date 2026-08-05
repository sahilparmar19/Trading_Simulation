
// IN PARAMETER

 import java.sql.*;
 import java.util.Scanner;
 
 public class CallableStatement2 {
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
 
      

        // IN PARAMETER CALL

        System.out.print("Enter name of faculty which details is given: ");
        String s = sc.next();

        String sql = "{call getFac(?)}";
        CallableStatement cst = con.prepareCall(sql);
        cst.setString(1, s);
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

