/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.account;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.jscriptive.moneyfx.repository.BankRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class AccountFrame implements Initializable {

    @FXML
    private TableView<AccountItem> dataTable;
    @FXML
    private TableColumn<AccountItem, String> bankColumn;
    @FXML
    private TableColumn<AccountItem, String> numberColumn;
    @FXML
    private TableColumn<AccountItem, String> nameColumn;
    @FXML
    private TableColumn<AccountItem, String> typeColumn;
    @FXML
    private TableColumn<AccountItem, String> formattedBalanceColumn;
    @FXML
    private TableColumn<AccountItem, String> balanceDateColumn;

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<AccountItem> accountData = FXCollections.observableArrayList();
    private BankRepository bankRepository;
    private AccountRepository accountRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        setupAccountTable();
        initializeColumns();
    }

    private void setupAccountTable() {
        dataTable.setItems(accountData);
        dataTable.addEventHandler(TabSelectionEvent.TAB_SELECTION, event -> loadAccountData());
    }

    private void loadAccountData() {
        accountData.clear();
        accountRepository.findAll().forEach(account ->
                accountData.add(new AccountItem(
                        account.getBank().getName(),
                        account.getNumber(),
                        account.getName(),
                        account.getType(),
                        account.getBalanceDate(),
                        account.getBalance().doubleValue()
                )));
    }

    private void initializeColumns() {
        bankColumn.setCellValueFactory(cellData -> cellData.getValue().bankProperty());
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        formattedBalanceColumn.setCellValueFactory(cellData -> cellData.getValue().formattedBalanceProperty());
        balanceDateColumn.setCellValueFactory(cellData -> cellData.getValue().balanceDateProperty());
    }

    public void addAccountFired(ActionEvent actionEvent) {
        AccountDialog dialog = new AccountDialog();
        Optional<AccountItem> accountItem = dialog.showAndWait();
        if (accountItem.isPresent()) {
            accountData.add(accountItem.get());
            persistAccount(accountItem.get());
        }
    }

    private void persistAccount(AccountItem item) {
        Bank bank = bankRepository.findByName(item.getBank());
        if (bank == null) {
            bank = new Bank(item.getBank());
            bankRepository.insert(bank);
        }
        Account account = new Account(bank, item.getNumber(), item.getName(), item.getType(), new BigDecimal(item.getBalance()));
        account.setBalanceDate(LocalDate.parse(item.getBalanceDate()));
        accountRepository.insert(account);
    }
}
