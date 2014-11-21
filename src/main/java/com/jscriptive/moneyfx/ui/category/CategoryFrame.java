/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.category;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.category.item.CategoryItem;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        setupCategoryTable();
        initializeColumns();
    }

    private void setupCategoryTable() {
        dataTable.setItems(categoryData);
        dataTable.addEventHandler(TabSelectionEvent.TAB_SELECTION, event -> loadCategoryData());
    }

    private void loadCategoryData() {
        categoryData.clear();
        categoryRepository.findAll().forEach(category ->
                categoryData.add(new CategoryItem(
                        category.getName(),
                        0.0
                )));
    }

    private void initializeColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    public void addCategoryFired(ActionEvent actionEvent) {
        editCategory(-1, null);
    }

    public void contextMenuItemEditSelected(ActionEvent actionEvent) {
        int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        editCategory(selectedIndex, selectedItem);
    }

    private void editCategory(int selectedIndex, CategoryItem selectedItem) {
        TextInputDialog dialog = new TextInputDialog("Category");
        dialog.setTitle("Category");
        dialog.setHeaderText("Enter the category name");
        dialog.setContentText("Name:");
        dialog.getDialogPane().getStyleClass().remove("text-input-dialog");
        dialog.getEditor().setPrefWidth(300.0);
        if (selectedItem != null) {
            dialog.getEditor().setText(selectedItem.getName());
        }
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("com/jscriptive/moneyfx/ui/images/Category-48.png"));

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            persistCategory(result.get());
            if (selectedItem == null) {
                categoryData.add(new CategoryItem(result.get(), 0.0));
            } else {
                categoryData.set(selectedIndex, new CategoryItem(result.get(), 0.0));
            }
        }
    }

    private void persistCategory(String name) {
        Category found = categoryRepository.findByName(name);
        if (found == null) {
            categoryRepository.insert(new Category(name));
        }
    }

    public void contextMenuItemDeleteSelected(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete category");
        alert.setHeaderText("Confirm delete category");
        CategoryItem selectedItem = dataTable.getSelectionModel().getSelectedItem();
        alert.setContentText(String.format("Are you sure you want to delete the selected category: %s?", selectedItem.getName()));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
            Category category = new Category(selectedItem.getName());
            List<Transaction> transactions = transactionRepository.findByAccountAndCategory(null, category);
            transactions.forEach(trx -> {
                trx.setCategory(category);
                transactionRepository.update(trx);
            });
            categoryRepository.remove(category);
            categoryData.remove(selectedIndex);
        }
    }
}