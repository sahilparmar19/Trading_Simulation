import java.sql.*;
import java.util.*;
class test
{
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
        System.out.println("Enter count of entries");
        int n = sc.nextInt();
        sc.nextLine();
        for(int i =0;i<n;i++)
        {
            System.out.println("Enter facid");
            int facid = sc.nextInt();
            sc.nextLine();
             System.out.println("Enter facname");
            String facname = sc.nextLine();
             System.out.println("Enter facsalary");
            double facsalary = sc.nextDouble();
            sc.nextLine();
            System.out.println("Enter facnumber");
            String facnumber = sc.nextLine();

            String sql = "insert into data(facid,facname,facsalary,facnumber) values(" +facid + ",'"+facname +"',"+facsalary+",'"+facnumber+"')"; 

            st.executeUpdate(sql);

        }


    }
}