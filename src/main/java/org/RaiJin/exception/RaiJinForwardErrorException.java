package org.RaiJin.exception;

public class RaiJinForwardErrorException extends RaiJinException{

    public RaiJinForwardErrorException(String msg) {
        super(msg);
    }

    public RaiJinForwardErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
