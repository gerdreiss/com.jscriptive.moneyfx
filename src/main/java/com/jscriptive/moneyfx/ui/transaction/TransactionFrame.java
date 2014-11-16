/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.transaction;

import com.jscriptive.moneyfx.ui.transaction.item.TransactionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
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


    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<TransactionItem> transactionData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        transactionTable.setItems(transactionData);
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        conceptColumn.setCellValueFactory(cellData -> cellData.getValue().conceptProperty());
        dtOpColumn.setCellValueFactory(cellData -> cellData.getValue().dtOpProperty());
        dtValColumn.setCellValueFactory(cellData -> cellData.getValue().dtValProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
    }

    public void importTransactionsFired(ActionEvent actionEvent) {

    }
}
