package com.jscriptive.moneyfx.ui.account.item;

import com.jscriptive.moneyfx.util.CurrencyFormat;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;

/**
 * Created by jscriptive.com on 13/11/2014.
 */
public class AccountItem {

    private final StringProperty bank;
    private final StringProperty number;
    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty balanceDate;
    private final DoubleProperty balance;
    private final StringProperty formattedBalance;

    public AccountItem(String bank, String number, String name, String type, LocalDate balanceDate, double balance) {
        this.bank = new SimpleStringProperty(bank);
        this.number = new SimpleStringProperty(number);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.balanceDate = new SimpleStringProperty(balanceDate.format(DATE_FORMATTER));
        this.balance = new SimpleDoubleProperty(balance);
        this.formattedBalance = new SimpleStringProperty(CurrencyFormat.getInstance().format(balance));
    }

    public String getBank() {
        return bank.get();
    }

    public StringProperty bankProperty() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank.set(bank);
    }

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getBalanceDate() {
        return balanceDate.get();
    }

    public StringProperty balanceDateProperty() {
        return balanceDate;
    }

    public void setBalanceDate(String balanceDate) {
        this.balanceDate.set(balanceDate);
    }

    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public String getFormattedBalance() {
        return formattedBalance.get();
    }

    public StringProperty formattedBalanceProperty() {
        return formattedBalance;
    }

    public void setFormattedBalance(String formattedBalance) {
        this.formattedBalance.set(formattedBalance);
    }
}
