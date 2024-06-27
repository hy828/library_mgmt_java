
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection getConnection(){
        Connection con = null;

        try{
            Class.forName("com.mysql.jdbc.Driver"); //加载类
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root",
                    "12345"); //获取数据库连接
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e);
        }
        return con;
    }

}
