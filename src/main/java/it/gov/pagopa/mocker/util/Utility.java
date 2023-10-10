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
                String element = it.next();
                builder.append(element);
                if (it.hasNext() && !Constants.EMPTY_STRING.equals(element)) {
                    builder.append(Constants.WHITESPACE);
                }
            }
            byte[] requestIdBytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestByteArray = md.digest(requestIdBytes);

            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : digestByteArray) {
                if ((0xff & b) < 0x10) {
                    hashStringBuilder.append('0');
                }
                hashStringBuilder.append(Integer.toHexString(0xff & b));
            }
            hashedContent = hashStringBuilder.toString();
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
