package com.jscriptive.moneyfx.repository;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class RepositoryProvider {
    private static RepositoryProvider instance;

    public static RepositoryProvider getInstance() {
        if (instance == null) {
            instance = new RepositoryProvider();
        }
        return instance;
    }

    private RepositoryProvider() {
    }

    private ConfigurableApplicationContext context;

    private ConfigurableApplicationContext getApplicationContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("classpath*:**/application-context.xml");
        }
        return context;
    }

    public BankRepository getBankRepository() {
        return getApplicationContext().getBean(BankRepository.class);
    }

    public AccountRepository getAccountRepository() {
        return getApplicationContext().getBean(AccountRepository.class);
    }

    public CategoryRepository getCategoryRepository() {
        return getApplicationContext().getBean(CategoryRepository.class);
    }

    public TransactionRepository getTransactionRepository() {
        return getApplicationContext().getBean(TransactionRepository.class);
    }

}
