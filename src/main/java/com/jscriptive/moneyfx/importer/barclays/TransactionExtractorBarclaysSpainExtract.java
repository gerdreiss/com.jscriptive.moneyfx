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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.math.MathContext.DECIMAL32;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;

/**
 * Created by jscriptive.com on 28/11/14.
 */
public class TransactionExtractorBarclaysSpainExtract {


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
        String dtOp = row.getCell(0).getStringCellValue();
        String dtVal = row.getCell(1).getStringCellValue();
        String concept = row.getCell(2).getStringCellValue();
        if (isAnyBlank(dtOp, dtVal, concept)) {
            return null;
        }
        double amount = row.getCell(3).getNumericCellValue();
        double accountBalance = row.getCell(4).getNumericCellValue();

        Transaction trx = new Transaction();
        trx.setConcept(concept);
        trx.setDtOp(parse(dtOp.substring(dtOp.length() - 10), ofPattern("dd-MM-yyyy")));
        trx.setDtVal(parse(dtVal.substring(dtVal.length() - 10), ofPattern("dd-MM-yyyy")));
        trx.setAmount(new BigDecimal(amount, DECIMAL32));
        trx.setAccountBalance(new BigDecimal(accountBalance, DECIMAL32));
        return trx;
    }
}
