import java.sql.*;
import java.util.*;
public class deletion {
    public static void main(String[] args) throws Exception
    {
        String dburl ="jdbc:mysql://localhost:3308/SSIT";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);

        Statement st = con.createStatement();

        Scanner sc = new Scanner (System.in);
        System.out.println("Enter name you want to remove");
        String facname = sc.nextLine();
       
        String sql = "delete from data where facname ='" +facname+"'"; 

            int r =st.executeUpdate(sql);
            if (r>0)
            System.out.println("Successful");
            else
            System.out.println("Unsuccessful");

        }


    }
    

