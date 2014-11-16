/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.chart;

import com.jscriptive.moneyfx.importer.TransactionReader;
import com.jscriptive.moneyfx.importer.TransactionReaderBarclaysSearchResult;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.math.MathContext.DECIMAL64;

/**
 * @author jscriptive.com
 */
public class ChartFrame extends BorderPane implements Initializable {

    @FXML
    private LineChart lineChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void importFired(Bank bank, File file) {
        TransactionReader reader = new TransactionReaderBarclaysSearchResult();
        try {
            List<Transaction> read = reader.read(bank, file.toURI());

            XYChart.Series series = new XYChart.Series();
            series.setName("Money flow");

            series.getData().add(new XYChart.Data(0, calculateStartingBalance(read)));
            read.forEach(trx -> series.getData().add(new XYChart.Data(
                    trx.getDtOp().getDayOfYear(), calculateCurrentBalance(trx))));

            if (lineChart == null) {
                ObservableList<Node> children = super.getChildren();
                BorderPane bp = (BorderPane) children.get(0);
                lineChart = (LineChart) bp.getCenter();
            }
            lineChart.getData().add(series);
            lineChart.setCreateSymbols(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double calculateStartingBalance(List<Transaction> read) {
        Account account = read.get(0).getAccount();
        read.forEach(trx -> account.setBalance(account.getBalance().subtract(trx.getAmount(), DECIMAL64)));
        return account.getBalance().doubleValue();
    }

    private BigDecimal calculateCurrentBalance(Transaction trx) {
        trx.getAccount().setBalance(trx.getAccount().getBalance().add(trx.getAmount(), DECIMAL64));
        return trx.getAccount().getBalance();
    }
}
