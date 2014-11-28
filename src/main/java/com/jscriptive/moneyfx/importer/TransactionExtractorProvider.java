package com.jscriptive.moneyfx.importer;

import com.jscriptive.moneyfx.importer.barclays.BarclaysTransactionExtractor;

import java.util.HashMap;
import java.util.Map;

import static com.jscriptive.moneyfx.importer.barclays.BarclaysTransactionExtractor.BARCLAYS_BANKNAME;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class TransactionExtractorProvider {

    private static class SingletonHolder {
        public static final TransactionExtractorProvider instance = new TransactionExtractorProvider();
    }

    public static TransactionExtractorProvider getInstance() {
        return SingletonHolder.instance;
    }

    private TransactionExtractorProvider() {
    }

    private final Map<String, TransactionExtractor> extractors = new HashMap<String, TransactionExtractor>() {{
        put(BARCLAYS_BANKNAME, new BarclaysTransactionExtractor());
    }};

    public TransactionExtractor getTransactionExtractor(String bank) {
        return extractors.get(bank);
    }
}

