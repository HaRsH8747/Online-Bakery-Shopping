package adminDashboard;

import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import org.apache.commons.lang.StringUtils;
import products.ProductdetailController;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewproductsController implements Initializable {

    @FXML private JFXComboBox<String> cbCategory;
    @FXML private JFXTextField txtFrom;
    @FXML private JFXTextField txtTo;
    @FXML private TilePane tpProductList;

    DatabaseHandler handler;
    ObservableList<String> categories = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
        try {
            loadProducts();
        } catch (SQLException | MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            loadCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCategory() throws SQLException {
        String query = "Select distinct category from product";
        ResultSet rs = handler.executeQuery(query);
        categories.add("All");
        while(rs.next()){
            categories.add(rs.getString("category"));
        }
        cbCategory.setItems(categories);
    }

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
//        vbproduct.setOnMouseClicked(event -> {
//            loadProductDetail(id);
//        });
        tpProductList.getChildren().add(vbproduct);
    }

    public void loadProducts() throws SQLException, MalformedURLException {

        tpProductList.setHgap(30);
        tpProductList.setVgap(15);
        tpProductList.setAlignment(Pos.TOP_LEFT);
        String query = "Select * From product";
        ResultSet rs = handler.executeQuery(query);
        while(rs.next()){
//            System.out.println(rs);
            fetchproduct(rs);
        }
    }

    public void search() throws SQLException {
        String selectedCategory = cbCategory.getSelectionModel().getSelectedItem();
        ObservableList<String> allCategories = cbCategory.getItems();
        String QueryFormat = "?";
//        System.out.println(cbCategory.getItems());
        if(selectedCategory == null || selectedCategory == "All"){
            QueryFormat = StringUtils.repeat(",?", allCategories.size()).substring(1);
        }

        String query = "Select * From product where category In("+QueryFormat+") and (price>=? and price<=?)";
        PreparedStatement stmt = handler.conn.prepareStatement(query);
        int i;
        if (selectedCategory == null || selectedCategory == "All"){
            for (i = 0; i < allCategories.size(); i++)
                stmt.setString(i+1, allCategories.get(i));
        }else {
            i=1;
            stmt.setString(i, selectedCategory);
        }
        i++;
        if(txtFrom.getText().equals("")) {
            stmt.setInt(i,0);
        }else {
            stmt.setInt(i, Integer.parseInt(txtFrom.getText()));
        }
        i++;
        if(txtTo.getText().equals("")) {
            stmt.setInt(i,100000000);
        }else {
            stmt.setInt(i,Integer.parseInt(txtTo.getText()));
        }
        ResultSet rs = stmt.executeQuery();
//        System.out.println(stmt);
        tpProductList.getChildren().clear();
        while (rs.next()){
            fetchproduct(rs);
        }
    }
}
