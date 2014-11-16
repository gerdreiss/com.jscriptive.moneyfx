/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jscriptive.moneyfx.ui;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.jscriptive.moneyfx.repository.BankRepository;
import com.jscriptive.moneyfx.ui.account.AccountFrame;
import com.jscriptive.moneyfx.ui.category.CategoryFrame;
import com.jscriptive.moneyfx.ui.chart.ChartFrame;
import com.jscriptive.moneyfx.ui.transaction.TransactionFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jscriptive.com
 */
public class MainFrame extends BorderPane implements Initializable {

    @FXML
    private AccountFrame accountFrame;
    @FXML
    private TransactionFrame transactionFrame;
    @FXML
    private CategoryFrame categoryFrame;
    @FXML
    private ChartFrame chartFrame;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath*:**/application-context.xml");
        BankRepository bankRepository = context.getBean(BankRepository.class);
        AccountRepository accountRepository = context.getBean(AccountRepository.class);
        accountFrame.setBankRepository(bankRepository);
        accountFrame.setAccountRepository(accountRepository);
    }

    public void importFired(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel sheets", "*.xls"));
        File file = fileChooser.showOpenDialog(null);
        chartFrame.importFired(new Bank("Barclays"), file);
    }

    public void exitFired(ActionEvent actionEvent) {
        System.exit(0);
    }






}
