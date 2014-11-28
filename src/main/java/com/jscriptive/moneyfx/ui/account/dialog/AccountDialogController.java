package com.jscriptive.moneyfx.ui.account.dialog;

import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.BankRepository;
import com.jscriptive.moneyfx.repository.RepositoryProvider;
import com.jscriptive.moneyfx.ui.account.item.AccountItem;
import com.jscriptive.moneyfx.ui.common.LocalDateStringConverter;
import com.jscriptive.moneyfx.util.CurrencyFormat;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMAT;
import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class AccountDialogController implements Initializable {

    @FXML
    private TextField bankField;
    @FXML
    private ComboBox<String> countryCombo;
    @FXML
    private TextField numberField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField balanceField;
    @FXML
    private DatePicker balanceDateField;

    private Node nodeToObserve;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BankRepository bankRepository = RepositoryProvider.getInstance().getBankRepository();
        TextFields.bindAutoCompletion(bankField, bankRepository.findAll().stream().map(Bank::getName).collect(Collectors.toList()));

        countryCombo.setItems(FXCollections.observableArrayList(Locale.getISOCountries()));
        countryCombo.getSelectionModel().select("ES" /* TODO Locale.getDefault().getCountry() */);

        balanceDateField.setConverter(new LocalDateStringConverter());
        balanceDateField.setPromptText(DATE_FORMAT.toLowerCase());
    }

    public void valueChanged(Event event) {
        if (nodeToObserve != null) {
            nodeToObserve.setDisable(anyInvalidFieldValues());
        }
    }

    private boolean anyInvalidFieldValues() {
        return StringUtils.isAnyBlank(
                countryCombo.getValue(),
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceField.getText()) || !CurrencyFormat.getInstance().isValid(balanceField.getText());
    }

    public AccountItem getAccount() {
        return new AccountItem(
                countryCombo.getValue(),
                bankField.getText(),
                numberField.getText(),
                nameField.getText(),
                typeField.getText(),
                balanceDateField.getValue(),
                CurrencyFormat.getInstance().parse(balanceField.getText())
        );
    }

    public void setDisabledNodeToObserve(Node nodeToObserve) {
        this.nodeToObserve = nodeToObserve;
    }

    public void setAccount(AccountItem account) {
        if (account != null) {
            if (account.getBank() != null) {
                bankField.setText(account.getBank());
                bankField.setEditable(false);
            }
            if (account.getCountry() != null) {
                countryCombo.getSelectionModel().select(account.getCountry());
                countryCombo.setDisable(true);
            }
            if (account.getNumber() != null) {
                numberField.setText(account.getNumber());
                numberField.setEditable(false);
            }
            if (account.getName() != null) {
                nameField.setText(account.getName());
            }
            if (account.getType() != null) {
                typeField.setText(account.getType());
            }
            if (account.getFormattedBalance() != null) {
                balanceField.setText(account.getFormattedBalance());
            }
            if (account.getBalanceDate() != null) {
                balanceDateField.setValue(LocalDate.parse(account.getBalanceDate(), DATE_FORMATTER));
            }
        } else {
            balanceDateField.setValue(LocalDate.now());
        }
    }
}
