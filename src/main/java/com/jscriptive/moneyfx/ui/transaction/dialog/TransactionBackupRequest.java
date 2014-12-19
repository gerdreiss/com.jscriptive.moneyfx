package com.jscriptive.moneyfx.ui.transaction.dialog;

import java.io.File;

/**
 * Created by jscriptive.com on 07/12/14.
 */
public class TransactionBackupRequest {
    private final File path;
    private final String format;
    private final boolean header;

    public TransactionBackupRequest(File path, String format, boolean header) {
        this.path = path;
        this.format = format;
        this.header = header;
    }

    public File getPath() {
        return path;
    }

    public String getFormat() {
        return format;
    }

    public boolean isHeader() {
        return header;
    }
}
