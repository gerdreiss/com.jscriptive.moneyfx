package com.jscriptive.moneyfx.ui.common;

import com.jscriptive.moneyfx.model.Account;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.jscriptive.moneyfx.model.Account.PREFIX_LAST_DIGITS;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.trim;

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
        return object.toPresentableString();
    }

    @Override
    public Account fromString(String string) {
        if ("All accounts".equals(string)) {
            return null;
        }
        return accounts.stream().filter(account ->
                isAccountWithBankAndCountryAndFourDigits(string, account)).findFirst().get();
    }

    private boolean isAccountWithBankAndCountryAndFourDigits(String string, Account account) {
        String bankCountry = trim(substringBefore(string, PREFIX_LAST_DIGITS));
        String bank = StringUtils.left(bankCountry, bankCountry.length() - 3);
        String country = StringUtils.right(bankCountry, 2);
        String lastFourDigits = substringAfter(string, PREFIX_LAST_DIGITS);
        return account.isOfBank(bank) && account.isOfCountry(country) && account.numberEndsWith(lastFourDigits);
    }
}
