/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.category;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionFilterRepository;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.category.dialog.CategoryDialog;
import com.jscriptive.moneyfx.ui.category.item.CategoryItem;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import com.jscriptive.moneyfx.ui.transaction.dialog.TransactionFilterDialog;
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
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class CategoryFrame implements Initializable {

    @FXML
    private TableView<CategoryItem> dataTable;
    @FXML
    private TableColumn<CategoryItem, String> nameColumn;
    @FXML
    private TableColumn<CategoryItem, Number> amountColumn;

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
        if (KeyCode.DELETE == keyCode) {
            deleteCategory();
        }
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
        dataTable.addEventHandler(TabSelectionEvent.TAB_SELECTION, event -> {
            categoryData.clear();
            categoryRepository.findAll().forEach(category -> categoryData.add(new CategoryItem(category.getName(), 0.0)));
        });
    }

    private void initializeColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    private void addNewCategory(ActionEvent actionEvent) {
        CategoryDialog categoryDialog = new CategoryDialog();
        Optional<Pair<String, Boolean>> categoryResult = categoryDialog.showAndWait();
        if (categoryResult.isPresent()) {
            Category found = categoryRepository.findByName(categoryResult.get().getKey());
            if (found != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Category exists");
                alert.setContentText("Category with name " + found.getName() + " already exists. Please choose another name for the new category.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    addCategoryFired(actionEvent);
                }
            } else {
                Category category = new Category(categoryResult.get().getKey());
                if (Boolean.TRUE.equals(categoryResult.get().getValue())) {
                    TransactionFilterDialog filterDialog = new TransactionFilterDialog();
                    Optional<TransactionFilter> filterResult = filterDialog.showAndWait();
                    if (filterResult.isPresent()) {
                        TransactionFilter filter = filterResult.get();
                        persistFilterRule(filter);
                        category.setFilterRule(filter);
                    }
                }
                persistCategory(category);
                categoryData.add(new CategoryItem(category.getName(), 0.0));
                if (category.getFilterRule() != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Apply new rule");
                    alert.setHeaderText("Apply new category rule?");
                    alert.setContentText("Should the newly created category filter rule be applied on existing transactions?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        applyCategoryRule(category);
                    }
                }
            }
        }
    }

    private void applyCategoryRule(Category category) {
        List<Transaction> transactions = transactionRepository.filterAll(category.getFilterRule());
        transactions.forEach(trx -> transactionRepository.updateCategory(trx, category));
    }

    private void editCategory() {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (Category.OTHER.getName().equals(selectedItem.getName())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Edit error");
            a.setContentText("Category \"Other\" cannot be edited");
            a.showAndWait();
        } else {
            CategoryDialog categoryDialog = new CategoryDialog(selectedItem.getName());
            Optional<Pair<String, Boolean>> categoryResult = categoryDialog.showAndWait();
            if (categoryResult.isPresent()) {
                int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
                Category category = categoryRepository.findByName(selectedItem.getName());
                category.setName(categoryResult.get().getKey());
                persistCategory(category);
                categoryData.set(selectedIndex, new CategoryItem(category.getName(), 0.0));
            }
        }
    }

    private void persistFilterRule(TransactionFilter filter) {
        if (filter.getId() == null) {
            transactionFilterRepository.insert(filter);
        } else {
            transactionFilterRepository.update(filter);
        }
    }

    private void persistCategory(Category category) {
        if (category.getId() == null) {
            categoryRepository.insert(category);
        } else {
            categoryRepository.update(category);
        }
    }
    private void deleteCategory() {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (Category.OTHER.getName().equals(selectedItem.getName())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Delete error");
            a.setContentText("Category \"Other\" cannot be deleted");
            a.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete category");
            alert.setHeaderText("Confirm delete category");
            alert.setContentText(String.format("Are you sure you want to delete the selected category: %s? The transactions in this category will be moved to category \"Other\"", selectedItem.getName()));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
                Category category = new Category(selectedItem.getName());
                List<Transaction> transactions = transactionRepository.findByCategory(category);
                Category other = categoryRepository.findByName(Category.OTHER.getName());
                transactions.forEach(trx -> transactionRepository.updateCategory(trx, other));
                transactionFilterRepository.removeByCategory(category);
                categoryRepository.remove(category);
                categoryData.remove(selectedIndex);
            }
        }
    }

}