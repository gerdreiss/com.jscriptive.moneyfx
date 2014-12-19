package com.jscriptive.moneyfx.ui.transaction;

import com.jscriptive.moneyfx.configuration.Configuration;
import com.jscriptive.moneyfx.importer.TransactionExtractor;
import com.jscriptive.moneyfx.importer.TransactionExtractorProvider;
import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.*;
import com.jscriptive.moneyfx.ui.account.dialog.AccountDialog;
import com.jscriptive.moneyfx.ui.category.dialog.CategoryDialog;
import com.jscriptive.moneyfx.ui.common.AccountStringConverter;
import com.jscriptive.moneyfx.ui.item.AccountItem;
import com.jscriptive.moneyfx.ui.item.TransactionItem;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionFilterDialog;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionImportDialog;
import com.jscriptive.moneyfx.util.CurrencyFormat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.controlsfx.dialog.ProgressDialog;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.DoubleStream;

import static com.jscriptive.moneyfx.model.TransactionFilter.DEFAULT_FILTER;
import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static javafx.application.Platform.runLater;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.YES;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @author jscriptive.com
 */
public class TransactionFrame implements Initializable {

    private static Logger log = Logger.getLogger(TransactionFrame.class);

    @FXML
    private TableView<TransactionItem> dataTable;
    @FXML
    private TableColumn<TransactionItem, String> accountColumn;
    @FXML
    private TableColumn<TransactionItem, String> categoryColumn;
    @FXML
    private TableColumn<TransactionItem, ImageView> transferColumn;
    @FXML
    private TableColumn<TransactionItem, String> conceptColumn;
    @FXML
    private TableColumn<TransactionItem, LocalDate> dtOpColumn;
    @FXML
    private TableColumn<TransactionItem, LocalDate> dtValColumn;
    @FXML
    private TableColumn<TransactionItem, BigDecimal> amountColumn;
    @FXML
    private Label dataSummaryLabel;

    private final ObservableList<TransactionItem> transactionData = FXCollections.observableArrayList();

    private CountryRepository countryRepository;
    private BankRepository bankRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    private TransactionFilter currentFilter;

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
        TransactionFilterDialog dialog = new TransactionFilterDialog(currentFilter);
        Optional<TransactionFilter> result = dialog.showAndWait();
        if (result.isPresent()) {
            currentFilter = result.get();
            filterTransactions(currentFilter);
        }
    }

    public void contextMenuItemEditSelected(ActionEvent actionEvent) {
        // TODO implement edit transaction
    }

    public void contextMenuItemDeleteSelected(ActionEvent actionEvent) {
        // TODO implement delete transaction
    }

    public void categorizeTransactionsFired(ActionEvent actionEvent) {
        categorizeTransactions();
    }

    public void contextMenuItemCategorizeSelected(ActionEvent actionEvent) {
        categorizeTransactions();
    }

    private void categorizeTransactions() {
        ObservableList<TransactionItem> selectedItems = dataTable.getSelectionModel().getSelectedItems();
        if (isEmpty(selectedItems)) {
            String contentText = "You didn't select any transaction from the table. Do you really want to categorize all transaction currently displayed?";
            Alert confirmation = new Alert(CONFIRMATION, contentText, YES, CANCEL);
            confirmation.setTitle("Confirm selection");
            confirmation.setHeaderText("Confirm transformation selection");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (YES == result.get()) {
                selectedItems = dataTable.getItems();
            } else {
                return;
            }
        }
        if (isNotEmpty(selectedItems)) {
            List<Account> accounts = accountRepository.findAll();
            AccountStringConverter converter = new AccountStringConverter(accounts);
            List<Transaction> toCategorize =
                    selectedItems.stream().map(item -> new Transaction(
                            converter.fromString(item.getAccount()),
                            new Category(item.getCategory()),
                            item.getConcept(),
                            item.getDtOp(),
                            item.getDtVal(),
                            item.getAmount()
                    )).collect(toList());
            CategoryDialog dialog = new CategoryDialog(categoryRepository.findAll());
            Optional<Pair<Category, Boolean>> result = dialog.showAndWait();
            if (result.isPresent()) {
                Category category = result.get().getKey();
                if (category.getId() == null) {
                    categoryRepository.save(category);
                }
                List<Transaction> transactions =
                        (currentFilter == null)
                                ? transactionRepository.findAll()
                                : transactionRepository.filterAll(currentFilter);
                toCategorize.forEach(aux -> {
                    Optional<Transaction> first = transactions.parallelStream().filter(t -> t.equals(aux)).findFirst();
                    if (first.isPresent()) {
                        Transaction persisted = first.get();
                        persisted.setCategory(category);
                        transactionRepository.save(persisted);
                    }
                });
                filterTransactions(currentFilter);
            }
        }
    }

    private void setupRepositories() {
        countryRepository = RepositoryProvider.getInstance().getCountryRepository();
        bankRepository = RepositoryProvider.getInstance().getBankRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
    }

    private void setupTransactionTable() {
        dataTable.getSelectionModel().setSelectionMode(MULTIPLE);
        dataTable.setItems(transactionData);
        dataTable.addEventHandler(TAB_SELECTION, event -> {
            if (event.isWithParams()) {
                Object firstParam = event.getFirstParam();
                if (firstParam instanceof TransactionFilter) {
                    currentFilter = (TransactionFilter) firstParam;
                    if (log.isDebugEnabled()) log.debug("TabSelectionEvent received with filter: " + currentFilter);
                }
            } else {
                if (log.isDebugEnabled()) log.debug("TabSelectionEvent received without params");
            }
            filterTransactions(currentFilter);
        });
    }

    private void setupTableColumns() {
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        transferColumn.setCellValueFactory(cellData -> cellData.getValue().transferProperty());
        conceptColumn.setCellValueFactory(cellData -> cellData.getValue().conceptProperty());
        dtOpColumn.setCellValueFactory(cellData -> cellData.getValue().dtOpProperty());
        dtValColumn.setCellValueFactory(cellData -> cellData.getValue().dtValProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    private Account extractAccountData(URI file, TransactionExtractor extractor) {
        Account extracted = extractor.extractAccountData(file);
        Account found = accountRepository.findByNumber(extracted.getNumber());
        if (found == null) {
            AccountDialog dialog = new AccountDialog(new AccountItem(extracted));
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
        return account;
    }

    private void extractTransactionData(URI file, TransactionExtractor extractor, Account account) {
        List<Transaction> transactions = extractor.extractTransactionData(file);
        account.updateBalance(transactions);
        Category other = getDefaultCategory();
        List<Transaction> all = transactionRepository.findAll();
        transactions.forEach(trx -> {
            trx.setAccount(account);
            trx.setCategory(other);
            if (all.contains(trx)) {
                log.debug("Found transaction that has a duplicate in DB: " + trx);
            } else {
                persistTransaction(trx);
                transactionData.add(new TransactionItem(trx));
            }
        });
        dataTable.setItems(transactionData);
        runLater(() -> dataSummaryLabel.setText(format("Transactions: %d, volume: %s, balance: %s",
                transactionData.size(), getTransactionVolume(transactionData), getAccountBalance(accountRepository.findAll()))));
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
            trx.setCategory(category);
        }
        transactionRepository.save(trx);
    }

    private void filterTransactions(TransactionFilter filter) {
        transactionData.clear();
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Loading transactions...");
                        List<Transaction> transactions =
                                (filter == null)
                                        ? transactionRepository.filterAll(DEFAULT_FILTER)
                                        : transactionRepository.filterAll(filter);
                        for (int idx = 0; idx < transactions.size(); idx++) {
                            Transaction trx = transactions.get(idx);
                            transactionData.add(new TransactionItem(trx));
                            updateProgress(idx, transactions.size());
                        }
                        dataTable.setItems(transactionData);
                        Platform.runLater(() -> dataSummaryLabel.setText(format("Transactions: %d, volume: %s, balance: %s",
                                transactionData.size(), getTransactionVolume(transactionData), getAccountBalance(accountRepository.findAll())
                        )));
                        return null;
                    }
                };
            }
        };
        ProgressDialog progress = new ProgressDialog(service);
        progress.setTitle("Transaction loader");
        progress.setHeaderText(null);
        progress.getDialogPane().getStyleClass().clear();
        progress.getDialogPane().getStylesheets().clear();
        progress.getDialogPane().setPadding(new Insets(10, 10, 0, 10));
        service.start();
    }

    private String getTransactionVolume(List<TransactionItem> transactionItems) {
        double sum = transactionItems.parallelStream().flatMapToDouble(item -> DoubleStream.of(abs(item.getAmount().doubleValue()))).sum();
        return CurrencyFormat.getInstance().format(sum);
    }

    private String getAccountBalance(List<Account> accounts) {
        double sum = accounts.stream().flatMapToDouble(a -> DoubleStream.of(a.getBalance().doubleValue())).sum();
        return CurrencyFormat.getInstance().format(sum);
    }

}
