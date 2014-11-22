package com.jscriptive.moneyfx.importer;

import com.jscriptive.moneyfx.importer.barclays.TransactionReaderBarclaysSearchResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class TransactionExtractorProvider {

    private static TransactionExtractorProvider instance;

    public static TransactionExtractorProvider getInstance() {
        if (instance == null) {
            instance = new TransactionExtractorProvider();
        }
        return instance;
    }

    private TransactionExtractorProvider() {
    }

    private final Map<String, TransactionExtractor> extractors = new HashMap<String, TransactionExtractor>() {{
        put("Barclays", new TransactionReaderBarclaysSearchResult());
    }};


    public TransactionExtractor getTransactionExtractor(String bank) {
        return extractors.get(bank);
    }
}
