package mysqlConnection;

import java.sql.*;

public class MysqlConnection {

    public MysqlConnection(){
        Connector();
    }

    public static Connection Connector(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/obs","root","harsh8747");
            return conn;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}