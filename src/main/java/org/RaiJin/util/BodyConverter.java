package org.RaiJin.util;

import java.nio.charset.StandardCharsets;

public class BodyConverter {

    public static String convertBodyToString(byte[] body) {
        if(body == null)
            return null;
        return new String(body, StandardCharsets.UTF_8);
    }

    public static byte[] convertStringToBody(String body) {
        if(body == null)
            return null;
        return body.getBytes(StandardCharsets.UTF_8);
    }
}
