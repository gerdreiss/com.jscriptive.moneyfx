/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui.chart;

import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import com.jscriptive.moneyfx.ui.common.AccountStringConverter;
import com.jscriptive.moneyfx.ui.event.ShowTransactionsEvent;
import com.jscriptive.moneyfx.util.CurrencyFormat;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.DoubleStream;

import static com.jscriptive.moneyfx.model.Account.ALL_ACCOUNTS;
import static com.jscriptive.moneyfx.ui.event.TabSelectionEvent.TAB_SELECTION;
import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;
import static com.jscriptive.moneyfx.util.LocalDateUtils.getMonthLabel;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.time.LocalDate.of;
import static java.time.LocalDate.ofYearDay;
import static javafx.geometry.Side.LEFT;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

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
        accounts.add(0, ALL_ACCOUNTS);
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
            ValueRange<LocalDate> period =
                    (account == ALL_ACCOUNTS)
                            ? transactionRepository.getTransactionOpDateRange()
                            : transactionRepository.getTransactionOpDateRangeForAccount(account);
            if (period.isEmpty()) {
                return;
            }
            xAxis.setLowerBound(period.from());
            xAxis.setUpperBound(period.to());

            Service<Void> service = new Service<Void>() {

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            Map<Account, List<Transaction>> transactionMap = new HashMap<>();
                            if (account == ALL_ACCOUNTS) {
                                accountCombo.getItems().forEach(each -> {
                                    if (each != ALL_ACCOUNTS) {
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
                                series.setName(format("%s [%s]",
                                        account.toPresentableString(),
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

            ValueRange<LocalDate> period =
                    account == ALL_ACCOUNTS
                            ? transactionRepository.getTransactionOpDateRange()
                            : transactionRepository.getTransactionOpDateRangeForAccount(account);
            if (period.isEmpty()) {
                return;
            }
            ObservableList<String> categories = FXCollections.observableArrayList();
            for (LocalDate date = period.from(); date.isBefore(period.to()); date = date.plusMonths(1)) {
                categories.add(getMonthLabel(date.getYear(), date.getMonthValue()));
            }
            xAxis.setCategories(categories);
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {

                            List<TransactionVolume> incomingVolumes =
                                    (account == ALL_ACCOUNTS)
                                            ? transactionRepository.getMonthlyIncomingVolumes(false)
                                            : transactionRepository.getMonthlyIncomingVolumesOfAccount(account, false);
                            for (TransactionVolume volume : incomingVolumes) {
                                String monthLabel = getMonthLabel(volume.getYear(), volume.getMonth());
                                XYChart.Data<String, Number> data = new XYChart.Data<>(monthLabel, volume.getVolume());
                                Platform.runLater(() -> {
                                    inSeries.getData().add(data);
                                    data.getNode().addEventHandler(MOUSE_CLICKED, event -> handleMonthlyInOutChartMouseClickEvent(
                                            (account == ALL_ACCOUNTS) ? null : account, of(volume.getYear(), volume.getMonth(), 1), event));
                                });
                            }

                            List<TransactionVolume> outgoingVolumes =
                                    (account == ALL_ACCOUNTS)
                                            ? transactionRepository.getMonthlyOutgoingVolumes(false)
                                            : transactionRepository.getMonthlyOutgoingVolumesOfAccount(account, false);
                            for (TransactionVolume volume : outgoingVolumes) {
                                String monthLabel = getMonthLabel(volume.getYear(), volume.getMonth());
                                XYChart.Data<String, Number> data = new XYChart.Data<>(monthLabel, volume.getVolume().abs());
                                Platform.runLater(() -> {
                                    outSeries.getData().add(data);
                                    data.getNode().addEventHandler(MOUSE_CLICKED, event -> handleMonthlyInOutChartMouseClickEvent((account == ALL_ACCOUNTS) ? null : account, of(volume.getYear(), volume.getMonth(), 1), event));
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

    public void yearlyInOutToggled(ActionEvent actionEvent) {
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("In/Out in Euro");
        xAxis.setLabel("Year");

        final BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Yearly in/out");

        chartFrame.setCenter(barChart);

        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            Account account = accountCombo.getValue();
            String accountLabel = getAccountLabel(account);

            XYChart.Series<Number, String> inSeries = new XYChart.Series<>();
            inSeries.setName("In" + accountLabel);
            barChart.getData().add(inSeries);

            XYChart.Series<Number, String> outSeries = new XYChart.Series<>();
            outSeries.setName("Out" + accountLabel);
            barChart.getData().add(outSeries);

            ValueRange<LocalDate> period =
                    (account == ALL_ACCOUNTS)
                            ? transactionRepository.getTransactionOpDateRange()
                            : transactionRepository.getTransactionOpDateRangeForAccount(account);
            if (period.isEmpty()) {
                return;
            }
            ObservableList<String> categories = FXCollections.observableArrayList();
            for (int y = period.from().getYear(); y < period.to().getYear() + 6; y++) {
                categories.add(String.valueOf(y));
            }
            yAxis.setCategories(categories);
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {

                            List<TransactionVolume> incomingVolumes =
                                    (account == ALL_ACCOUNTS)
                                            ? transactionRepository.getYearlyIncomingVolumes(false)
                                            : transactionRepository.getYearlyIncomingVolumesOfAccount(account, false);
                            for (TransactionVolume volume : incomingVolumes) {
                                XYChart.Data<Number, String> inData = new XYChart.Data<>(volume.getVolume(), String.valueOf(volume.getYear()));
                                Platform.runLater(() -> {
                                    inSeries.getData().add(inData);
                                    inData.getNode().addEventHandler(MOUSE_CLICKED, event -> handleYearlyInOutChartMouseClickEvent(
                                            (account == ALL_ACCOUNTS) ? null : account, ofYearDay(volume.getYear(), 1), event));
                                });
                            }

                            List<TransactionVolume> outgoingVolumes =
                                    (account == ALL_ACCOUNTS)
                                            ? transactionRepository.getYearlyOutgoingVolumes(false)
                                            : transactionRepository.getYearlyOutgoingVolumesOfAccount(account, false);
                            for (TransactionVolume volume : outgoingVolumes) {
                                XYChart.Data<Number, String> outData = new XYChart.Data<>(volume.getVolume().abs(), String.valueOf(volume.getYear()));
                                Platform.runLater(() -> {
                                    outSeries.getData().add(outData);
                                    outData.getNode().addEventHandler(MOUSE_CLICKED, event -> handleYearlyInOutChartMouseClickEvent(
                                            (account == ALL_ACCOUNTS) ? null : account, ofYearDay(volume.getYear(), 1), event));
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

    /**
     * This method is invoked when the "by category" button has been toggled
     *
     * @param actionEvent
     */
    public void byCategoryToggled(ActionEvent actionEvent) {
        final PieChart pieChart = new PieChart();
        pieChart.setTitle("Transaction balance by categories");
        pieChart.setLegendSide(LEFT);
        chartFrame.setCenter(pieChart);
        ToggleButton toggle = (ToggleButton) actionEvent.getTarget();
        if (toggle.isSelected()) {
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            ValueRange<LocalDate> period =
                                    (accountCombo.getValue() == ALL_ACCOUNTS)
                                            ? transactionRepository.getTransactionOpDateRange()
                                            : transactionRepository.getTransactionOpDateRangeForAccount(accountCombo.getValue());
                            if (period.isEmpty()) {
                                return null;
                            }
                            Platform.runLater(() -> pieChart.setTitle(format("%s for transactions from %s until %s",
                                    pieChart.getTitle(), period.from().format(DATE_FORMATTER), period.to().format(DATE_FORMATTER))));

                            categoryRepository.findAll().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).forEach(category -> {
                                List<Transaction> transactions =
                                        (accountCombo.getValue() == ALL_ACCOUNTS)
                                                ? transactionRepository.findByCategory(category)
                                                : transactionRepository.findByAccountAndCategory(accountCombo.getValue(), category);
                                Platform.runLater(() -> {
                                    double value = getSum(transactions);
                                    String name = format("%s [%s]", category.getName(), CurrencyFormat.getInstance().format(value));
                                    PieChart.Data data = new PieChart.Data(name, value);
                                    pieChart.getData().add(data);
                                    data.getNode().addEventHandler(MOUSE_CLICKED, event -> handleCategoryChartMouseClickEvent(
                                            (accountCombo.getValue() == ALL_ACCOUNTS) ? null : accountCombo.getValue(), category, event));
                                });
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
        if (account == ALL_ACCOUNTS) {
            double balance = accountCombo.getItems().stream().flatMapToDouble(a -> DoubleStream.of(a == ALL_ACCOUNTS ? 0.0 : a.getBalance().doubleValue())).sum();
            accountLabel = format(" All accounts [%s]", CurrencyFormat.getInstance().format(balance));
        } else {
            accountLabel = format(" %s [%s]", account.toPresentableString(), account.getFormattedBalance());
        }
        return accountLabel;
    }


    private double getSum(List<Transaction> transactions) {
        return abs(transactions.parallelStream().flatMapToDouble(trx -> DoubleStream.of(trx.getAmount().doubleValue())).sum());
    }

    private void handleMonthlyInOutChartMouseClickEvent(Account account, LocalDate month, MouseEvent event) {
        if (event.getClickCount() == 2) {
            TransactionFilter filter = new TransactionFilter(
                    account, null, null,
                    new ValueRange<>(month.withDayOfMonth(1), month.plusMonths(1).withDayOfMonth(1).minusDays(1)),
                    new ValueRange<>(null, null),
                    new ValueRange<>(null, null));
            chartFrame.getScene().lookup("#tabPane").fireEvent(new ShowTransactionsEvent(filter));
        }
    }

    private void handleYearlyInOutChartMouseClickEvent(Account account, LocalDate year, MouseEvent event) {
        if (event.getClickCount() == 2) {
            TransactionFilter filter = new TransactionFilter(
                    account, null, null,
                    new ValueRange<>(year.withDayOfYear(1), year.withDayOfYear(1).plusYears(1).minusDays(1)),
                    new ValueRange<>(null, null),
                    new ValueRange<>(null, null));
            chartFrame.getScene().lookup("#tabPane").fireEvent(new ShowTransactionsEvent(filter));
        }
    }

    private void handleCategoryChartMouseClickEvent(Account account, Category category, MouseEvent event) {
        if (event.getClickCount() == 2) {
            TransactionFilter filter = new TransactionFilter(
                    account, category, null,
                    new ValueRange<>(null, null),
                    new ValueRange<>(null, null),
                    new ValueRange<>(null, null));
            chartFrame.getScene().lookup("#tabPane").fireEvent(new ShowTransactionsEvent(filter));
        }
    }

}
