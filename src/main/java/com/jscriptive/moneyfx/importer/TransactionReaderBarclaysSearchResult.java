package com.jscriptive.moneyfx.importer;

import com.jscriptive.moneyfx.model.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.math.MathContext.DECIMAL64;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public class TransactionReaderBarclaysSearchResult implements TransactionReader {

    private final DateTimeFormatter df = ofPattern("dd-MM-yyyy");

    @Override
    public List<Transaction> read(Bank bank, URI file) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        InputStream in = file.toURL().openStream();

        Workbook wb = new HSSFWorkbook(in);
        Sheet sheet = wb.getSheetAt(0);

        Account account = getAccount(bank, sheet);

        sheet.forEach(row -> readTransaction(account, transactions, row));

        in.close();
        return transactions;
    }

    private Account getAccount(Bank bank, Sheet sheet) {
        Row accountRow = sheet.getRow(3);
        Cell accountCell = accountRow.getCell(0);
        String accountString = accountCell.getStringCellValue();
        String[] strings = accountString.split(" ");
        String number = strings[0];
        String name = strings[0];
        return new Account(bank, number, name);
    }

    private void readTransaction(Account account, Collection<Transaction> transactions, Row row) {
        if (row.getRowNum() < 6) {
            return;
        }
        String concept = row.getCell(0).getStringCellValue();
        if (concept == null || "".equals(concept)) {
            return;
        }
        String dtOp = row.getCell(1).getStringCellValue();
        String dtVal = row.getCell(2).getStringCellValue();
        double amount = row.getCell(3).getNumericCellValue();

        Transaction trx = new Transaction(account, new Category("default"), concept,
                parse(dtOp.substring(dtOp.length() - 10), df),
                parse(dtVal.substring(dtVal.length() - 10), df),
                new BigDecimal(amount, DECIMAL64));

        transactions.add(trx);
    }

}
