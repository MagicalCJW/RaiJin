package org.RaiJin.exception;

/**
 * RaiJin's base Exception
 */
public class RaiJinException extends RuntimeException{

    public RaiJinException(String msg) {
        super(msg);
    }

    public RaiJinException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
