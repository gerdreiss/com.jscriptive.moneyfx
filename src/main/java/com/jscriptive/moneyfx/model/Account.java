package com.jscriptive.moneyfx.model;

import com.jscriptive.moneyfx.util.CurrencyFormat;
import com.jscriptive.moneyfx.util.LocalDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.jscriptive.moneyfx.util.BigDecimalUtils.isEqual;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL32;
import static org.apache.commons.lang3.StringUtils.right;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Account {

    public static final String PREFIX_LAST_DIGITS = " ***";
    public static final int NUMBER_LAST_DIGITS = 4;

    @Id
    private String id;
    @DBRef
    private Bank bank;
    @Indexed
    private String number;
    @Indexed
    private String name;
    private String type;
    private BigDecimal balance;
    private LocalDate balanceDate;

    public Account() {
    }

    public Account(Bank bank, String number, String name, String type) {
        this(bank, number, name, type, ZERO);
    }

    public Account(Bank bank, String number, String name, String type, BigDecimal balance) {
        this(bank, number, name, type, balance, LocalDate.now());
    }

    public Account(Bank bank, String number, String name, String type, BigDecimal balance, LocalDate balanceDate) {
        setBank(bank);
        setNumber(number);
        setName(name);
        setType(type);
        setBalance(balance);
        setBalanceDate(balanceDate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bank getBank() {
        return bank;
    }

    private void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getNumber() {
        return number;
    }

    public String getLastFourDigits() {
        return PREFIX_LAST_DIGITS + right(number, NUMBER_LAST_DIGITS);
    }

    private void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public String getFormattedBalanceDate() {
        return getBalanceDate().format(LocalDateUtils.DATE_FORMATTER);
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getFormattedBalance() {
        return CurrencyFormat.getInstance().format(getBalance().doubleValue());
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void updateBalance(List<Transaction> transactions) {
        transactions.forEach(trx -> addAmount(trx.getDtOp(), trx.getAmount()));
    }

    private void addAmount(LocalDate dtOp, BigDecimal amount) {
        if (dtOp.isAfter(getBalanceDate())) {
            setBalance(getBalance().add(amount));
            setBalanceDate(dtOp);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account that = (Account) o;

        if (balanceDate != null ? !balanceDate.equals(that.balanceDate) : that.balanceDate != null) return false;
        if (bank != null ? !bank.equals(that.bank) : that.bank != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return isEqual(this.balance, that.balance);
    }

    @Override
    public int hashCode() {
        int result = bank != null ? bank.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (balanceDate != null ? balanceDate.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return String.format("Account{bank=%s, number='%s', name='%s', type='%s', balance=%s, balanceDate=%s}", getBank(), getNumber(), getName(), getType(), getFormattedBalance(), getFormattedBalanceDate());
    }

    public BigDecimal calculateStartingBalance(List<Transaction> read) {
        // calculate the balance of the last transaction of the list "read"
        read.forEach(trx -> {
            // if the transaction date is before or the same as the account balance date - subtract the amount from the account balance
            if (!trx.getDtOp().isAfter(getBalanceDate())) {
                setBalance(getBalance().subtract(trx.getAmount(), DECIMAL32));
                setBalanceDate(trx.getDtOp());
            }
        });
        return getBalance();
    }

    public BigDecimal calculateCurrentBalance(Transaction trx) {
        if (!trx.getDtOp().isBefore(getBalanceDate())) {
            setBalance(getBalance().add(trx.getAmount(), DECIMAL32));
        } else {
            setBalance(getBalance().subtract(trx.getAmount(), DECIMAL32));
        }
        setBalanceDate(trx.getDtOp());
        return getBalance();
    }

    public boolean isOfBank(String name) {
        return getBank() != null && StringUtils.isNotBlank(name) && name.equals(getBank().getName());
    }

    public boolean numberEndsWith(String lastFourDigits) {
        return StringUtils.isNotBlank(lastFourDigits) && StringUtils.isNotBlank(getNumber()) && getNumber().endsWith(lastFourDigits);
    }
}
