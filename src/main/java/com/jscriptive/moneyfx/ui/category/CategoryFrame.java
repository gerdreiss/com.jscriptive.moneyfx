/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.category;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.ui.category.item.CategoryItem;
import com.jscriptive.moneyfx.ui.event.TabSelectionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
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
        TextInputDialog dialog = new TextInputDialog("Category");
        dialog.setTitle("Category");
        dialog.setHeaderText("Enter the category name");
        dialog.setContentText("Name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            persistCategory(result.get());
            categoryData.add(new CategoryItem(result.get(), 0.0));
        }
    }

    private void persistCategory(String name) {
        Category found = categoryRepository.findByName(name);
        if (found == null) {
            categoryRepository.insert(new Category(name));
        }
    }
}
