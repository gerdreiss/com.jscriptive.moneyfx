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

    private ObservableList<CategoryItem> categoryData = FXCollections.observableArrayList();

    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private TransactionFilterRepository transactionFilterRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        transactionFilterRepository = RepositoryProvider.getInstance().getTransactionFilterRepository();
        setupCategoryTable();
        initializeColumns();
    }

    private void setupCategoryTable() {
        dataTable.setItems(categoryData);
        dataTable.addEventHandler(TabSelectionEvent.TAB_SELECTION, event -> loadCategoryData());
    }

    private void loadCategoryData() {
        categoryData.clear();
        categoryRepository.findAll().forEach(category -> categoryData.add(new CategoryItem(category.getName(), 0.0)));
    }

    private void initializeColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    public void addCategoryFired(ActionEvent actionEvent) {
        editCategory(-1, null);
    }

    public void contextMenuItemEditSelected(ActionEvent actionEvent) {
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (Category.OTHER.getName().equals(selectedItem.getName())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Edit error");
            a.setContentText("Category Other cannot be edited");
            a.showAndWait();
        } else {
            int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
            editCategory(selectedIndex, selectedItem);
        }
    }

    public void contextMenuItemDeleteSelected(ActionEvent actionEvent) {
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
            if (result.get() == ButtonType.OK) {
                int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
                Category category = new Category(selectedItem.getName());
                List<Transaction> transactions = transactionRepository.findByAccountAndCategory(null, category);
                transactions.forEach(trx -> {
                    trx.setCategory(Category.OTHER);
                    transactionRepository.update(trx);
                });
                categoryRepository.remove(category);
                categoryData.remove(selectedIndex);
            }
        }
    }

    private void editCategory(int selectedIndex, CategoryItem selectedItem) {
        CategoryDialog categoryDialog = new CategoryDialog(selectedItem == null ? null : selectedItem.getName());
        Optional<Pair<String, Boolean>> categoryResult = categoryDialog.showAndWait();
        if (categoryResult.isPresent()) {
            Category found = categoryRepository.findByName(categoryResult.get().getKey());
            TransactionFilter filter = found == null ? null : found.getFilterRule();
            if (Boolean.TRUE.equals(categoryResult.get().getValue())) {
                TransactionFilterDialog filterDialog = new TransactionFilterDialog(filter);
                Optional<TransactionFilter> filterResult = filterDialog.showAndWait();
                if (filterResult.isPresent()) {
                    filter = filterResult.get();
                    persistFilterRule(filter);
                }
            }
            persistCategory(found == null ? new Category(categoryResult.get().getKey(), filter) : found);
            if (selectedItem == null) {
                categoryData.add(new CategoryItem(categoryResult.get().getKey(), 0.0));
            } else {
                categoryData.set(selectedIndex, new CategoryItem(categoryResult.get().getKey(), 0.0));
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

}