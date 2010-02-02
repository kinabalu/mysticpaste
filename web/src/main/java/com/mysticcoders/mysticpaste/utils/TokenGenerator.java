package com.mysticcoders.mysticpaste.utils;

import java.util.Random;

/**
 * Random token Generator.
 * 
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public class TokenGenerator {

    private static final String VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String generateToken(int length) {
        Random rand = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(VALID_CHARS.charAt(rand.nextInt(VALID_CHARS.length())));
        }

        return builder.toString();
    }
}
