package com.jscriptive.moneyfx.importer;

import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.Transaction;

import java.net.URI;
import java.util.List;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public interface TransactionReader {

    List<Transaction> read(Bank bank, URI file) throws Exception;

}
