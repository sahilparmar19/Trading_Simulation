import java.sql.*;
import java.io.*;
import java.util.*;
public class FileStorageEx 
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
        String sql="insert into fileTable(fileName,fileContent) values(?,?)";
        PreparedStatement pst=con.prepareStatement(sql);
        pst.setString(1,"Test Java File");
        File f=new File("D://p1.java");
        FileReader fr=new FileReader(f);
        pst.setCharacterStream(2,fr,f.length());
        int r=pst.executeUpdate();
        if (r>0) 
        {
            System.out.println();
            System.out.println("File Storage Success");
            System.out.println();
        } 
        else 
        {
            System.out.println("File Storage failed");
        }

    }
}
