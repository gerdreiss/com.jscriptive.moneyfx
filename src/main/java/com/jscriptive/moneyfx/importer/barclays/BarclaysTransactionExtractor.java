package com.jscriptive.moneyfx.importer.barclays;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.importer.TransactionExtractor;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.model.Country;
import com.jscriptive.moneyfx.model.Transaction;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * Created by jscriptive.com on 28/11/14.
 */
public class BarclaysTransactionExtractor implements TransactionExtractor {

    public static String BARCLAYS_BANKNAME = "Barclays";
    public static String SPANISH_LANGCODE = "es";
    public static String SPAIN_COUNTRYCODE = "ES";

    public static Bank BANK_BARCLAYS_SPAIN = new Bank(BARCLAYS_BANKNAME, new Country(new Locale(SPANISH_LANGCODE, SPAIN_COUNTRYCODE)));

    private TransactionExtractorBarclaysSpainExtract fromExtract;
    private TransactionExtractorBarclaysSpainSearchResult fromSearchResult;

    public BarclaysTransactionExtractor() {
        fromExtract = new TransactionExtractorBarclaysSpainExtract();
        fromSearchResult = new TransactionExtractorBarclaysSpainSearchResult();
    }

    @Override
    public Account extractAccountData(URI file) {
        InputStream in = null;
        try {
            in = file.toURL().openStream();
            Sheet sheet = new HSSFWorkbook(in).getSheetAt(0);
            String accountString = sheet.getRow(3).getCell(0).getStringCellValue();
            if (StringUtils.isBlank(accountString)) {
                return null;
            }
            String[] strings = StringUtils.split(StringUtils.normalizeSpace(accountString), " ", 2);
            if (ArrayUtils.getLength(strings) != 2) {
                return null;
            }

            //noinspection ConstantConditions
            return new Account(BANK_BARCLAYS_SPAIN, strings[0], strings[1], null);

        } catch (IOException e) {
            throw new TechnicalException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public List<Transaction> extractTransactionData(URI file) {
        // TODO extract data using one or the other object depending whether it's Saldo y movimientos or Lista de movimientos
        return fromSearchResult.extractTransactionData(file);
    }
}
