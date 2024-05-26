package UserCart;

import CurrentUser.CurrentUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import mysqlConnection.DatabaseHandler;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UsercartController implements Initializable {

    @FXML private TableView<ObservableList<String>> tvproducts;
    @FXML private Label lbltotalprice;
    @FXML private ContextMenu tvmenu;
    @FXML private ImageView ivback;

    private ObservableList<ObservableList<String>> data;
    DatabaseHandler handler;
    String totalAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        File file = new File("/H:/Online Bakery Shopping/icons/arrow.png");
        Image img = null;
        try {
            img = new Image(file.toURI().toURL().toExternalForm());
            ivback.setImage(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        ((Stage)lbltotalprice.getScene().getWindow()).close();
    }

    public void loadOrders() throws SQLException {
        tvproducts.getColumns().clear();
        String query1 = "Select p.product_name Product_Name, sum(o.quantity) Quantity, sum(o.total) Total from orders o, product p, customer c where o.customer_id=? and o.product_id=p.product_id and o.customer_id=c.customer_id and o.status=0 group by product_name";
        String query2 = "Select sum(o.total) total from orders o, product p, customer c where o.customer_id=? and o.product_id=p.product_id and o.customer_id=c.customer_id and o.status=0";
        PreparedStatement stmt1 = handler.conn.prepareStatement(query1);
        PreparedStatement stmt2 = handler.conn.prepareStatement(query2);

        stmt1.setInt(1, CurrentUser.getUserId());
        stmt2.setInt(1,CurrentUser.getUserId());
//        System.out.println(stmt1+"\n"+stmt2);
        ResultSet resultSet1 = stmt1.executeQuery();
        ResultSet resultSet2 = stmt2.executeQuery();
        resultSet2.next();
        totalAmount = resultSet2.getString("Total");
        lbltotalprice.setText("Total Rs. "+resultSet2.getString("Total"));

        for (int i = 0; i < resultSet1.getMetaData().getColumnCount(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(resultSet1.getMetaData().getColumnName(i + 1).replace("_"," "));
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            col.setPrefWidth(123);
            col.setResizable(false);
//            tvproducts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tvproducts.getColumns().addAll(col);
        }

        data = FXCollections.observableArrayList();
        while(resultSet1.next())
        {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet1.getMetaData().getColumnCount(); i++) {
                row.add(resultSet1.getString(i));
            }
            data.add(row);
        }
        tvproducts.setItems(data);
        if (tvproducts.getItems().isEmpty()){
            tvproducts.setPlaceholder(new Label("No Pending Orders"));
        }
    }

    public void confirmorder() throws IOException, SQLException {
        if(tvproducts.getColumns().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("First add Products to Cart!!");
            alert.showAndWait();
            return;
        }else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("usercartpayment.fxml"));
            Parent root = loader.load();
            UsercartpaymentController usercartpaymentController = loader.getController();
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Payment");
            paymentStage.setScene(new Scene(root));
            paymentStage.setOnHidden(event -> {
                if (usercartpaymentController.successful){
                    try {
                        loadOrders();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            paymentStage.show();
            usercartpaymentController.setInitialOrderValues(CurrentUser.getName(), totalAmount, tvproducts);
        }
    }

    public void deleteorder() throws SQLException {
        String deleteItem = tvproducts.getSelectionModel().getSelectedItem().get(0);
        String query1 = "Delete FROM orders WHERE customer_id=? and product_id=(Select product_id from product where product_name=?)";
        String query2 = "Select max(order_id) max_orderId from orders";
        String query3 = "ALTER TABLE orders AUTO_INCREMENT = ?";
        PreparedStatement stmt1 = handler.conn.prepareStatement(query1);
        PreparedStatement stmt3 = handler.conn.prepareStatement(query3);
        ResultSet resultSet2 = handler.executeQuery(query2);
        resultSet2.next();
        stmt1.setInt(1,CurrentUser.getUserId());
        stmt1.setString(2,deleteItem);
//        System.out.println(stmt1);
        stmt3.setInt(1,resultSet2.getInt("max_orderId")+1);
        stmt1.executeUpdate();
        stmt3.executeUpdate();
        loadOrders();
    }
}
