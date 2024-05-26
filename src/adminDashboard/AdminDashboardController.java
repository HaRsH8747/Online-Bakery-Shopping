package adminDashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML ImageView ivback;
    @FXML private AnchorPane apadmindashboard;

    DatabaseHandler handler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        File file = new File("/H:/Online Bakery Shopping/icons/arrow.png");
        Image img = null;
        try {
            img = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ivback.setImage(img);
        try {
            loadproductentry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        ((Stage)apadmindashboard.getScene().getWindow()).close();
    }

    public void loadproductentry() throws IOException {
        apadmindashboard.getChildren().clear();
        Parent root = FXMLLoader.load(getClass().getResource("productentry.fxml"));
        apadmindashboard.getChildren().add(root);
    }

    public void loadviewproducts() throws IOException {
        apadmindashboard.getChildren().clear();
        Parent root = FXMLLoader.load(getClass().getResource("viewproducts.fxml"));
        apadmindashboard.getChildren().add(root);
    }

    public void loadvieworders() throws IOException {
        apadmindashboard.getChildren().clear();
        apadmindashboard.setPrefWidth(1000);
        Parent root = FXMLLoader.load(getClass().getResource("vieworders.fxml"));
        apadmindashboard.getChildren().add(root);
    }
}
