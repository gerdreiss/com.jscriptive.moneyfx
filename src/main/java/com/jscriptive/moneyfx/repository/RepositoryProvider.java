package com.jscriptive.moneyfx.repository;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
public class RepositoryProvider {
    private RepositoryProvider() {
    }

    private static class SingletonHolder {
        public static final RepositoryProvider instance = new RepositoryProvider();
    }

    public static RepositoryProvider getInstance() {
        return SingletonHolder.instance;
    }

    private ConfigurableApplicationContext context;

    private ConfigurableApplicationContext getApplicationContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("classpath*:**/application-context.xml");
        }
        return context;
    }

    public CountryRepository getCountryRepository() {
        return getApplicationContext().getBean(CountryRepository.class);
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

    public TransactionFilterRepository getTransactionFilterRepository() {
        return getApplicationContext().getBean(TransactionFilterRepository.class);
    }

    public JsonRepository getJsonRepository() {
        return getApplicationContext().getBean(JsonRepository.class);
    }

}
