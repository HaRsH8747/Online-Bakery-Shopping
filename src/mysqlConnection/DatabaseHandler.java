package mysqlConnection;

import java.sql.*;

public class DatabaseHandler {
    private Statement stmt;
    public Connection conn;
    public static DatabaseHandler handler=null;

    private DatabaseHandler(){
        conn = MysqlConnection.Connector();
    }

    public static DatabaseHandler getInstance(){
        if (handler==null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    public ResultSet executeQuery(String query) {
        ResultSet resultSet;
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isValidQuery(String query){
        ResultSet resultSet;
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeAction(String query) {
        try {
            stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeUpdate(PreparedStatement stmt){
        try {
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
