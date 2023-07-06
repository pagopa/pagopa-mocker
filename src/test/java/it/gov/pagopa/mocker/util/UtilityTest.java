package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.MockerApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MockerApplication.class)
class UtilityTest {

    @Test
    @SneakyThrows
    void testGenerateID() {
        String url = "/Creditor-Institutions/7777\\7777777/some__detail";
        String httpMethod = "GET";
        String result = Utility.generateID(url, httpMethod);
        assertEquals("getcreditorinstitutions77777777777somedetail", result);
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
