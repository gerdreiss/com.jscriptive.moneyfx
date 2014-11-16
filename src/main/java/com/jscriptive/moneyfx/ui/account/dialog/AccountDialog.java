package com.jscriptive.moneyfx.ui.account.dialog;

import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by jscriptive.com on 13/11/2014.
 */
public class AccountDialog extends GridPane implements Initializable {

    @FXML
    private TextField bankField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField balanceField;
    @FXML
    private DatePicker balanceDateField;
    @FXML
    private Button addButton;

    private Stage stage;
    private ObservableList<AccountItem> data;

    public static void showAndWait(Window owner, ObservableList<AccountItem> accountData) throws IOException {

        URL resource = AccountDialog.class.getResource("AccountDialog.fxml");
        FXMLLoader loader = new FXMLLoader(resource, null, new JavaFXBuilderFactory());

        InputStream in = resource.openStream();
        AccountDialog page = null;
        try {
            page = (AccountDialog) loader.load(in);
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }

        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(page));
        dialogStage.setTitle("Add account");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner);

        AccountDialog controller = loader.getController();
        controller.setStage(dialogStage);
        controller.setData(accountData);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.balanceDateField.setValue(LocalDate.now());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(ObservableList<AccountItem> accountData) {
        this.data = accountData;
    }


    public void addFired(ActionEvent actionEvent) {
        AccountItem item = new AccountItem(
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceDateField.getValue(),
                Double.valueOf(balanceField.getText()));
        this.data.add(item);
        stage.close();
    }

    public void cancelFired(ActionEvent actionEvent) {
        stage.close();
    }

    public void valueChanged(Event event) {
        addButton.setDisable(anyInvalidFieldValues());
    }

    private boolean anyInvalidFieldValues() {
        return StringUtils.isAnyBlank(
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceField.getText()) || !NumberUtils.isNumber(balanceField.getText());
    }
}
