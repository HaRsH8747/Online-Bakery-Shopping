package UserCart;

import CurrentUser.CurrentUser;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsercartpaymentController implements Initializable {

    @FXML private JFXTextField txtname;
    @FXML private JFXTextField txtcardno;
    @FXML private Label lblvalidccnumber;
    @FXML private JFXTextField txtcvv;
    @FXML private JFXTextField txtamount;

    DatabaseHandler handler;
    public TableView<ObservableList<String>> tvproducts;
    public Boolean successful = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        isValidCCNumber();
    }

    public void setInitialOrderValues(String name,String amount,TableView tableView){
        txtname.setText(name);
        txtamount.setText(amount);
        tvproducts = tableView;
    }

    public void isValidCCNumber(){
        txtcardno.textProperty().addListener((observable, oldValue, newValue) -> {
            if(LuhnAlgorithm.Check(newValue)){
                lblvalidccnumber.setTextFill(Color.RED);
                lblvalidccnumber.setText("Invalid Card Number!");
            }else {
                lblvalidccnumber.setText("");
            }
        });
    }

    public void payorder() throws SQLException, IOException {
        if(txtname.getText().isEmpty()||txtcardno.getText().isEmpty()||txtcvv.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all Fields");
            alert.showAndWait();
            return;
        }
        TableColumn col = tvproducts.getColumns().get(0);
        List productFromColumn = new ArrayList();
        for (ObservableList<String> item: tvproducts.getItems()) {
            productFromColumn.add(col.getCellObservableValue(item).getValue());
        }
        String queryFormat = StringUtils.repeat(",?", productFromColumn.size()).substring(1);
        String query1 = "Update orders SET status=1 WHERE customer_id=? and product_id In(Select product_id from product where product_name In("+queryFormat+"))";
        String query2 = "Select p.product_name Product_Name,count(product_name) Quantity from orders o, product p, customer c where o.customer_id=? and o.product_id=p.product_id and o.customer_id=c.customer_id and o.status=0 group by product_name";
        PreparedStatement stmt = handler.conn.prepareStatement(query1);
        PreparedStatement stmt2 = handler.conn.prepareStatement(query2);
        stmt2.setInt(1,CurrentUser.getUserId());
        ResultSet resultSet2 = stmt2.executeQuery();
        stmt.setInt(1, CurrentUser.getUserId());
        for (int i=0;i < productFromColumn.size(); i++)
            stmt.setString(i+2,productFromColumn.get(i).toString());
        System.out.println(stmt);
        if(handler.executeUpdate(stmt)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Payment Successful");
            alert.showAndWait();
            successful = true;
//            close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("orderdelivery.fxml"));
            Parent root = loader.load();
            OrderdeliveryController orderdeliveryController = loader.getController();
            Stage deliveryStage = new Stage();
            deliveryStage.setTitle("Order Delivery");
            deliveryStage.setScene(new Scene(root));
            deliveryStage.show();
            orderdeliveryController.initializeDeliveryData(resultSet2);
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Payment Failed!!");
            alert.showAndWait();
            successful = false;
        }
    }

    public void close() {
        ((Stage)txtname.getScene().getWindow()).close();
    }
}