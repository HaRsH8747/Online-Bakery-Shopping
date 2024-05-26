package products;

import UserCart.UserCart;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ResourceBundle;

public class ProductListController implements Initializable {
    @FXML private TilePane tpproductlistview;
    @FXML private ScrollPane spproduct;
    @FXML private JFXListView lvcategory;
    @FXML private JFXTextField txtsearch;
    @FXML private Label lblsearch;
    @FXML private JFXTextField txtfrom;
    @FXML private JFXTextField txtto;
    @FXML private ImageView ivback;
    @FXML private JFXButton btnback;


    DatabaseHandler handler;
    ObservableList<String> categories = FXCollections.observableArrayList();
    String validProductName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("/H:/Online Bakery Shopping/icons/arrow.png");
        Image img = null;
        searchFilter();
        try {
            img = new Image(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ivback.setImage(img);
        handler = DatabaseHandler.getInstance();
        lvcategory.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            loadCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            loadProducts();
        } catch (SQLException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void searchFilter(){
        txtsearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!txtsearch.getText().equals("")){
//                System.out.println(txtsearch.getText());
                try {
                    if(isValidProductName(newValue)){
                        lblsearch.setText("");
                        search();
                    }
                    else{
                        lblsearch.setTextFill(Color.RED);
                        lblsearch.setText("No Such Product Exist!!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isValidProductName(String newValue) throws SQLException {
        String query = "Select * From product where product_name REGEXP '^"+newValue+"'";
        ResultSet resultSet = handler.executeQuery(query);
        if(resultSet.next()){
            validProductName = newValue;
            return true;
        }else {
            return false;
//            lblsearch.setText("No Such Product Exist!!");
        }
    }

    public void close(){
        ((Stage)tpproductlistview.getScene().getWindow()).close();
    }

    public void loadCategory() throws SQLException {
        String query = "Select distinct category from product";
        ResultSet rs = handler.executeQuery(query);
        while(rs.next()){
            categories.add(rs.getString("category"));
        }
        lvcategory.setItems(categories);
    }

//    public void fileChooser() throws MalformedURLException, SQLException {
//        Stage stage = new Stage();
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select Image");
//        File file = fileChooser.showOpenDialog(stage);
//        String query = "UPDATE product SET image = ? WHERE (product_id = 1)";
//        PreparedStatement stmt = handler.conn.prepareStatement(query);
//        System.out.println(file.toURI().toURL().toExternalForm());
//        stmt.setString(1,file.toURI().toURL().toExternalForm());
//        stmt.executeUpdate();
//        loadProducts();
//    }

    public void loadProductDetail(int id){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("productdetail.fxml"));
            Parent root = loader.load();
            Stage productdetail = new Stage();
            productdetail.setTitle("Product Detail");
            ProductdetailController pdc = loader.getController();
            pdc.mysqlHandler(id);
            productdetail.setScene(new Scene(root));
            productdetail.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void fetchproduct(ResultSet rs) throws SQLException {
        VBox vbproduct = new VBox();
        vbproduct.setCursor(Cursor.OPEN_HAND);
        vbproduct.setMinWidth(215);
        vbproduct.setMinHeight(200);
        vbproduct.setAlignment(Pos.TOP_CENTER);
        Image img = new Image(rs.getString("image"));
        ImageView productView = new ImageView(img);
        productView.setFitWidth(168);
        productView.setFitHeight(130);
        int id = rs.getInt("product_id");
        Label productName = new Label(rs.getString("product_name"));
        Label productPrice = new Label("Rs. "+rs.getString("price"));
        productName.setTextFill(Color.rgb(255, 255, 141));
        productPrice.setTextFill(Color.rgb(255, 255, 141));
        vbproduct.setSpacing(10);
        vbproduct.getChildren().addAll(productView,productName,productPrice);
        vbproduct.setOnMouseClicked(event -> {
            loadProductDetail(id);
        });
        tpproductlistview.getChildren().add(vbproduct);
    }

    public void loadProducts() throws SQLException, MalformedURLException {
        tpproductlistview.setHgap(30);
        tpproductlistview.setVgap(15);
        tpproductlistview.setAlignment(Pos.TOP_LEFT);
        String query = "Select * From product";
        ResultSet rs = handler.executeQuery(query);
        while(rs.next()){
//            System.out.println(rs);
            fetchproduct(rs);
        }
    }

    public void search() throws SQLException {
        ObservableList<String> selectedCategories = lvcategory.getSelectionModel().getSelectedItems();
        ObservableList<String> allCategories = lvcategory.getItems();
        String QueryFormat;
//        System.out.println(lvcategory.getItems());
        if(selectedCategories.isEmpty()){
            QueryFormat = StringUtils.repeat(",?", allCategories.size()).substring(1);
        }else {
            QueryFormat = StringUtils.repeat(",?", selectedCategories.size()).substring(1);
        }
        String query;
        if(isValidProductName(validProductName)){
            query = "Select * From product where category In("+QueryFormat+") and (price>=? and price<=?) and product_name REGEXP '"+validProductName+"?'";
        }else {
            query = "Select * From product where category In("+QueryFormat+") and (price>=? and price<=?)";
        }
        PreparedStatement stmt = handler.conn.prepareStatement(query);
        int i;
        if (selectedCategories.isEmpty()){
            for (i = 0; i < allCategories.size(); i++)
                stmt.setString(i+1, allCategories.get(i));
        }else {
            for (i = 0; i < selectedCategories.size(); i++)
                stmt.setString(i+1, selectedCategories.get(i));
        }
        i++;
        if(txtfrom.getText().equals("")) {
            stmt.setInt(i,0);
        }else {
            stmt.setInt(i, Integer.parseInt(txtfrom.getText()));
        }
        i++;
        if(txtto.getText().equals("")) {
            stmt.setInt(i,100000000);
        }else {
            stmt.setInt(i,Integer.parseInt(txtto.getText()));
        }
        ResultSet rs = stmt.executeQuery();
//        System.out.println(stmt);
        tpproductlistview.getChildren().clear();
        while (rs.next()){
            fetchproduct(rs);
        }
    }

    public void viewCart() throws IOException {
        Stage cartStage = new Stage();
        UserCart userCart = new UserCart();
        userCart.start(cartStage);
    }
}