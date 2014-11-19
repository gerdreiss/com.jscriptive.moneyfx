package com.jscriptive.moneyfx.ui.transaction;

import com.jscriptive.moneyfx.exception.BusinessException;
import com.jscriptive.moneyfx.importer.TransactionExtractor;
import com.jscriptive.moneyfx.importer.TransactionExtractorProvider;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.repository.*;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionImportDialog;
import com.jscriptive.moneyfx.ui.transaction.item.TransactionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class TransactionFrame implements Initializable {

    @FXML
    private TableView<TransactionItem> dataTable;
    @FXML
    private TableColumn<TransactionItem, String> accountColumn;
    @FXML
    private TableColumn<TransactionItem, String> categoryColumn;
    @FXML
    private TableColumn<TransactionItem, String> conceptColumn;
    @FXML
    private TableColumn<TransactionItem, String> dtOpColumn;
    @FXML
    private TableColumn<TransactionItem, String> dtValColumn;
    @FXML
    private TableColumn<TransactionItem, String> amountColumn;

    private ObservableList<TransactionItem> transactionData = FXCollections.observableArrayList();

    private BankRepository bankRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRepositories();
        setupTransactionTable();
    }

    private void setupRepositories() {
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
    }

    private void setupTransactionTable() {
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        conceptColumn.setCellValueFactory(cellData -> cellData.getValue().conceptProperty());
        dtOpColumn.setCellValueFactory(cellData -> cellData.getValue().dtOpProperty());
        dtValColumn.setCellValueFactory(cellData -> cellData.getValue().dtValProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        dataTable.setItems(transactionData);
        dataTable.addEventHandler(TabSelectionEvent.TAB_SELECTION, event -> loadTransactionData());
    }

    private void loadTransactionData() {
        transactionData.clear();
        transactionRepository.findAll().forEach(trx -> transactionData.add(new TransactionItem(
                trx.getAccount().getBank().getName() + trx.getAccount().getLastFourDigits(),
                trx.getCategory().getName(),
                trx.getConcept(),
                trx.getDtOp().toString(),
                trx.getDtVal().toString(),
                trx.getFormattedAmount()
        )));
    }

    public void importTransactionsFired(ActionEvent actionEvent) {
        TransactionImportDialog dialog = new TransactionImportDialog(bankRepository.findAll());
        Optional<Pair<Bank, File>> result = dialog.showAndWait();
        if (result.isPresent()) {
            Bank bank = result.get().getKey();
            if (bank.getId() == null) {
                bankRepository.insert(bank);
            }
            TransactionExtractor extractor = TransactionExtractorProvider.getInstance().getTransactionExtractor(bank);
            Account account = extractAccountData(result.get().getValue().toURI(), bank, extractor);
            if (account == null) {
                throw new BusinessException("No account created!");
            }
            if (!bank.equals(account.getBank())) {
                throw new BusinessException("Bank changed when creating the account");
            }
            extractTransactionData(result.get().getValue().toURI(), extractor, account);
        }
    }

    private Account extractAccountData(URI file, Bank bank, TransactionExtractor extractor) {
        Account extracted = extractor.extractAccountData(file);
        Account found = accountRepository.findByNumber(extracted.getNumber());
        if (found == null) {
            extracted.setBank(bank);
            AccountDialog dialog = new AccountDialog(extracted);
            Optional<AccountItem> accountItem = dialog.showAndWait();
            if (accountItem.isPresent()) {
                found = persistAccount(accountItem.get());
            }
        }
        return found;
    }

    private Account persistAccount(AccountItem item) {
        Bank bank = bankRepository.findByName(item.getBank());
        if (bank == null) {
            bank = new Bank(item.getBank());
            bankRepository.insert(bank);
        }
        Account account = new Account(bank, item.getNumber(), item.getName(), item.getType(), new BigDecimal(item.getBalance()));
        account.setBalanceDate(LocalDate.parse(item.getBalanceDate()));
        accountRepository.insert(account);
        return account;
    }

    private void extractTransactionData(URI file, TransactionExtractor extractor, Account account) {
        List<Transaction> transactions = extractor.extractTransactionData(file);
        account.updateBalance(transactions);
        transactions.forEach(trx -> {
            trx.setAccount(account);
            persistTransaction(trx);
        });
        transactions.forEach(trx -> transactionData.add(new TransactionItem(
                trx.getAccount().getNumber(),
                trx.getCategory().getName(),
                trx.getConcept(),
                trx.getDtOp().toString(),
                trx.getDtVal().toString(),
                trx.getFormattedAmount()
        )));
    }

    private void persistTransaction(Transaction trx) {
        Category category = categoryRepository.findByName(trx.getCategory().getName());
        if (category == null) {
            category = new Category(trx.getCategory().getName());
            categoryRepository.insert(category);
        }
        transactionRepository.insert(trx);
    }

}
