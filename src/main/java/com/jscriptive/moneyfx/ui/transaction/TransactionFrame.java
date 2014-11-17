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
import com.jscriptive.moneyfx.ui.exception.ExceptionDialog;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionImportDialog;
import com.jscriptive.moneyfx.ui.transaction.item.TransactionItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class TransactionFrame extends BorderPane implements Initializable {

    @FXML
    private TableView<TransactionItem> transactionTable;
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
    private TableColumn<TransactionItem, Number> amountColumn;

    private ObservableList<TransactionItem> transactionData = FXCollections.observableArrayList();
    private ObservableList<AccountItem> accountData = FXCollections.observableArrayList();

    private BankRepository bankRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        transactionRepository.findAll().forEach(trx -> transactionData.add(new TransactionItem(
                trx.getAccount().getNumber(),
                trx.getCategory().getName(),
                trx.getConcept(),
                trx.getDtOp().toString(),
                trx.getDtVal().toString(),
                trx.getAmount().doubleValue()
        )));
        transactionData.addListener(new ListChangeListener<TransactionItem>() {
            @Override
            public void onChanged(Change<? extends TransactionItem> c) {
                if (c.next()) {
                    List<? extends TransactionItem> added = c.getAddedSubList();
                    if (added.isEmpty()) {
                        return;
                    }
                    added.forEach(item -> persistTransaction(item));
                }
            }
        });
        accountData.addListener(new ListChangeListener<AccountItem>() {
            @Override
            public void onChanged(Change<? extends AccountItem> c) {
                if (c.next()) {
                    List<? extends AccountItem> added = c.getAddedSubList();
                    if (added.isEmpty()) {
                        return;
                    }
                    added.forEach(item -> persistAccount(item));
                }
            }
        });
        transactionTable.setItems(transactionData);
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        conceptColumn.setCellValueFactory(cellData -> cellData.getValue().conceptProperty());
        dtOpColumn.setCellValueFactory(cellData -> cellData.getValue().dtOpProperty());
        dtValColumn.setCellValueFactory(cellData -> cellData.getValue().dtValProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    private void persistTransaction(TransactionItem item) {
        Account account = accountRepository.findByNumber(item.getAccount());
        Category category = categoryRepository.findByName(item.getCategory());
        if (category == null) {
            category = new Category(item.getCategory());
            categoryRepository.insert(category);
        }
        transactionRepository.insert(new Transaction(
                account,
                category,
                item.getConcept(),
                LocalDate.parse(item.getDtOp()),
                LocalDate.parse(item.getDtVal()),
                new BigDecimal(item.getAmount())
        ));
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

    public void importTransactionsFired(ActionEvent actionEvent) {
        TransactionImportDialog dialog = new TransactionImportDialog(bankRepository.findAll());
        Optional<Pair<Bank, File>> result = dialog.showAndWait();
        result.ifPresent(bankFile -> {
            Bank bank = bankFile.getKey();
            if (bank.getId() == null) {
                bankRepository.insert(bank);
            }
            TransactionExtractor extractor = TransactionExtractorProvider.getInstance().getTransactionExtractor(bank);
            Account extracted = extractor.extractAccountData(bankFile.getValue().toURI());
            Account found = accountRepository.findByNumber(extracted.getNumber());
            if (found == null) {
                extracted.setBank(bank);
                try {
                    AccountDialog.showAndWait(transactionTable.getScene().getWindow(), accountData, extracted);
                } catch (Exception e) {
                    ExceptionDialog alert = new ExceptionDialog(e);
                    alert.showAndWait();
                }
            }
            Account account = accountRepository.findByNumber(extracted.getNumber());
            if (account != null) {
                if (!bank.equals(account.getBank())) {
                    throw new BusinessException("Bank changed when creating the account");
                }
                List<Transaction> transactions = extractor.extractTransactionData(bankFile.getValue().toURI());
                account.updateBalance(transactions);
                transactions.forEach(trx -> trx.setAccount(account));
                transactions.forEach(trx -> transactionData.add(new TransactionItem(
                        trx.getAccount().getNumber(),
                        trx.getCategory().getName(),
                        trx.getConcept(),
                        trx.getDtOp().toString(),
                        trx.getDtVal().toString(),
                        trx.getAmount().doubleValue()
                )));
            }
        });
    }
}
