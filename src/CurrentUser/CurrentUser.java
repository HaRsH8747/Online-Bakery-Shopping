package CurrentUser;

import javafx.beans.property.SimpleStringProperty;
import mysqlConnection.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrentUser {

    public static String UserName;

    public static int getUserId() throws SQLException {
        String query = "Select customer_id from customer where username='"+UserName+"'";
        DatabaseHandler handler = DatabaseHandler.getInstance();
        ResultSet rs = handler.executeQuery(query);
        rs.next();
        return (rs.getInt("customer_id"));
    }

    public static String getName() throws SQLException {
        String query = "Select first_name,last_name from customer where username='"+UserName+"'";
        DatabaseHandler handler = DatabaseHandler.getInstance();
        ResultSet rs = handler.executeQuery(query);
        rs.next();
        return ((rs.getString("first_name"))+" "+(rs.getString("last_name")));
    }

    public static String getAddress() throws SQLException {
        String query = "Select address from customer where username='"+UserName+"'";
        DatabaseHandler handler = DatabaseHandler.getInstance();
        ResultSet rs = handler.executeQuery(query);
        rs.next();
        return (rs.getString("address"));
    }
}
