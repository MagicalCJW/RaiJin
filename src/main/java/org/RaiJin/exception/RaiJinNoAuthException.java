package org.RaiJin.exception;

public class RaiJinNoAuthException extends RaiJinException{

    public RaiJinNoAuthException(String msg) {
        super(msg);
    }

    public RaiJinNoAuthException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
