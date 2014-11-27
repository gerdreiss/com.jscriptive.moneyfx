/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.account;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.jscriptive.moneyfx.repository.BankRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.input.KeyCode.DELETE;

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
    private final ObservableList<AccountItem> accountData = FXCollections.observableArrayList();
    private BankRepository bankRepository;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeRepositories();
        setupAccountTable();
        initializeColumns();
    }

    private void initializeRepositories() {
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
    }

    private void setupAccountTable() {
        dataTable.setItems(accountData);
        dataTable.addEventHandler(TAB_SELECTION, event -> loadAccountData());
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
            bankRepository.save(bank);
        }
        Account account = new Account(bank, item.getNumber(), item.getName(), item.getType(), new BigDecimal(item.getBalance()));
        account.setBalanceDate(LocalDate.parse(item.getBalanceDate(), DATE_FORMATTER));
        accountRepository.save(account);
    }

    public void contextMenuItemEditSelected(ActionEvent actionEvent) {
        editAccount();
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            editAccount();
        }
    }

    private void editAccount() {
        AccountItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            AccountDialog dialog = new AccountDialog(selectedItem);
            Optional<AccountItem> result = dialog.showAndWait();
            if (result.isPresent()) {
                Account account = accountRepository.findByNumber(result.get().getNumber());
                account.setName(result.get().getName());
                account.setType(result.get().getType());
                account.setBalance(BigDecimal.valueOf(result.get().getBalance()));
                account.setBalanceDate(LocalDate.parse(result.get().getBalanceDate(), DATE_FORMATTER));
                accountRepository.save(account);
                accountData.set(dataTable.getSelectionModel().getSelectedIndex(), result.get());
            }
        }
    }

    public void contextMenuItemDeleteSelected(ActionEvent actionEvent) {
        deleteAccount();
    }

    public void keyTyped(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (DELETE == keyCode) {
            deleteAccount();
        }
    }

    private void deleteAccount() {
        Alert alert = new Alert(CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText("Confirm delete account");
        AccountItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        alert.setContentText(
                String.format("Are you sure you want to delete the selected account: %s %s? All transactions of that account will also be deleted.",
                        selectedItem.getBank(), selectedItem.getNumber()));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == OK) {
            int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
            Account account = accountRepository.findByNumber(selectedItem.getNumber());
            transactionRepository.removeByAccount(account);
            accountRepository.remove(account);
            bankRepository.remove(account.getBank());
            accountData.remove(selectedIndex);
        }
    }

    public void contextMenuShowTransactions(ActionEvent actionEvent) {
        AccountItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        Account persistedAccount = accountRepository.findByNumber(selectedItem.getNumber());
        TransactionFilter filter = new TransactionFilter();
        filter.setAccount(persistedAccount);
        dataTable.getScene().lookup("#tabPane").fireEvent(new ShowTransactionsEvent(filter));
    }
}
