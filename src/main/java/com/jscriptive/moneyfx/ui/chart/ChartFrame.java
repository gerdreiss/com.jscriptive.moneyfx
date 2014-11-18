/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.chart;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class ChartFrame implements Initializable {

    @FXML
    private BorderPane chartFrame;

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
    }

    public void dayByDayToggled(ActionEvent actionEvent) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Balance in Euro");

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        chartFrame.setCenter(lineChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {

            lineChart.setTitle("Balance development day by day");
            xAxis.setLabel("Day of year");

            Map<Account, List<Transaction>> transactionMap = new HashMap<>();
            accountRepository.findAll().forEach(account -> transactionMap.put(account, transactionRepository.findByAccountAndYear(account, 2014)));

            transactionMap.entrySet().forEach(entry -> {
                Account account = entry.getKey();
                List<Transaction> transactionList = entry.getValue();

                XYChart.Series series = new XYChart.Series();
                series.setName(String.format("%s %s[%s]", account.getBank().getName(), account.getLastFourDigits(), account.getFormattedBalance()));

                account.calculateStartingBalance(transactionList);
                series.getData().add(new XYChart.Data(0, account.getBalance()));

                transactionList.sort((t1, t2) -> t1.getDtOp().compareTo(t2.getDtOp()));
                transactionList.forEach(trx -> {
                            account.calculateCurrentBalance(trx);
                            series.getData().add(new XYChart.Data(account.getBalanceDate().getDayOfYear(), account.getBalance()));
                        }
                );

                lineChart.getData().add(series);
                lineChart.setCreateSymbols(false);
            });
        }
    }
}
