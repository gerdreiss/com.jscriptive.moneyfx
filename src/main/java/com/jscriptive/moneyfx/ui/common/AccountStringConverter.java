package com.jscriptive.moneyfx.ui.common;

import com.jscriptive.moneyfx.model.Account;
import javafx.util.StringConverter;

import java.util.List;

import static com.jscriptive.moneyfx.model.Account.PREFIX_LAST_DIGITS;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Created by jscriptive.com on 20/11/2014.
 */
public class AccountStringConverter extends StringConverter<Account> {
    private final List<Account> accounts;

    public AccountStringConverter(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString(Account object) {
        if (object == null) {
            return "All accounts";
        }
        return object.getBank().getName() + object.getLastFourDigits();
    }

    @Override
    public Account fromString(String string) {
        if ("All accounts".equals(string)) {
            return null;
        }
        return accounts.stream().filter(account ->
                isAccountWithBankAndFourDigits(string, account)).findFirst().get();
    }

    private boolean isAccountWithBankAndFourDigits(String string, Account account) {
        String bankName = substringBefore(string, PREFIX_LAST_DIGITS);
        String lastFourDigits = substringAfter(string, PREFIX_LAST_DIGITS);
        return account.isOfBank(bankName) && account.numberEndsWith(lastFourDigits);
    }
}
