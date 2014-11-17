package com.jscriptive.moneyfx.importer;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Transaction;

import java.net.URI;
import java.util.List;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public interface TransactionExtractor {

    Account extractAccountData(URI file);

    List<Transaction> extractTransactionData(URI file);

}
