//wap to store audio video image files in Database using Java

    import java.sql.*;
    import java.util.*;
    import java.io.*;
    
    public class BinaryStorage {
        public static void main(String[] args) throws Exception
        {
            String dburl ="jdbc:mysql://localhost:3308/SSIT";
            String dbuser = "root";
            String dbpass = "";
            // String drivername = "com.mysql.cj.jdbc.Driver";
    
           // Class.forName(drivername);
            Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);
            //file operation
            File f = new File("G://logo.png");
            String fileName = f.getName();
            long fileSizekb = (f.length()/1024);            
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            //sql pst
            String sql = "INSERT INTO binaryfilestorage (fileName, fileSizekb,	fileExtension,	fileContent) values (?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fileName);
            pst.setLong(2, fileSizekb);
            pst.setString(3, fileExtension);

            FileInputStream fis = new FileInputStream(f);
            pst.setBinaryStream(4, fis);
            pst.executeUpdate();
            // System.out.println((r>0)? "insetion done": "insetion not done" );
        }
}
