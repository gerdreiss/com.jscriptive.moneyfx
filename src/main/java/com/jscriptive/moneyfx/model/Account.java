package com.jscriptive.moneyfx.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public class Account extends Entity {

    private static String REPO_NAME = "accounts";

    private Bank bank;
    private String number;
    private String name;
    private BigDecimal balance;
    private LocalDate balanceDate;

    public Account() {
        super(REPO_NAME);
    }

    public Account(Bank bank, String number, String name) {
        this(bank, number, name, BigDecimal.ZERO);
    }

    public Account(Bank bank, String number, String name, BigDecimal balance) {
        this();
        setBank(bank);
        setNumber(number);
        setName(name);
        setBalance(balance);
        setBalanceDate(LocalDate.now());
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Account{bank=%s, number='%s', name='%s', balanceDate=%s, balance=%s}",
                bank, number, name, balanceDate, balance);
    }

}
