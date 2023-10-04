package it.gov.pagopa.mocker.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
public class Utility {

    private Utility() {}

    public static String generateHash(String... content) {
        String hashedContent = "";
        try {
            StringBuilder builder = new StringBuilder();
            Iterator<String> it = Arrays.stream(content).iterator();
            while (it.hasNext()) {
                builder.append(it.next());
                if (it.hasNext()) {
                    builder.append(Constants.WHITESPACE);
                }
            }
            byte[] requestIdBytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            hashedContent = new String(md.digest(requestIdBytes));
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while generating the hash value from objects. No valid algorithm found as 'MD5'.", e);
        }
        return hashedContent;
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
