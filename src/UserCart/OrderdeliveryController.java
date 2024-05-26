package UserCart;

import CurrentUser.CurrentUser;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class OrderdeliveryController implements Initializable {

    @FXML private Label lbldeliveryname;
    @FXML private JFXTextField txtdeliveryname;
    @FXML private TableView<ObservableList<String>> tvdeliveryproducts;
    @FXML private JFXTextArea txtdeliveryaddress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initializeDeliveryData(ResultSet resultSet) throws SQLException {
        lbldeliveryname.setText(CurrentUser.getName() +" Your Order has been placed Successfully");
        txtdeliveryname.setText(CurrentUser.getName());
        txtdeliveryaddress.setText(CurrentUser.getAddress());

        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1).replace("_"," "));
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            col.setPrefWidth(130);
            col.setResizable(false);
//            tvproducts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tvdeliveryproducts.getColumns().addAll(col);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        while(resultSet.next())
        {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                row.add(resultSet.getString(i));
            }
            data.add(row);
        }
        tvdeliveryproducts.setItems(data);
    }

}
