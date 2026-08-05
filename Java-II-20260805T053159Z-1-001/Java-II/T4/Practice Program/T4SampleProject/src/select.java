import java.sql.*;
public class select {
    public static void main(String[] args) throws Exception
    {
        String dburl ="jdbc:mysql://localhost:3308/ssit";
        String dbuser = "root";
        String dbpass = "";
        String drivername = "com.mysql.jdbc.Driver";

        Class.forName(drivername);
        Connection con = DriverManager.getConnection(dburl,dbuser,dbpass);

        Statement st = con.createStatement();

        
       
        String sql = "select facname, facsalary from data where facname='PAT' "; 

            ResultSet rs =st.executeQuery(sql);
            while(rs.next())
            {
                //System.out.println("ID no = "+ rs.getInt("facid"));
                System.out.println("Facumly Name = "+ rs.getString("facname"));
                System.out.println("Faculty Salary = "+ rs.getDouble("facsalary"));
                //System.out.println("Faculty Number = "+ rs.getString("facnumber"));
            }
            // if (r>0)
            // System.out.println("Successful");
            // else
            // System.out.println("Unsuccessful");

        }


    
}
