package products;

import CurrentUser.CurrentUser;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static mysqlConnection.DatabaseHandler.handler;

public class ProductdetailController implements Initializable {

    @FXML private ImageView ivproduct;
    @FXML private Label lblname;
    @FXML private Label lblcost;
    @FXML private Label lbldescription;
    @FXML private TextField txtquantity;
    @FXML private ImageView ivback;
    @FXML private JFXButton btnback;

    private int product_id;
    private int price;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("/H:/Online Bakery Shopping/icons/arrow.png");
        Image img = null;
        try {
            img = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ivback.setImage(img);
        txtquantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtquantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void close(){
        ((Stage)ivproduct.getScene().getWindow()).close();
    }

    public void mysqlHandler(int id) throws SQLException {
        product_id=id;
        String query = "Select * From product where product_id="+product_id;
        ResultSet rs = handler.executeQuery(query);
        rs.next();
        Image img = new Image(rs.getString("image"));
        ivproduct.setImage(img);
        lblname.setText(rs.getString("product_name"));
        lblcost.setText("Cost: ₹"+rs.getString("price"));
        lbldescription.setWrapText(true);
        lbldescription.setText(rs.getString("s_description"));
        price = rs.getInt("price");
    }

    public void addToCart() throws SQLException {
        String query = "INSERT INTO orders (customer_id, product_id, quantity, total) VALUES (?,?,?,?)";
        PreparedStatement stmt = handler.conn.prepareStatement(query);
        stmt.setInt(1, CurrentUser.getUserId());
        stmt.setInt(2,product_id);
        stmt.setInt(3,Integer.parseInt(txtquantity.getText()));
        stmt.setInt(4,Integer.parseInt(txtquantity.getText())*price);
        stmt.executeUpdate();
    }
}
