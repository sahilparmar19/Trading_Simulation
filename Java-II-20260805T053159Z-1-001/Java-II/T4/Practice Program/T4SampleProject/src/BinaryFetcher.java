// TINYTEXT - 255
//TEXT 65535
//MEDIUM TEXT - 16777215
//LONGTEXT 4294967295

//CLOb character large object : texts maate
//BLOb ByteLarge Object : 
import java.sql.*;
import java.util.*;
import java.io.*;

public class BinaryFetcher {
    public static void main(String[] args) throws Exception
    {
        String dburl ="jdbc:mysql://localhost:3308/SSIT";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);

        String sql = "SELECT * FROM binaryFileStorage";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        
        
        while(rs.next()){
            //extraction
            String fileName = rs.getString("fileName");
            Blob b = rs.getBlob("fileContent");
        
            //byte[] conversion of blob object
            byte[] arr = b.getBytes(1, (int)b.length());

            //file creation and content writing
            FileOutputStream fos = new FileOutputStream("G://new" + rs.getString("fileName"));
            fos.write(arr);
            fos.close();
    }
}
}
