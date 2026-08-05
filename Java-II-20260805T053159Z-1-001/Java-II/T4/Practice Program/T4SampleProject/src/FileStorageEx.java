// TINYTEXT - 255
//TEXT 65535
//MEDIUM TEXT - 16777215
//LONGTEXT 4294967295
import java.sql.*;
import java.util.*;
import java.io.*;

public class FileStorageEx {
    public static void main(String[] args) throws Exception
    {
        String dburl ="jdbc:mysql://localhost:3308/SSIT";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);

        String sql = "INSERT INTO fileTable (fileName, fileContent) values (?,?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, "Test Java File");

        File f = new File("G://hello.txt");
        FileReader fr = new FileReader(f);
        pst.setCharacterStream(2, fr, 10);
        int r = pst.executeUpdate();
        System.out.println((r>0)? "insetion done": "insetion not done" );
    }
}
