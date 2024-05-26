package signup;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mysqlConnection.DatabaseHandler;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController implements Initializable {

    @FXML private JFXTextField txtfirstname;
    @FXML private JFXTextField txtmiddlename;
    @FXML private JFXTextField txtlastname;
    @FXML private JFXComboBox<String> cbgender;
    @FXML private JFXTextField txtemailid;
    @FXML private Label lblValidEmail;
    @FXML private JFXTextField txtcontactno;
    @FXML private JFXTextField txtcity;
    @FXML private JFXTextArea txtaddress;
    @FXML private JFXTextField txtusername;
    @FXML private Label lblValidUsername;
    @FXML private JFXPasswordField pfpassword;

    ObservableList<String> list = FXCollections.observableArrayList("Male","Female","Other");
    DatabaseHandler handler;
    private static final int LIMIT = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbgender.setItems(list);
        handler = DatabaseHandler.getInstance();
        ValidateEmail();
        validateContactNo();
        isValidUsername();
    }

    public void isValidUsername(){
        txtusername.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = "Select * From customer Where username='"+newValue+"'";
            if(handler.isValidQuery(query)){
                lblValidUsername.setTextFill(Color.RED);
                lblValidUsername.setText("Username already exist!");
            }else {
                lblValidUsername.setText("");
            }
        });
    }

    public void validateContactNo(){
        txtcontactno.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                // Check if the new character is greater than LIMIT
                if (txtcontactno.getText().length() >= LIMIT) {
                    // if it's 11th character then just setText to previous
                    // one
                    txtcontactno.setText(txtcontactno.getText().substring(0, LIMIT));
                }
            }
        });

        txtcontactno.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtcontactno.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public boolean isValidEmail(String string){
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public void ValidateEmail(){
        txtemailid.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!isValidEmail(newValue)){
                lblValidEmail.setTextFill(Color.RED);
                lblValidEmail.setText("Invalid Email Id!");
            }else {
                lblValidEmail.setText("");
            }
        });
    }

    public void signup(){
        String FirstName = txtfirstname.getText();
        String MiddleName = txtmiddlename.getText();
        String LastName = txtlastname.getText();
        String Gender = cbgender.getValue();
        String EmailId = txtemailid.getText();
        String ContactNo = txtcontactno.getText();
        String City = txtcity.getText();
        String Address = txtaddress.getText();
        String Username = txtusername.getText();
        String Password = pfpassword.getText();

        if(FirstName.isEmpty()||MiddleName.isEmpty()||LastName.isEmpty()||
                Gender.isEmpty()||EmailId.isEmpty()||ContactNo.isEmpty()||
                City.isEmpty()||Address.isEmpty()||Username.isEmpty()||Password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all Fields");
            alert.showAndWait();
            return;
        }

        String query = "INSERT INTO obs.customer (`first_name`, `middle_name`, `last_name`, `gender`, `email_id`, `contact_no`, `city`, `address`, `username`, `password`) VALUES("+
                "'"+FirstName+"'," + "'"+MiddleName+"',"+
                "'"+LastName+"'," + "'"+Gender+"',"+
                "'"+EmailId+"'," + "'"+ContactNo+"',"+
                "'"+City+"'," + "'"+Address+"',"+
                "'"+Username+"'," + "'"+Password+"'"+
                ")";

        if(handler.executeAction(query)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Registration Successful");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Registration Failed!!");
            alert.showAndWait();
        }
    }

    public void close(){
        ((Stage)txtfirstname.getScene().getWindow()).close();
    }
}
