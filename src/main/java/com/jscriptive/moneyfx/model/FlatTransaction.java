package com.jscriptive.moneyfx.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.jscriptive.moneyfx.util.BigDecimalUtils.isEqual;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.replaceChars;

/**
 * Created by jscriptive.com on 07/12/14.
 */
public class FlatTransaction {

    public static final String BANK = "Bank";
    public static final String COUNTRY = "Country";
    public static final String ACCOUNT_NUMBER = "Account number";
    public static final String CONCEPT = "Concept";
    public static final String OP_DATE = "Op date";
    public static final String VAL_DATE = "Value date";
    public static final String AMOUNT = "Amount";
    public static final String CURRENCY = "Currency";
    public static final String TRANSFER = "Transfer";
    public static final String[] FIELD_NAMES = {BANK, COUNTRY, ACCOUNT_NUMBER, CONCEPT, OP_DATE, VAL_DATE, AMOUNT, CURRENCY, TRANSFER};

    private String bankName;
    private String bankCountry;
    private String accountNumber;
    private String category;
    private String concept;
    private LocalDate dtOp;
    private LocalDate dtVal;
    private BigDecimal amount;
    private String currency;
    private Boolean isTransfer;

    FlatTransaction() {
    }

    FlatTransaction(String bankName, String bankCountry, String accountNumber, String category, String concept, LocalDate dtOp, LocalDate dtVal, BigDecimal amount, String currency, Boolean isTransfer) {
        this.bankName = bankName;
        this.bankCountry = bankCountry;
        this.accountNumber = accountNumber;
        this.category = category;
        this.concept = concept;
        this.dtOp = dtOp;
        this.dtVal = dtVal;
        this.amount = amount;
        this.currency = currency;
        this.isTransfer = isTransfer;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCountry() {
        return bankCountry;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getConcept() {
        return concept;
    }

    public LocalDate getDtOp() {
        return dtOp;
    }

    public LocalDate getDtVal() {
        return dtVal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Boolean getIsTransfer() {
        return isTransfer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlatTransaction)) return false;

        FlatTransaction that = (FlatTransaction) o;

        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null)
            return false;
        if (bankCountry != null ? !bankCountry.equals(that.bankCountry) : that.bankCountry != null) return false;
        if (bankName != null ? !bankName.equals(that.bankName) : that.bankName != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (dtOp != null ? !dtOp.equals(that.dtOp) : that.dtOp != null) return false;
        if (dtVal != null ? !dtVal.equals(that.dtVal) : that.dtVal != null) return false;
        if (isTransfer != null ? !isTransfer.equals(that.isTransfer) : that.isTransfer != null) return false;
        return isEqual(amount, that.amount);
    }

    @Override
    public int hashCode() {
        int result = bankName != null ? bankName.hashCode() : 0;
        result = 31 * result + (bankCountry != null ? bankCountry.hashCode() : 0);
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOp != null ? dtOp.hashCode() : 0);
        result = 31 * result + (dtVal != null ? dtVal.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (isTransfer != null ? isTransfer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String _concept = replaceChars(replaceChars(replaceChars(concept, (char) 32, ' '), (char) 160, ' '), ',', ' ');
        return format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                bankName, bankCountry, accountNumber, category, _concept, dtOp, dtVal, currency, amount.toString(), isTransfer);
    }
}
