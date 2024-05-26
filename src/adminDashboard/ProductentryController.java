package adminDashboard;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProductentryController implements Initializable {

    @FXML private JFXTextField txtproductname;
    @FXML private ImageView ivproductimage;
    @FXML private JFXTextArea txtdescription;
    @FXML private JFXTextField txtprice;
    @FXML private JFXComboBox<String> cbcategory;
    @FXML private JFXTextField txtaddcategory;

    DatabaseHandler handler;
    File file;
    ObservableList<String> categories = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        try {
            loadCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCategory() throws SQLException {
        String query = "Select distinct category from product";
        ResultSet rs = handler.executeQuery(query);
        while(rs.next()){
            categories.add(rs.getString("category"));
        }
        cbcategory.setItems(categories);
    }

    public void clearFields() {
        txtproductname.clear(); ivproductimage.setImage(null); txtdescription.clear(); txtprice.clear();
        cbcategory.setValue("");
    }

    public void fileChooser() throws MalformedURLException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        file = fileChooser.showOpenDialog(new Stage());
        Image img = new Image(file.toURI().toURL().toExternalForm());
        ivproductimage.setImage(img);
    }

    public void addProduct() throws SQLException, MalformedURLException {
        String query = "INSERT INTO product (product_name, image, s_description, price, category) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = handler.conn.prepareStatement(query);
        stmt.setString(1,txtproductname.getText());
        stmt.setString(2,file.toURI().toURL().toExternalForm());
        stmt.setString(3,txtdescription.getText());
        stmt.setString(4,txtprice.getText());
        if(cbcategory.isVisible()){
            stmt.setString(5,cbcategory.getValue());
        }else {
            stmt.setString(5,txtaddcategory.getText());

        }

        if(handler.executeUpdate(stmt)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Product Added Successfully");
            alert.showAndWait();
            txtaddcategory.setVisible(false);
            cbcategory.setVisible(true);
            loadCategory();
            clearFields();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Product Insertion Failed!!");
            alert.showAndWait();
        }
    }

    public void addCategory(){
        txtaddcategory.setVisible(true);
        cbcategory.setVisible(false);
    }
}
