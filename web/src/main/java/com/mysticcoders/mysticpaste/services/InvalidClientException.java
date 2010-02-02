package com.mysticcoders.mysticpaste.services;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public class InvalidClientException extends Exception {
    public InvalidClientException() {
    }

    public InvalidClientException(String s) {
        super(s);
    }

    public InvalidClientException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidClientException(Throwable throwable) {
        super(throwable);
    }
}
