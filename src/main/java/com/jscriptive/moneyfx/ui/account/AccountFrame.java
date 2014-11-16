/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.account;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.exception.ExceptionDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class AccountFrame extends BorderPane implements Initializable {

    @FXML
    private TableView<AccountItem> accountTable;
    @FXML
    private TableColumn<AccountItem, String> bankColumn;
    @FXML
    private TableColumn<AccountItem, String> numberColumn;
    @FXML
    private TableColumn<AccountItem, String> nameColumn;
    @FXML
    private TableColumn<AccountItem, String> typeColumn;
    @FXML
    private TableColumn<AccountItem, Number> balanceColumn;
    @FXML
    private TableColumn<AccountItem, String> balanceDateColumn;


    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<AccountItem> accountData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accountData.addListener(new ListChangeListener<AccountItem>() {
            @Override
            public void onChanged(Change<? extends AccountItem> c) {
                List<? extends AccountItem> added = c.getAddedSubList();
                if (added.isEmpty()) {
                    return;
                }
                added.forEach(item -> persistAccount(item));
            }
        });
        accountTable.setItems(accountData);
        bankColumn.setCellValueFactory(cellData -> cellData.getValue().bankProperty());
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        balanceDateColumn.setCellValueFactory(cellData -> cellData.getValue().balanceDateProperty());
    }

    private void persistAccount(AccountItem item) {
        Account account = new Account(new Bank(item.getName()), item.getNumber(), item.getName(), new BigDecimal(item.getBalance()));
        account.setBalanceDate(LocalDate.parse(item.getBalanceDate()));
        // TODO Persist the new account item

    }

    public void addAccountFired(ActionEvent actionEvent) {
        try {
            AccountDialog.showAndWait(accountTable.getScene().getWindow(), accountData);
        } catch (Exception e) {
            ExceptionDialog alert = new ExceptionDialog(e);
            alert.showAndWait();
        }
    }

}
