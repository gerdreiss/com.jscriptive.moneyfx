package com.jscriptive.moneyfx.ui.transaction.dialog;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.model.ValueRange;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.ui.common.AccountStringConverter;
import com.jscriptive.moneyfx.ui.common.CategoryStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.jscriptive.moneyfx.model.Account.ALL_ACCOUNTS;
import static com.jscriptive.moneyfx.model.Category.ALL_CATEGORIES;
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

    private TransactionFilter filter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAccountComboBox();
        setupCategoryComboBox();
    }

    private void setupAccountComboBox() {
        List<Account> accounts = RepositoryProvider.getInstance().getAccountRepository().findAll();
        StringConverter<Account> converter = new AccountStringConverter(accounts);
        accounts.sort((a1, a2) -> converter.toString(a1).compareTo(converter.toString(a2)));
        accounts.add(0, ALL_ACCOUNTS);
        accountCombo.setConverter(converter);
        accountCombo.getItems().addAll(accounts);
        accountCombo.getSelectionModel().selectFirst();
    }

    private void setupCategoryComboBox() {
        List<Category> categories = RepositoryProvider.getInstance().getCategoryRepository().findAll();
        categories.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        categories.add(0, ALL_CATEGORIES);
        categoryCombo.setConverter(new CategoryStringConverter(categories));
        categoryCombo.getItems().addAll(categories);
        categoryCombo.getSelectionModel().selectFirst();
    }

    public TransactionFilter getTransactionFilter() {
        TransactionFilter newFilter = new TransactionFilter(
                accountCombo.getValue() == ALL_ACCOUNTS ? null : accountCombo.getValue(),
                categoryCombo.getValue() == ALL_CATEGORIES ? null : categoryCombo.getValue(),
                conceptField.getText(),
                new ValueRange<>(
                        dtOpFieldFrom.getValue(),
                        dtOpFieldTo.getValue()),
                new ValueRange<>(
                        dtValFieldFrom.getValue(),
                        dtValFieldTo.getValue()),
                new ValueRange<>(
                        isBlank(amountFieldFrom.getText()) ? null : new BigDecimal(amountFieldFrom.getText()),
                        isBlank(amountFieldTo.getText()) ? null : new BigDecimal(amountFieldTo.getText()))
        );
        if (this.filter != null) {
            newFilter.setId(this.filter.getId());
            this.filter = newFilter;
        }
        return newFilter;
    }

    public void setFilter(TransactionFilter filter) {
        this.filter = filter;
        if (this.filter != null) {
            if (this.filter.filterByAccount()) {
                this.accountCombo.getSelectionModel().select(
                        this.accountCombo.getItems().stream().filter(
                                account -> account != null && account.equals(this.filter.getAccount())
                        ).findFirst().get()
                );
            }
            if (this.filter.filterByCategory()) {
                this.categoryCombo.getSelectionModel().select(
                        this.categoryCombo.getItems().stream().filter(
                                category -> category != null && category.equals(this.filter.getCategory())
                        ).findFirst().get()
                );
            }
            if (this.filter.filterByConcept()) {
                this.conceptField.setText(this.filter.getConcept());
            }
            if (this.filter.filterByDtOp()) {
                if (this.filter.getDtOpRange().hasFrom()) {
                    this.dtOpFieldFrom.setValue(this.filter.getDtOpRange().from());
                }
                if (this.filter.getDtOpRange().hasTo()) {
                    this.dtOpFieldTo.setValue(this.filter.getDtOpRange().to());
                }
            }
            if (this.filter.filterByDtVal()) {
                if (this.filter.getDtValRange().hasFrom()) {
                    this.dtValFieldFrom.setValue(this.filter.getDtValRange().from());
                }
                if (this.filter.getDtValRange().hasTo()) {
                    this.dtValFieldTo.setValue(this.filter.getDtValRange().to());
                }
            }
            if (this.filter.filterByAmount()) {
                if (this.filter.getAmountRange().hasFrom()) {
                    this.amountFieldFrom.setText(this.filter.getAmountRange().from().toString());
                }
                if (this.filter.getAmountRange().hasTo()) {
                    this.amountFieldTo.setText(this.filter.getAmountRange().to().toString());
                }
            }
        }
    }
}
