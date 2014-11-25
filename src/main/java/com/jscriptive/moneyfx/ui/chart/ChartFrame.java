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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import static java.lang.String.format;
import static java.time.LocalDate.now;

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
    @FXML

    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryRepository = RepositoryProvider.getInstance().getCategoryRepository();
        transactionRepository = RepositoryProvider.getInstance().getTransactionRepository();
        chartFrame.addEventHandler(TAB_SELECTION, event -> setupAccountComboBox());
    }


    private void setupAccountComboBox() {
        List<Account> accounts = new ArrayList<>(RepositoryProvider.getInstance().getAccountRepository().findAll());
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

        final LineChart<LocalDate, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);

        chartFrame.setCenter(lineChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            xAxis.setLabel("Day of year");
            yAxis.setLabel("Balance in Euro");
            lineChart.setTitle("Balance development day by day");

            Account account = accountCombo.getValue();
            Transaction earliest =
                    account == null
                            ? transactionRepository.findEarliestTransaction()
                            : transactionRepository.findEarliestTransactionOfAccount(account);
            Transaction latest =
                    account == null
                            ? transactionRepository.findLatestTransaction()
                            : transactionRepository.findLatestTransactionOfAccount(account);
            xAxis.setLowerBound(earliest.getDtOp());
            xAxis.setUpperBound(latest.getDtOp());

            Service<Void> service = new Service<Void>() {

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            Map<Account, List<Transaction>> transactionMap = new HashMap<>();
                            if (account == null) {
                                accountCombo.getItems().forEach(each -> {
                                    if (each != null) {
                                        transactionMap.put(each, transactionRepository.findByAccount(each));
                                    }
                                });
                            } else {
                                transactionMap.put(account, transactionRepository.findByAccount(account));
                            }

                            transactionMap.entrySet().forEach(entry -> {
                                Account account = entry.getKey();
                                List<Transaction> transactionList = entry.getValue();

                                XYChart.Series<LocalDate, Number> series = new XYChart.Series<>();
                                series.setName(format("%s %s [%s]",
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

                                Platform.runLater(() -> lineChart.getData().add(series));
                            });

                            return null;
                        }
                    };
                }
            };
            service.start();
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
            barChart.getData().add(inSeries);

            XYChart.Series<String, Number> outSeries = new XYChart.Series<>();
            outSeries.setName("Out" + accountLabel);
            barChart.getData().add(outSeries);

            Transaction earliestTransaction =
                    account == null
                            ? transactionRepository.findEarliestTransaction()
                            : transactionRepository.findEarliestTransactionOfAccount(account);
            Transaction latestTransaction =
                    account == null
                            ? transactionRepository.findLatestTransaction()
                            : transactionRepository.findLatestTransactionOfAccount(account);
            if (earliestTransaction != null && latestTransaction != null) {
                ObservableList<String> categories = FXCollections.observableArrayList();
                for (LocalDate date = earliestTransaction.getDtOp(); date.isBefore(latestTransaction.getDtOp()); date = date.plusMonths(1)) {
                    categories.add(LocalDateUtils.getMonthLabel(date.getYear(), date.getMonthValue()));
                }
                xAxis.setCategories(categories);
                Service<Void> service = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                for (LocalDate date = earliestTransaction.getDtOp(); date.isBefore(latestTransaction.getDtOp()); date = date.plusMonths(1)) {

                                    String monthLabel = LocalDateUtils.getMonthLabel(date.getYear(), date.getMonthValue());

                                    List<Transaction> incoming =
                                            account == null
                                                    ? transactionRepository.findIncomingByYearAndMonth(date.getYear(), date.getMonthValue())
                                                    : transactionRepository.findIncomingByAccountAndYearAndMonth(account, date.getYear(), date.getMonthValue());
                                    XYChart.Data<String, Number> inData = new XYChart.Data<>(monthLabel, getSum(incoming));

                                    List<Transaction> outgoing =
                                            account == null
                                                    ? transactionRepository.findOutgoingByYearAndMonth(date.getYear(), date.getMonthValue())
                                                    : transactionRepository.findOutgoingByAccountAndYearAndMonth(account, date.getYear(), date.getMonthValue());
                                    XYChart.Data<String, Number> outData = new XYChart.Data<>(monthLabel, getSum(outgoing));

                                    Platform.runLater(() -> {
                                        inSeries.getData().add(inData);
                                        outSeries.getData().add(outData);
                                    });
                                }

                                return null;
                            }
                        };
                    }
                };
                service.start();
            }
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
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Transaction earliest = accountCombo.getValue() == null
                                    ? transactionRepository.findEarliestTransaction()
                                    : transactionRepository.findEarliestTransactionOfAccount(accountCombo.getValue());
                            Platform.runLater(() ->
                                    pieChart.setTitle(format("%s for transactions from %s until %s",
                                            pieChart.getTitle(), earliest.getDtOp(), now())));
                            categoryRepository.findAll().forEach(category -> {
                                List<Transaction> transactions =
                                        (accountCombo.getValue() == null)
                                                ? transactionRepository.findByCategory(category)
                                                : transactionRepository.findByAccountAndCategory(accountCombo.getValue(), category);
                                Platform.runLater(() -> pieChart.getData().add(new PieChart.Data(category.getName(), getSum(transactions))));
                            });
                            return null;
                        }
                    };
                }
            };
            service.start();
        }
    }


    private String getAccountLabel(Account account) {
        String accountLabel;
        if (account == null) {
            double balance = accountCombo.getItems().stream().flatMapToDouble(a -> DoubleStream.of(a == null ? 0.0 : a.getBalance().doubleValue())).sum();
            accountLabel = format(" All accounts [%s]", CurrencyFormat.getInstance().format(balance));
        } else {
            accountLabel = format(" %s %s [%s]",
                    account.getBank().getName(),
                    account.getLastFourDigits(),
                    account.getFormattedBalance());
        }
        return accountLabel;
    }


    private double getSum(List<Transaction> transactions) {
        return abs(transactions.parallelStream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum());
    }
}
