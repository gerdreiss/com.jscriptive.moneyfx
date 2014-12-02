/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.category;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.model.TransactionVolume;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionFilterRepository;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.category.dialog.CategoryDialog;
import com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent;
import com.jscriptive.moneyfx.ui.item.CategoryItem;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionFilterDialog;
import com.jscriptive.moneyfx.util.CurrencyFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.DoubleStream;

import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.abs;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.input.KeyCode.DELETE;

/**
 * @author jscriptive.com
 */
public class CategoryFrame implements Initializable {

    private static Logger log = Logger.getLogger(CategoryFrame.class);

    @FXML
    private TableView<CategoryItem> dataTable;
    @FXML
    private TableColumn<CategoryItem, String> nameColumn;
    @FXML
    private TableColumn<CategoryItem, BigDecimal> amountColumn;
    @FXML
    private TableColumn<CategoryItem, String> ruleColumn;
    @FXML
    private Label dataSummaryLabel;

    private final ObservableList<CategoryItem> categoryData = FXCollections.observableArrayList();

    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private TransactionFilterRepository transactionFilterRepository;

    /**
     * Initializes the controller
     *
     * @param url The URL
     * @param rb  The resource bundle for i18n
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeRepositories();
        setupCategoryTable();
        initializeColumns();
    }

    /**
     * Invoked when "Add category" button is pushed
     *
     * @param actionEvent
     */
    public void addCategoryFired(ActionEvent actionEvent) {
        addNewCategory(actionEvent);
    }

    /**
     * Invoked when "Edit" menu item from context menu is selected
     *
     * @param actionEvent
     */
    public void contextMenuItemEditSelected(ActionEvent actionEvent) {
        editCategory();
    }

    /**
     * Invoked when mouse click event is fired upon the table
     *
     * @param event
     */
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            editCategory();
        }
    }

    /**
     * Invoked when "Delete" menu item from context menu is selected
     *
     * @param actionEvent
     */
    public void contextMenuItemDeleteSelected(ActionEvent actionEvent) {
        deleteCategory();
    }

    /**
     * Invoked when any key is fired while the table has the focus
     *
     * @param event
     */
    public void keyTyped(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (DELETE == keyCode) {
            deleteCategory();
        }
    }

    public void contextMenuShowTransactions(ActionEvent actionEvent) {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        Category persistedCategory = categoryRepository.findByName(selectedItem.getName());
        TransactionFilter filter = new TransactionFilter();
        filter.setCategory(persistedCategory);
        dataTable.getScene().lookup("#tabPane").fireEvent(new ShowTransactionsEvent(filter));
    }

    /**
     * ********************** private helper methods ****************************************************************
     */

    private void initializeRepositories() {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        transactionFilterRepository = RepositoryProvider.getInstance().getTransactionFilterRepository();
    }

    private void setupCategoryTable() {
        dataTable.setItems(categoryData);
        dataTable.addEventHandler(TAB_SELECTION, event -> {
            categoryData.clear();
            List<TransactionVolume> transactionVolumes = transactionRepository.getCategoryVolumes(true);
            transactionVolumes.forEach(volume -> {
                categoryData.add(new CategoryItem(volume.getCategory(), volume.getVolume()));
            });
            dataTable.setItems(categoryData);
            dataSummaryLabel.setText("Categories: " + categoryData.size() + ", volume: " + getAbsSum(categoryData));
        });
    }

    private void initializeColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        ruleColumn.setCellValueFactory(cellData -> cellData.getValue().ruleProperty());
    }

    private void addNewCategory(ActionEvent actionEvent) {
        CategoryDialog categoryDialog = new CategoryDialog();
        Optional<Pair<Category, Boolean>> categoryResult = categoryDialog.showAndWait();
        if (categoryResult.isPresent()) {
            Category found = categoryRepository.findByName(categoryResult.get().getKey().getName());
            if (found != null) {
                Alert alert = new Alert(ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Category exists");
                alert.setContentText("Category with name " + found.getName() + " already exists. Please choose another name for the new category.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == OK) {
                    addCategoryFired(actionEvent);
                }
            } else {
                Category category = categoryResult.get().getKey();
                if (TRUE.equals(categoryResult.get().getValue())) {
                    editTransactionFilter(category);
                }
                categoryRepository.save(category);
                double sum = 0.0;
                if (category.getFilterRule() != null) {
                    Alert alert = new Alert(CONFIRMATION);
                    alert.setTitle("Apply new rule");
                    alert.setHeaderText("Apply new category rule?");
                    alert.setContentText("Should the newly created category filter rule be applied on existing transactions?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == OK) {
                        sum = applyCategoryRule(category);
                    }
                }
                categoryData.add(new CategoryItem(category, BigDecimal.valueOf(sum)));
            }
        }
    }

    private void editCategory() {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (Category.OTHER.getName().equals(selectedItem.getName())) {
                Alert a = new Alert(ERROR);
                a.setTitle("Error");
                a.setHeaderText("Edit error");
                a.setContentText("Category \"Other\" cannot be edited");
                a.showAndWait();
            } else {
                CategoryDialog categoryDialog = new CategoryDialog(selectedItem.getName());
                Optional<Pair<Category, Boolean>> categoryResult = categoryDialog.showAndWait();
                if (categoryResult.isPresent()) {
                    int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
                    Category category = categoryRepository.findByName(selectedItem.getName());
                    category.setName(categoryResult.get().getKey().getName());
                    categoryRepository.save(category);
                    BigDecimal categorySum = selectedItem.getAmount();
                    if (TRUE.equals(categoryResult.get().getValue())) {
                        if (editTransactionFilter(category)) {
                            Alert alert = new Alert(CONFIRMATION);
                            alert.setTitle("Re-apply rule");
                            alert.setHeaderText("Re-apply the category rule?");
                            alert.setContentText("Should the edited category filter rule be re-applied on existing transactions?");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == OK) {
                                List<Transaction> transactions = transactionRepository.findByCategory(category);
                                Category other = categoryRepository.findByName(Category.OTHER.getName());
                                transactions.forEach(trx -> {
                                    trx.setCategory(other);
                                    transactionRepository.save(trx);
                                });
                                categorySum = BigDecimal.valueOf(applyCategoryRule(category));
                            }
                        }
                    }
                    categoryData.set(selectedIndex, new CategoryItem(category, categorySum));
                }
            }
        }
    }

    private boolean editTransactionFilter(Category category) {
        TransactionFilterDialog filterDialog = new TransactionFilterDialog(category.getFilterRule());
        Optional<TransactionFilter> filterResult = filterDialog.showAndWait();
        if (filterResult.isPresent()) {
            TransactionFilter filter = filterResult.get();
            transactionFilterRepository.save(filter);
            category.setFilterRule(filter);
            return true;
        }
        return false;
    }

    private double applyCategoryRule(Category category) {
        List<Transaction> transactions = transactionRepository.filterAll(category.getFilterRule());
        transactions.forEach(trx -> {
            trx.setCategory(category);
            transactionRepository.save(trx);
        });
        return transactions.parallelStream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum();
    }

    private void deleteCategory() {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (Category.OTHER.getName().equals(selectedItem.getName())) {
            Alert a = new Alert(ERROR);
            a.setHeaderText("Delete error");
            a.setContentText("Category \"Other\" cannot be deleted");
            a.showAndWait();
        } else {
            Alert alert = new Alert(CONFIRMATION);
            alert.setTitle("Delete category");
            alert.setHeaderText("Confirm delete category");
            alert.setContentText(String.format("Are you sure you want to delete the selected category: %s? The transactions in this category will be moved to category \"Other\"", selectedItem.getName()));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == OK) {
                Category category = categoryRepository.findByName(selectedItem.getName());
                Category other = categoryRepository.findByName(Category.OTHER.getName());
                List<Transaction> transactions = transactionRepository.findByCategory(category);
                transactions.forEach(trx -> {
                    trx.setCategory(other);
                    transactionRepository.save(trx);
                });
                transactionFilterRepository.removeByCategory(category);
                categoryRepository.remove(category);
                categoryData.remove(dataTable.getSelectionModel().getSelectedIndex());
            }
        }
    }

    private String getAbsSum(List<CategoryItem> categoryItems) {
        double sum = categoryItems.parallelStream().flatMapToDouble(item -> DoubleStream.of(abs(item.getAmount().doubleValue()))).sum();
        return CurrencyFormat.getInstance().format(sum);
    }
}