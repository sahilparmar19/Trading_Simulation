//Write a program that is used to store audio , video,image 
//file in Dastabase using  java
import java.sql.*;
import java.io.*;
import java.util.*;
public class BinaryStorage 
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
        String sql="insert into binaryFileStorage(fileName,fileSizeKb,fileExtension,fileContent) values(?,?,?,?)";
        PreparedStatement pst=con.prepareStatement(sql);
        File f=new File("D://AP.JPG");
        String fileName=f.getName();
        Long fileLength=f.length();
        long fileSizeKb=(fileLength/1024);
        String fileExtension=fileName.substring(fileName.lastIndexOf("."));
        pst.setString(1,fileName);
        pst.setLong(2,fileSizeKb);
        pst.setString(3,fileExtension);
        FileInputStream fis=new FileInputStream(f);
        pst.setBinaryStream(4,fis);
        int r=pst.executeUpdate();
        if (r>0) 
        {
            System.out.println();
            System.out.println("Insert Success");
            System.out.println();
        } 
        else 
        {
            System.out.println("Insert failed");
        }
    }
}
