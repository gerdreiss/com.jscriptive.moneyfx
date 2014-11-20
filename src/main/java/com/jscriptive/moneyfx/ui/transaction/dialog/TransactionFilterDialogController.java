package com.jscriptive.moneyfx.ui.transaction.dialog;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.repository.filter.TransactionFilter;
import com.jscriptive.moneyfx.ui.common.AccountStringConverter;
import com.jscriptive.moneyfx.ui.common.CategoryStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by jscriptive.com on 20/11/2014.
 */
public class TransactionFilterDialogController implements Initializable {

    @FXML
    private ComboBox<Account> accountCombo;
    @FXML
    private ComboBox<Category> categoryCombo;
    @FXML
    private TextField conceptField;
    @FXML
    private DatePicker dtOpFieldFrom;
    @FXML
    private DatePicker dtOpFieldTo;
    @FXML
    private DatePicker dtValFieldFrom;
    @FXML
    private DatePicker dtValFieldTo;
    @FXML
    private TextField amountFieldFrom;
    @FXML
    private TextField amountFieldTo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAccountComboBox();
        setupCategoryComboBox();
    }

    private void setupAccountComboBox() {
        List<Account> accounts = RepositoryProvider.getInstance().getAccountRepository().findAll();
        accounts.add(0, null);
        accountCombo.setConverter(new AccountStringConverter(accounts));
        accountCombo.getItems().addAll(accounts);
        accountCombo.getSelectionModel().selectFirst();
    }

    private void setupCategoryComboBox() {
        List<Category> categories = RepositoryProvider.getInstance().getCategoryRepository().findAll();
        categories.add(0, null);
        categoryCombo.setConverter(new CategoryStringConverter(categories));
        categoryCombo.getItems().addAll(categories);
        categoryCombo.getSelectionModel().selectFirst();
    }

    public TransactionFilter getTransactionFilter() {
        return new TransactionFilter(
                accountCombo.getValue(),
                categoryCombo.getValue(),
                conceptField.getText(),
                new TransactionFilter.ValueRange<>(
                        dtOpFieldFrom.getValue(),
                        dtOpFieldTo.getValue()),
                new TransactionFilter.ValueRange<>(
                        dtValFieldFrom.getValue(),
                        dtValFieldTo.getValue()),
                new TransactionFilter.ValueRange<>(
                        isBlank(amountFieldFrom.getText()) ? null : new BigDecimal(amountFieldFrom.getText()),
                        isBlank(amountFieldTo.getText()) ? null : new BigDecimal(amountFieldTo.getText()))
        );
    }
}
