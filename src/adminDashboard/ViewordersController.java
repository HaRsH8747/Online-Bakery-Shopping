package adminDashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import mysqlConnection.DatabaseHandler;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewordersController implements Initializable {

    @FXML private TableView<Orders> tvOrders;
    @FXML private TableColumn<Orders, String> tcName;
    @FXML private TableColumn<Orders, Integer> tcContact;
    @FXML private TableColumn<Orders, String> tcEmail;
    @FXML private TableColumn<Orders, String> tcAddress;
    @FXML private TableColumn<Orders, List<String>> tcProduct;
    @FXML private TableColumn<Orders, Integer> tcCost;

    ObservableList<Orders> orderData = FXCollections.observableArrayList();
    DatabaseHandler handler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        try {
            loadQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tcProduct.setCellFactory(tc ->{
            TableCell cell = new TableCell();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tcProduct.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        tcAddress.setCellFactory(tc ->{
            TableCell cell = new TableCell();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tcAddress.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcContact.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        tcEmail.setCellValueFactory(new PropertyValueFactory<>("emailId"));
        tcAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        tcProduct.setCellValueFactory(new PropertyValueFactory<>("products"));
        tcCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        tvOrders.setItems(orderData);
    }


    public void loadQuery() throws SQLException {
        String query1 = "Select c.customer_id,c.first_name, c.contact_no, c.email_id, c.address, sum(o.total) FROM orders o, product p, customer c where o.customer_id=c.customer_id and o.product_id=p.product_id and o.status=1 group by c.customer_id";
        ResultSet resultSet1 = handler.executeQuery(query1);
        while (resultSet1.next()){
            String query2 = "Select p.product_name From orders o, product p, customer c where o.customer_id=? and o.product_id=p.product_id and o.status=1 group by p.product_id;";
            PreparedStatement stmt = handler.conn.prepareStatement(query2);
            stmt.setInt(1,resultSet1.getInt("customer_id"));
            ResultSet resultSet2 = stmt.executeQuery();
            String name = resultSet1.getString("first_name");
            BigDecimal contact = resultSet1.getBigDecimal("contact_no");
            String email = resultSet1.getString("email_id");
            String address = resultSet1.getString("address");
            int cost = resultSet1.getInt("sum(o.total)");

            List<String> productsList = new ArrayList<>();
            while (resultSet2.next()){
                productsList.add(resultSet2.getString("product_name"));
            }
            String listToString = productsList.toString().replace("[","").replace("]","");
            orderData.add(new Orders(name, contact, email, address, listToString, cost));
        }
    }
}
