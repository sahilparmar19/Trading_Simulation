import java.sql.*;
import java.util.*;
//--> all code same till connection
//
public class RSMD 
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
        String sql="select* from faculty";
        PreparedStatement pst=con.prepareStatement(sql);
        ResultSet rs=pst.executeQuery();
        ResultSetMetaData rsmd=rs.getMetaData();
        System.out.println("total columns="+rsmd.getColumnCount());
        System.out.println("1st col="+rsmd.getColumnName(1));
        System.out.println("2nd col type="+rsmd.getColumnTypeName(2));
        System.out.println("Tabe Name="+rsmd.getTableName(1));
    }

}
