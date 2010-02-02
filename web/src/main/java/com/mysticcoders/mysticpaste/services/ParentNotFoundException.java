package com.mysticcoders.mysticpaste.services;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public class ParentNotFoundException extends Exception {
    public ParentNotFoundException() {
    }

    public ParentNotFoundException(String s) {
        super(s);
    }

    public ParentNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ParentNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
