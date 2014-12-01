package com.jscriptive.moneyfx.importer.barclays;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.model.Transaction;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.math.MathContext.DECIMAL32;
import static java.math.RoundingMode.HALF_UP;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public class TransactionExtractorBarclaysSpainSearchResult {


    public List<Transaction> extractTransactionData(URI file) {
        List<Transaction> transactions = new ArrayList<>();
        InputStream in = null;
        try {
            in = file.toURL().openStream();

            Workbook wb = new HSSFWorkbook(in);
            Sheet sheet = wb.getSheetAt(0);

            sheet.forEach(row -> {
                Transaction trx = readTransaction(row);
                if (trx != null) {
                    transactions.add(trx);
                }
            });

        } catch (IOException e) {
            throw new TechnicalException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return transactions;
    }

    private Transaction readTransaction(Row row) {
        if (row.getRowNum() < 5) {
            return null;
        }
        String concept = row.getCell(0).getStringCellValue();
        if (concept == null || "".equals(concept)) {
            return null;
        }
        String dtOp = row.getCell(1).getStringCellValue();
        String dtVal = row.getCell(2).getStringCellValue();
        double amount = row.getCell(3).getNumericCellValue();

        Transaction trx = new Transaction();
        trx.setConcept(concept);
        trx.setDtOp(parse(dtOp.substring(dtOp.length() - 10), ofPattern("dd-MM-yyyy")));
        trx.setDtVal(parse(dtVal.substring(dtVal.length() - 10), ofPattern("dd-MM-yyyy")));
        trx.setAmount(new BigDecimal(amount, DECIMAL32));
        return trx;
    }

}
