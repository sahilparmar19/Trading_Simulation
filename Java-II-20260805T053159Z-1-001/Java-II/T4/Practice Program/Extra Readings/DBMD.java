import java.sql.*;
import java.util.*;
public class DBMD 
{
    public static void main(String[] args) throws Exception
    {
        String dburl="jdbc:mysql://localhost:3306/lju";
        String dbuser="root";
        String dbpass="";
        String driverName="com.mysql.cj.jdbc.Driver";
        //Step:1: Load Driver
        Class.forName(driverName);
        //Step:2:Connect with db
        Connection con=DriverManager.getConnection(dburl,dbuser,dbpass);
        //check is connection Establihed?
        if(con!=null)
        {
            System.out.println();
            System.out.println("Connection Success");
            System.out.println();
        }
        else
        {
            System.out.println("Connection failed");
        }
        DatabaseMetaData dbmd=con.getMetaData();
        System.out.println(dbmd.getDriverName());
        System.out.println(dbmd.getDriverVersion());
        System.out.println(dbmd.getUserName());
        System.out.println(dbmd.getDatabaseProductName());
        System.out.println(dbmd.getDatabaseProductVersion());
        System.out.println(dbmd.getURL());
	

    }

}
