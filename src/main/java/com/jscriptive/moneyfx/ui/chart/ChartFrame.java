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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.DoubleStream;

import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * @author jscriptive.com
 */
public class ChartFrame implements Initializable {

    private static final int EARLIEST_TRX_YEAR = 2013;

    @FXML
    private BorderPane chartFrame;

    @FXML
    private ComboBox<Account> accountCombo;
    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private ToggleGroup chartToggleGroup;

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        accountRepository = RepositoryProvider.getInstance().getAccountRepository();
        setupAccountComboBox();
        setupYearComboBox();
    }

    private void setupAccountComboBox() {
        List<Account> accounts = accountRepository.findAll();
        accounts.add(0, null);
        accountCombo.getItems().addAll(accounts);
        accountCombo.getSelectionModel().selectFirst();
        accountCombo.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                if (object == null) {
                    return "All accounts";
                }
                return object.getBank().getName() + object.getLastFourDigits();
            }

            @Override
            public Account fromString(String string) {
                if ("All accounts".equals(string)) {
                    return null;
                }
                return accounts.stream().filter(account -> isAccountWithBankAndFourDigits(string, account)).findFirst().get();
            }

            private boolean isAccountWithBankAndFourDigits(String string, Account account) {
                return account.hasBank(substringBefore(string, " ")) && account.hasLastFourDigits(substringAfter(string, Account.FOUR_DIGIT_PREFIX));
            }
        });
    }

    private void setupYearComboBox() {
        List<Integer> years = new ArrayList<>();
        int year = LocalDate.now().getYear();
        for (int y = year; y >= EARLIEST_TRX_YEAR; y--) {
            years.add(y);
        }
        yearCombo.getItems().addAll(years);
        yearCombo.getSelectionModel().selectFirst();
    }

    public void dailyBalanceToggled(ActionEvent actionEvent) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Balance in Euro");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        chartFrame.setCenter(lineChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {

            lineChart.setTitle("Balance development day by day");
            xAxis.setLabel("Day of year");

            Map<Account, List<Transaction>> transactionMap = new HashMap<>();
            if (accountCombo.getValue() == null) {
                accountRepository.findAll().forEach(account -> transactionMap.put(account, transactionRepository.findByAccountAndYear(account, yearCombo.getValue())));
            } else {
                transactionMap.put(accountCombo.getValue(), transactionRepository.findByAccountAndYear(accountCombo.getValue(), yearCombo.getValue()));
            }

            transactionMap.entrySet().forEach(entry -> {
                Account account = entry.getKey();
                List<Transaction> transactionList = entry.getValue();

                XYChart.Series series = new XYChart.Series();
                series.setName(String.format("%s %s[%s]", account.getBank().getName(), account.getLastFourDigits(), account.getFormattedBalance()));

                account.calculateStartingBalance(transactionList);
                series.getData().add(new XYChart.Data<Number, Number>(0, account.getBalance()));

                transactionList.sort((t1, t2) -> t1.getDtOp().compareTo(t2.getDtOp()));
                transactionList.forEach(trx -> {
                            account.calculateCurrentBalance(trx);
                            series.getData().add(new XYChart.Data<Number, Number>(
                                    account.getBalanceDate().getDayOfYear(),
                                    account.getBalance()));
                        }
                );

                lineChart.getData().add(series);
                lineChart.setCreateSymbols(false);
            });
        }
    }


    public void monthlyInOutToggled(ActionEvent actionEvent) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("In/Out in Euro");

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        chartFrame.setCenter(barChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {

            barChart.setTitle("Monthly in/out");
            xAxis.setLabel("Month of year");

            int year = yearCombo.getValue();

            XYChart.Series inSeries = new XYChart.Series();
            inSeries.setName("In");
            XYChart.Series outSeries = new XYChart.Series();
            outSeries.setName("Out");

            for (int month = 1; month <= 12; month++) {

                String monthLabel = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;

                List<Transaction> incoming = transactionRepository.findIncomingByAccountAndYearAndMonth(accountCombo.getValue(), year, month);
                XYChart.Data<String, Number> inData = new XYChart.Data<>(
                        monthLabel,
                        abs(incoming.stream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum()));
                inSeries.getData().add(inData);

                List<Transaction> outgoing = transactionRepository.findOutgoingByAccountAndYearAndMonth(accountCombo.getValue(), year, month);
                XYChart.Data<String, Number> outData = new XYChart.Data(
                        monthLabel,
                        abs(outgoing.stream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum()));
                outSeries.getData().add(outData);

            }

            barChart.getData().add(inSeries);
            barChart.getData().add(outSeries);
        }
    }

    public void accountChanged(ActionEvent actionEvent) {
        comboSelectionChanged();
    }

    public void yearChanged(ActionEvent actionEvent) {
        comboSelectionChanged();
    }

    private void comboSelectionChanged() {
        ToggleButton selectedToggle = (ToggleButton) chartToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            return;
        }
        EventHandler<ActionEvent> onAction = selectedToggle.getOnAction();
        onAction.handle(new ActionEvent(selectedToggle, selectedToggle));
    }

}
