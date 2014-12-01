package com.jscriptive.moneyfx.configuration;

import com.jscriptive.moneyfx.exception.TechnicalException;
import com.jscriptive.moneyfx.model.Bank;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Igor on 01/12/2014.
 */
public class Configuration {

    private static class InstanceHolder {
        public static final Configuration instance = new Configuration();
    }

    public static Configuration getInstance() {
        return InstanceHolder.instance;
    }

    private final Properties properties;

    private Configuration() {
        this.properties = new Properties();
        InputStream input = getClass().getResourceAsStream("MoneyFX.ini");
        try {
            this.properties.load(input);
        } catch (IOException e) {
            throw new TechnicalException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public String getTransferConceptRegexFor(Bank bank) {
        return this.properties.getProperty(bank.getTransferConceptRegexConfigurationProperty());
    }
}
