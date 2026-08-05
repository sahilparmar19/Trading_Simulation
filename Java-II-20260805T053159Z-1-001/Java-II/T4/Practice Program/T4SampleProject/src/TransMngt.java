import java.sql.*;
import java.util.*;
import java.io.*;

public class TransMngt{
    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/SSIT","root", "");
        con.setAutoCommit(false);
        String sql = "INSERT INTO binaryfilestorage (fileName, fileSizekb,	fileExtension,	fileContent) values ('xyz', 400, '.png', 'dfg')";
        PreparedStatement pst = con.prepareStatement(sql);
        int rs = pst.executeUpdate();
        int ch;
        do{
        System.out.println("1. Commit     2: Rollback   3: Set Savepoint");
        ch = sc.nextInt();
        switch(ch){
            case 1: 
            con.commit(); break;

            case 2:
            con.rollback(); break;

            case 3:
            sql = "INSERT INTO binaryfilestorage (fileName, fileSizekb,	fileExtension,	fileContent) values ('abc', 300, '.jpg', 'ddd')";
            pst = con.prepareStatement(sql);
            rs = pst.executeUpdate(); 
            con.setSavepoint(); break;
        }
    }while(ch!=4);
    }
}