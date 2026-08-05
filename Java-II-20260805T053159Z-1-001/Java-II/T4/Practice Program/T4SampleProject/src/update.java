import java.sql.*;
import java.util.*;
public class update {

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
        System.out.println("Enter faculty name which you want to replace");
        String replace = sc.nextLine();
        sc.nextLine();
        System.out.println("Enter faculty name which you want in replacement");
        String replacement = sc.nextLine();
        
       
        String sql = "update data set facname='HNM' where facname = 'HDS'"; 

            int r =st.executeUpdate(sql);
            if (r>0)
            System.out.println("Successful");
            else
            System.out.println("Unsuccessful");
    
}
}