/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.account;

import com.jscriptive.moneyfx.configuration.Configuration;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.Country;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.*;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent;
import com.jscriptive.moneyfx.ui.item.AccountItem;
import com.jscriptive.moneyfx.util.CurrencyFormat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.DoubleStream;

import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static java.lang.Math.abs;
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
    private TableColumn<AccountItem, String> countryColumn;
    @FXML
    private TableColumn<AccountItem, String> numberColumn;
    @FXML
    private TableColumn<AccountItem, String> nameColumn;
    @FXML
    private TableColumn<AccountItem, String> typeColumn;
    @FXML
    private TableColumn<AccountItem, BigDecimal> balanceColumn;
    @FXML
    private TableColumn<AccountItem, LocalDate> balanceDateColumn;
    @FXML
    private Label dataSummaryLabel;

    private final ObservableList<AccountItem> accountData = FXCollections.observableArrayList();

    private CountryRepository countryRepository;
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
        countryRepository = RepositoryProvider.getInstance().getCountryRepository();
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
        accountRepository.findAll().forEach(account -> accountData.add(new AccountItem(account)));
        Platform.runLater(() -> dataSummaryLabel.setText("Accounts: " + accountData.size() + ", volume: " + getAbsSum(accountData)));
    }

    private void initializeColumns() {
        bankColumn.setCellValueFactory(cellData -> cellData.getValue().bankProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
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
            Country country = countryRepository.findByCode(item.getCountry());
            if (country == null) {
                country = Country.fromCountryCode(item.getCountry());
                countryRepository.save(country);
            }
            bank = new Bank(item.getBank(), country);
            bank.setTransferConceptRegex(Configuration.getInstance().getTransferConceptRegexFor(bank));
            bankRepository.save(bank);
        }
        Account account = new Account(bank,
                item.getNumber(),
                item.getName(),
                item.getType(),
                item.getBalance(),
                item.getBalanceDate());
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
                String countryCode = result.get().getCountry();
                if (!StringUtils.equals(account.getBank().getCountryCode(), countryCode)) {
                    Country country = countryRepository.findByCode(countryCode);
                    if (country == null) {
                        country = Country.fromCountryCode(countryCode);
                        countryRepository.save(country);
                    }
                    account.getBank().setCountry(country);
                    account.getBank().setTransferConceptRegex(Configuration.getInstance().getTransferConceptRegexFor(account.getBank()));
                    bankRepository.save(account.getBank());
                }
                account.setName(result.get().getName());
                account.setType(result.get().getType());
                account.setBalance(result.get().getBalance());
                account.setBalanceDate(result.get().getBalanceDate());
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

    private String getAbsSum(List<AccountItem> accountItems) {
        double sum = accountItems.parallelStream().flatMapToDouble(item -> DoubleStream.of(abs(item.getBalance().doubleValue()))).sum();
        return CurrencyFormat.getInstance().format(sum);
    }
}
