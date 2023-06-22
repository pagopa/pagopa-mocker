package it.gov.pagopa.mocker.util;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utility {

    private Utility() {}

    public static String generateID(String url, String httpMethod) {
        return url.concat(httpMethod).replaceAll("[\\\\/\\-_]+", Constants.EMPTY_STRING);
    }

    public static boolean isNullOrEmpty(String content) {
        return content == null || Constants.EMPTY_STRING.equals(content);
    }

    public static String decodeBase64(String encodedContent) {
        String decodedContent = null;
        if (encodedContent != null) {
            byte[] decoded = Base64.getDecoder().decode(encodedContent);
            decodedContent = new String(decoded, StandardCharsets.UTF_8);
        }
        return decodedContent;
    }
}
