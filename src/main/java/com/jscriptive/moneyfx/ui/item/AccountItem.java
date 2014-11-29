package com.jscriptive.moneyfx.ui.item;

import com.jscriptive.moneyfx.model.Account;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 13/11/2014.
 */
public class AccountItem implements UIItem {

    private final StringProperty country;
    private final StringProperty bank;
    private final StringProperty number;
    private final StringProperty name;
    private final StringProperty type;
    private final ObjectProperty<LocalDate> balanceDate;
    private final ObjectProperty<BigDecimal> balance;

    public AccountItem(Account account) {
        this(account.getBank() == null ? null : account.getBank().getCountryCode(),
                account.getBank() == null ? null : account.getBank().getName(),
                account.getNumber(),
                account.getName(),
                account.getType(),
                account.getBalanceDate(),
                account.getBalance());
    }

    public AccountItem(String country, String bank, String number, String name, String type, LocalDate balanceDate, BigDecimal balance) {
        this.country = new SimpleStringProperty(country);
        this.bank = new SimpleStringProperty(bank);
        this.number = new SimpleStringProperty(number);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.balanceDate = new SimpleObjectProperty<>(balanceDate);
        this.balance = new SimpleObjectProperty<>(balance);
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
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

    public LocalDate getBalanceDate() {
        return balanceDate.get();
    }

    public ObjectProperty<LocalDate> balanceDateProperty() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate.set(balanceDate);
    }

    public BigDecimal getBalance() {
        return balance.get();
    }

    public ObjectProperty<BigDecimal> balanceProperty() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance.set(balance);
    }

}
