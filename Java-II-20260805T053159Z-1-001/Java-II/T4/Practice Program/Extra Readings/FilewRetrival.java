import java.sql.*;
import java.io.*;
import java.util.*;
public class FilewRetrival 
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
        String sql="select * from fileTable";
        PreparedStatement pst=con.prepareStatement(sql);
        ResultSet rst=pst.executeQuery();
        while(rst.next())
        {
            String fileName=rst.getString("fileName");
            Clob fClob=rst.getClob("fileContent");
            Reader r=fClob.getCharacterStream();
            FileWriter fw=new FileWriter("D://"+fileName+".txt");
            int i;
            while((i=r.read())!=-1)
            {
                fw.write((char)i);
            }
            r.close();
            fw.close();
        }
    }
    
}
