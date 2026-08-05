import java.util.*;
import java.sql.*;
public class crudd 
{
    static Connection con = null;
    static Statement st = null;
    static Scanner sc = new Scanner(System.in);

    static void insertdata() throws Exception
    {
        String sql = "insert into data (facid,facname,facsalary,facnumber) values (99,'lal',565655,'1254878')";
        int r = st.executeUpdate(sql);
        if(r>0)
        System.out.println("Insertion successful");
        else
        System.out.println("Insertion failed");
    }   
    static void deletedata() throws Exception
    {
        String sql = "delete from data where facname='PAT'";
        int r = st.executeUpdate(sql);
        if(r>0)
        System.out.println("Deletion successful");
        else
        System.out.println("Deleertion failed");
    }
    static void selectdata() throws Exception
    {
        String sql = "select * from data where facname='HNM'";
        ResultSet rs = st.executeQuery(sql);
        while(rs.next())
        {
            System.out.println("facid");
            System.out.println("facname");
            System.out.println("facsalary");
            System.out.println("facnumber");
        }
    }
    static void updatedata() throws Exception
    {
        String sql = "update facname = 'radhe' from data where facname='CVM'";
        int r = st.executeUpdate(sql);
        if(r>0)
        System.out.println("Updation successful");
        else
        System.out.println("Updation failed");
    }
    public static void main(String[] args) throws Exception
    {
        String dburl = "jdbc:mysql://localhost:3308:/SSIT";
        String dbuser ="root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        con =DriverManager.getConnection(dburl,dbuser,dbpass);

        st = con.createStatement();
        
        int ch;
        do
        {
            System.out.println("1-insert \n 2-delete \n 3-select \n 4-update ");
            ch = sc.nextInt();
            switch(ch)
            {
                case 1: insertdata();
                break;
                case 2: deletedata();
                break;
                case 3: selectdata();
                break;
                case 4: updatedata();
                break;
                case 5:
                System.exit(0);

            }
        }while(ch!=5);
        

    }
    
}
