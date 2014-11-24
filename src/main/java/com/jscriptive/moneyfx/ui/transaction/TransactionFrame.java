package com.jscriptive.moneyfx.ui.transaction;

import com.jscriptive.moneyfx.importer.TransactionExtractor;
import com.jscriptive.moneyfx.importer.TransactionExtractorProvider;
import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.*;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionFilterDialog;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionImportDialog;
import com.jscriptive.moneyfx.ui.transaction.item.TransactionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;
import static javafx.scene.control.SelectionMode.MULTIPLE;

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

    private final ObservableList<TransactionItem> transactionData = FXCollections.observableArrayList();

    private boolean filtered = false;

    private BankRepository bankRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRepositories();
        setupTransactionTable();
        setupTableColumns();
    }

    public void importTransactionsFired(ActionEvent actionEvent) {
        TransactionImportDialog dialog = new TransactionImportDialog(bankRepository.findAll());
        Optional<Pair<String, File>> result = dialog.showAndWait();
        if (result.isPresent()) {
            String bank = result.get().getKey();
            File file = result.get().getValue();
            TransactionExtractor extractor = TransactionExtractorProvider.getInstance().getTransactionExtractor(bank);
            Account account = extractAccountData(file.toURI(), extractor);
            if (account != null) {
                extractTransactionData(file.toURI(), extractor, account);
            }
        }
    }

    public void filterTransactionsFired(ActionEvent actionEvent) {
        Button b = (Button) actionEvent.getTarget();
        if (filtered) {
            loadTransactionData();
            b.setText("Filter transactions");
            filtered = false;
        } else {
            TransactionFilterDialog dialog = new TransactionFilterDialog();
            Optional<TransactionFilter> result = dialog.showAndWait();
            if (result.isPresent()) {
                filterTransactionData(result.get());
                b.setText("Reload transactions");
                filtered = true;
            }
        }
    }

    private void setupRepositories() {
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
    }

    private void setupTransactionTable() {
        dataTable.getSelectionModel().setSelectionMode(MULTIPLE);
        dataTable.setItems(transactionData);
        dataTable.addEventHandler(TAB_SELECTION, event -> loadTransactionData());
    }

    private void loadTransactionData() {
        transactionData.clear();
        transactionRepository.findAll().forEach(trx ->
                transactionData.add(new TransactionItem(
                        trx.getAccount().getBank().getName() + trx.getAccount().getLastFourDigits(),
                        trx.getCategory().getName(),
                        trx.getConcept(),
                        trx.getDtOp().format(DATE_FORMATTER),
                        trx.getDtVal().format(DATE_FORMATTER),
                        trx.getFormattedAmount()
                )));
    }

    private void setupTableColumns() {
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        conceptColumn.setCellValueFactory(cellData -> cellData.getValue().conceptProperty());
        dtOpColumn.setCellValueFactory(cellData -> cellData.getValue().dtOpProperty());
        dtValColumn.setCellValueFactory(cellData -> cellData.getValue().dtValProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    private Account extractAccountData(URI file, TransactionExtractor extractor) {
        Account extracted = extractor.extractAccountData(file);
        Account found = accountRepository.findByNumber(extracted.getNumber());
        if (found == null) {
            AccountDialog dialog = new AccountDialog(new AccountItem(
                    extracted.getBank().getName(),
                    extracted.getNumber(),
                    extracted.getName(),
                    extracted.getType(),
                    extracted.getBalanceDate(),
                    extracted.getBalance().doubleValue()
            ));
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
            bankRepository.save(bank);
        }
        Account account = new Account(
                bank,
                item.getNumber(),
                item.getName(),
                item.getType(),
                new BigDecimal(item.getBalance()),
                LocalDate.parse(item.getBalanceDate(), DATE_FORMATTER));
        accountRepository.save(account);
        return account;
    }

    private void extractTransactionData(URI file, TransactionExtractor extractor, Account account) {
        List<Transaction> transactions = extractor.extractTransactionData(file);
        account.updateBalance(transactions);
        Category other = getDefaultCategory();
        transactions.forEach(trx -> {
            trx.setAccount(account);
            trx.setCategory(other);
            persistTransaction(trx);
        });
        transactions.forEach(trx -> transactionData.add(new TransactionItem(
                trx.getAccount().getBank().getName() + trx.getAccount().getLastFourDigits(),
                trx.getCategory().getName(),
                trx.getConcept(),
                trx.getDtOp().format(DATE_FORMATTER),
                trx.getDtVal().format(DATE_FORMATTER),
                trx.getFormattedAmount()
        )));
    }

    private Category getDefaultCategory() {
        Category other = categoryRepository.findByName(Category.OTHER.getName());
        if (other == null) {
            other = Category.OTHER;
            categoryRepository.save(other);
        }
        return other;
    }

    private void persistTransaction(Transaction trx) {
        Category category = categoryRepository.findByName(trx.getCategory().getName());
        if (category == null) {
            category = new Category(trx.getCategory().getName());
            categoryRepository.save(category);
        }
        transactionRepository.save(trx);
    }

    private void filterTransactionData(TransactionFilter filter) {
        transactionData.clear();
        transactionRepository.filterAll(filter).forEach(trx ->
                transactionData.add(new TransactionItem(
                        trx.getAccount().getBank().getName() + trx.getAccount().getLastFourDigits(),
                        trx.getCategory().getName(),
                        trx.getConcept(),
                        trx.getDtOp().format(DATE_FORMATTER),
                        trx.getDtVal().format(DATE_FORMATTER),
                        trx.getFormattedAmount()
                )));
    }

}
