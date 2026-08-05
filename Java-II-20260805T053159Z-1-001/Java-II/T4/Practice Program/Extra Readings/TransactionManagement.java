import java.sql.*;
import java.io.*;
import java.util.*;
public class TransactionManagement 
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
        con.setAutoCommit(false);
        String sql="insert into faculty(facName,salary,number) values('champu',61000,654321123)";
        PreparedStatement pst= con.prepareStatement(sql);
        pst.executeUpdate();
        //con.commit();
        int ch;
        Scanner sc=new Scanner(System.in);
        do
        {
            System.out.print("Enter 1 for commit() \n Enter 2 for rollback() \n 3 for exit \n");
            System.out.println();
            System.out.print("Enter your choice=");
            ch=sc.nextInt();
            switch(ch)
            {
                case 1:
                con.commit();
                break;

                case 2:
                con.rollback();
                break;
            }

        }while(ch!=3);
    
        
        
    }
}
