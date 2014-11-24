/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.chart;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.common.AccountStringConverter;
import com.jscriptive.moneyfx.util.CurrencyFormat;
import com.jscriptive.moneyfx.util.LocalDateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.DoubleStream;

import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static java.lang.Math.abs;

/**
 * @author jscriptive.com
 */
public class ChartFrame implements Initializable {

    @FXML
    private BorderPane chartFrame;

    @FXML
    private ComboBox<Account> accountCombo;

    @FXML
    private ToggleGroup chartToggleGroup;

    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;


    private List<Account> allAccounts;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        chartFrame.addEventHandler(TAB_SELECTION, event -> setupAccountComboBox());
    }

    private void setupAccountComboBox() {
        allAccounts = RepositoryProvider.getInstance().getAccountRepository().findAll();
        List<Account> accounts = new ArrayList<>(allAccounts);
        accounts.add(0, null);
        accountCombo.setConverter(new AccountStringConverter(accounts));
        accountCombo.getItems().setAll(accounts);
        accountCombo.getSelectionModel().selectFirst();
    }

    /**
     * This method is called when an account has been (un-)selected in the account combo box
     *
     * @param actionEvent
     */
    public void accountChanged(ActionEvent actionEvent) {
        ToggleButton selectedToggle = (ToggleButton) chartToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            return;
        }
        EventHandler<ActionEvent> onAction = selectedToggle.getOnAction();
        onAction.handle(new ActionEvent(selectedToggle, selectedToggle));
    }

    /**
     * This method is invoked when the daily balance button has been toggled
     *
     * @param actionEvent
     */
    public void dailyBalanceToggled(ActionEvent actionEvent) {
        LocalDateAxis xAxis = new LocalDateAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day of year");
        yAxis.setLabel("Balance in Euro");

        final LineChart<LocalDate, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Balance development day by day");

        chartFrame.setCenter(lineChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            Map<Account, List<Transaction>> transactionMap = mapTransactionsToAccount();
            transactionMap.entrySet().forEach(entry -> addDataSeries(lineChart, entry));
        }
    }

    /**
     * This method is invoked when the monthly in/out button has been toggled
     *
     * @param actionEvent
     */
    public void monthlyInOutToggled(ActionEvent actionEvent) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month of Year");
        yAxis.setLabel("In/Out in Euro");

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly in/out");

        chartFrame.setCenter(barChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            Account account = accountCombo.getValue();
            String accountLabel = getAccountLabel(account);

            XYChart.Series<String, Number> inSeries = new XYChart.Series<>();
            inSeries.setName("In" + accountLabel);
            XYChart.Series<String, Number> outSeries = new XYChart.Series<>();
            outSeries.setName("Out" + accountLabel);

            addData(account, inSeries, outSeries);

            barChart.getData().add(inSeries);
            barChart.getData().add(outSeries);
        }
    }

    /**
     * This method is invoked when the "by category" button has been toggled
     *
     * @param actionEvent
     */
    public void byCategoryToggled(ActionEvent actionEvent) {
        final PieChart pieChart = new PieChart();
        pieChart.setTitle("Transaction balance by categories");

        chartFrame.setCenter(pieChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            Transaction earliest = accountCombo.getValue() == null
                    ? transactionRepository.findEarliestTransaction()
                    : transactionRepository.findEarliestTransactionOfAccount(accountCombo.getValue());
            pieChart.setTitle(String.format("%s for transactions from %s until %s", pieChart.getTitle(), earliest.getDtOp(), LocalDate.now()));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            categoryRepository.findAll().forEach(category -> {
                double sum = getSum(
                        accountCombo.getValue() == null
                                ? transactionRepository.findByCategory(category)
                                : transactionRepository.findByAccountAndCategory(accountCombo.getValue(), category)
                );
                pieChartData.add(new PieChart.Data(String.format("%s %s", category.getName(), CurrencyFormat.getInstance().format(sum)), sum));
            });
            pieChart.getData().addAll(pieChartData);
        }
    }

    private Map<Account, List<Transaction>> mapTransactionsToAccount() {
        Map<Account, List<Transaction>> transactionMap = new HashMap<>();
        if (accountCombo.getValue() == null) {
            allAccounts.forEach(account ->
                    transactionMap.put(account,
                            transactionRepository.findByAccount(account)));
        } else {
            transactionMap.put(accountCombo.getValue(),
                    transactionRepository.findByAccount(accountCombo.getValue()));
        }
        return transactionMap;
    }

    private void addDataSeries(LineChart<LocalDate, Number> lineChart, Map.Entry<Account, List<Transaction>> entry) {
        Account account = entry.getKey();
        List<Transaction> transactionList = entry.getValue();

        XYChart.Series<LocalDate, Number> series = new XYChart.Series<>();
        series.setName(String.format("%s %s [%s]",
                account.getBank().getName(),
                account.getLastFourDigits(),
                account.getFormattedBalance()));

        // sort transactions by operation value descending
        transactionList.sort((t1, t2) -> t2.getDtOp().compareTo(t1.getDtOp()));
        account.calculateStartingBalance(transactionList);
        series.getData().add(new XYChart.Data<>(
                account.getBalanceDate(),
                account.getBalance()));

        // sort transactions by operation value ascending
        transactionList.sort((t1, t2) -> t1.getDtOp().compareTo(t2.getDtOp()));
        transactionList.forEach(trx -> {
            account.calculateCurrentBalance(trx);
            series.getData().add(new XYChart.Data<>(
                    account.getBalanceDate(),
                    account.getBalance()));
        });

        lineChart.getData().add(series);
        lineChart.setCreateSymbols(false);
    }

    private String getAccountLabel(Account account) {
        String accountLabel;
        if (account == null) {
            double balance = allAccounts.stream().flatMapToDouble(a -> DoubleStream.of(a.getBalance().doubleValue())).sum();
            accountLabel = String.format(" All accounts [%s]", CurrencyFormat.getInstance().format(balance));
        } else {
            accountLabel = String.format(" %s %s [%s]",
                    account.getBank().getName(),
                    account.getLastFourDigits(),
                    account.getFormattedBalance());
        }
        return accountLabel;
    }

    private void addData(Account account, XYChart.Series<String, Number> inSeries, XYChart.Series<String, Number> outSeries) {
        Transaction earliest =
                account == null
                        ? transactionRepository.findEarliestTransaction()
                        : transactionRepository.findEarliestTransactionOfAccount(account);
        if (earliest != null) {
            LocalDate earliestTransactionDate = earliest.getDtOp();
            int currentYear = LocalDate.now().getYear();
            for (int year = earliestTransactionDate.getYear(); year <= currentYear; year++) {
                for (int month = earliestTransactionDate.getMonth().getValue(); month <= 12; month++) {

                    String monthLabel = LocalDateUtils.getMonthLabel(year, month);

                    List<Transaction> incoming =
                            account == null
                                    ? transactionRepository.findIncomingByYearAndMonth(year, month)
                                    : transactionRepository.findIncomingByAccountAndYearAndMonth(account, year, month);
                    List<Transaction> outgoing =
                            account == null
                                    ? transactionRepository.findOutgoingByYearAndMonth(year, month)
                                    : transactionRepository.findOutgoingByAccountAndYearAndMonth(account, year, month);

                    XYChart.Data<String, Number> inData = new XYChart.Data<>(monthLabel, getSum(incoming));
                    XYChart.Data<String, Number> outData = new XYChart.Data<>(monthLabel, getSum(outgoing));

                    inSeries.getData().add(inData);
                    outSeries.getData().add(outData);
                }
            }
        }
    }

    private double getSum(List<Transaction> transactions) {
        return abs(transactions.parallelStream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum());
    }
}
