package login;

import CurrentUser.CurrentUser;
import adminDashboard.AdminDashboard;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import products.ProductList;
import signup.SignUp;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private JFXTextField txtuserid;
    @FXML private JFXPasswordField pfpassword;
    @FXML private JFXRadioButton customer;
    @FXML private JFXRadioButton owner;

    Stage primaryStage = new Stage();
    DatabaseHandler handler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
    }
//    public void RoleOfPerson(){
//        if (customer.isSelected()){
//            userid.setText(customer.getText());
//        }
//        if (owner.isSelected()){
//            userid.setText(owner.getText());
//        }
//    }

    public void login() throws Exception {
        if(txtuserid.getText().equals("admin") && pfpassword.getText().equals("admin") && owner.isSelected()){
//            Stage adminStage = new Stage();
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.start(primaryStage);
        }else {
            String query = "Select * From customer where username=? and password=?";
            PreparedStatement stmt = handler.conn.prepareStatement(query);
            stmt.setString(1, txtuserid.getText());
            stmt.setString(2, pfpassword.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ProductList productList = new ProductList();
                CurrentUser.UserName = txtuserid.getText();
                productList.start(primaryStage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Login Failed!!");
                alert.showAndWait();
                return;
            }
        }
    }
    public void signup() throws Exception {
        SignUp signup = new SignUp();
        if (primaryStage != null)
            signup.start(primaryStage);
    }
}
