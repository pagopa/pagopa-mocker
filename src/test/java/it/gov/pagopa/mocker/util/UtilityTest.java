package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.MockerApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MockerApplication.class)
class UtilityTest {

    @Test
    @SneakyThrows
    void testGenerateID() {
        String url = "/Creditor-Institutions/7777\\7777777/some__detail";
        String httpMethod = "GET";
        byte[] requestIdBytes = httpMethod.concat(Constants.WHITESPACE).concat(url).getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        assertEquals(new String(md.digest(requestIdBytes)), Utility.generateHash(url, httpMethod));
    }

    @Test
    @SneakyThrows
    void testIsNullOrEmpty() {
        assertTrue(Utility.isNullOrEmpty(null));
        assertTrue(Utility.isNullOrEmpty(""));
        assertFalse(Utility.isNullOrEmpty("something"));
        assertFalse(Utility.isNullOrEmpty(" "));
        assertFalse(Utility.isNullOrEmpty("     "));
    }

    @Test
    @SneakyThrows
    void testDecodeBase64() {
        String base64 = "c29tZSBlbmNvZGVkIHN0cmluZw==";
        String plainContent = "some encoded string";

        assertNull(Utility.decodeBase64(null));
        assertEquals(plainContent, Utility.decodeBase64(base64));
        assertThrows(IllegalArgumentException.class, () -> Utility.decodeBase64(plainContent));
    }
}
