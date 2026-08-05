// TINYTEXT - 255
//TEXT 65535
//MEDIUM TEXT - 16777215
//LONGTEXT 4294967295

//CLOb character large object : texts maate
//BLOb ByteLarge Object : 
import java.sql.*;
import java.util.*;
import java.io.*;

public class FileFetcher {
    public static void main(String[] args) throws Exception
    {
        String dburl ="jdbc:mysql://localhost:3308/SSIT";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);

        String sql = "SELECT * FROM fileTable";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        
        
        while(rs.next()){
            String fileName = rs.getString("fileName");
            Clob fclob = rs.getClob("fileContent");
            Reader r = fclob.getCharacterStream();
            FileWriter fw = new FileWriter("G://" + fileName + ".txt");
            int i =r.read();
            while(i!=-1){
                fw.write((char)i);
                i=r.read();
            }
            fw.close();
        }

        //System.out.println((r>0)? "insetion done": "insetion not done" );
    }
}
