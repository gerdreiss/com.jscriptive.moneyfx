package com.jscriptive.moneyfx.exception;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public class TechnicalException extends RuntimeException {

    public TechnicalException() {
        super();
    }

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalException(Throwable cause) {
        super(cause);
    }

}
