package com.jscriptive.moneyfx.ui.account.dialog;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class AccountDialogController implements Initializable {

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

    private Node importButton;
    private Account account;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void valueChanged(Event event) {
        importButton.setDisable(anyInvalidFieldValues());
    }

    private boolean anyInvalidFieldValues() {
        return StringUtils.isAnyBlank(
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceField.getText()) || !NumberUtils.isNumber(balanceField.getText());
    }

    public AccountItem getAccount() {
        return new AccountItem(
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceDateField.getValue(),
                Double.parseDouble(balanceField.getText())
        );
    }

    public void setDisabledNodeToObserve(Node importButton) {
        this.importButton = importButton;
    }

    public void setAccount(Account account) {
        this.account = account;
        if (this.account != null) {
            if (this.account.getBank() != null) {
                this.bankField.setText(this.account.getBank().getName());
            }
            if (this.account.getNumber() != null) {
                this.numberField.setText(this.account.getNumber());
            }
            if (this.account.getName() != null) {
                this.nameField.setText(this.account.getName());
            }
            if (this.account.getType() != null) {
                this.typeField.setText(this.account.getType());
            }
            if (this.account.getBalance() != null) {
                this.balanceField.setText(this.account.getBalance().toString());
            }
            if (this.account.getBalanceDate() != null) {
                this.balanceDateField.setValue(this.account.getBalanceDate());
            }
        }
    }
}
