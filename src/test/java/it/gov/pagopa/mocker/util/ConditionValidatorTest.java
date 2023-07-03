package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.MockerApplication;
import it.gov.pagopa.mocker.model.enumeration.ConditionType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = MockerApplication.class)
class ConditionValidatorTest {

    @Test
    @SneakyThrows
    void testValidatorREGEX() {
        // Positive checks
        assertTrue(ConditionValidator.validate("STRING-STRING-STR", "(STRING){1,}", ConditionType.REGEX));
        assertTrue(ConditionValidator.validate("johndoe@fakemail.com", "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$", ConditionType.REGEX));
        assertTrue(ConditionValidator.validate("3.14", "^\\d*\\.\\d+$", ConditionType.REGEX));
        assertTrue(ConditionValidator.validate("2020-01-01T23:59:59Z", "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))", ConditionType.REGEX));
        // Negative checks
        assertFalse(ConditionValidator.validate("ONLY-STR", "(STRING){1,}", ConditionType.REGEX));
        assertFalse(ConditionValidator.validate("johndoe_at_fakemail_dotcom", "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$", ConditionType.REGEX));
        assertFalse(ConditionValidator.validate("314", "^\\d*\\.\\d+$", ConditionType.REGEX));
        assertFalse(ConditionValidator.validate("2020/01/01T23:59:59Z", "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))", ConditionType.REGEX));
    }

    @Test
    @SneakyThrows
    void testValidatorEQ() {
        // Positive checks
        assertTrue(ConditionValidator.validate(3, "3", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(3L, "3.0", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(new BigInteger("3"), "3.0", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.1"), "2.1", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(2.1F, "2.1", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(2.1D, "2.1", ConditionType.EQ));
        assertTrue(ConditionValidator.validate(2D, "2", ConditionType.EQ));
        assertTrue(ConditionValidator.validate("E", "E", ConditionType.EQ));
        assertTrue(ConditionValidator.validate("32", "32", ConditionType.EQ));
        // Negative checks
        assertFalse(ConditionValidator.validate(1, "2", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(1L, "2.0", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(new BigInteger("1"), "2.0", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(new BigDecimal("1.1"), "2.0", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(1.1F, "2.0", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(1.1D, "2.0", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(1D, "1.000000001", ConditionType.EQ));
        assertFalse(ConditionValidator.validate("E", "EG", ConditionType.EQ));
        assertFalse(ConditionValidator.validate(3, "G", ConditionType.EQ));
        assertFalse(ConditionValidator.validate("3.1", "31", ConditionType.EQ));
    }

    @Test
    @SneakyThrows
    void testValidatorNEQ() {
        // Positive checks
        assertTrue(ConditionValidator.validate(2, "3", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(2L, "3.0", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(new BigInteger("2"), "3.0", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.2"), "2.1", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(2.2F, "2.1", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(2.2D, "2.1", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(2D, "2.00000001", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate(2.00000001D, "2", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate("A", "E", ConditionType.NEQ));
        assertTrue(ConditionValidator.validate("31", "32", ConditionType.NEQ));
        // Negative checks
        assertFalse(ConditionValidator.validate(2, "2", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(2L, "2.0", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(new BigInteger("2"), "2.0", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(new BigDecimal("2.0"), "2.0", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(2.0F, "2.0", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(2.0D, "2.0", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate(1.000000001D, "1.000000001", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate("E", "E", ConditionType.NEQ));
        assertFalse(ConditionValidator.validate("31", "31", ConditionType.NEQ));
    }

    @Test
    @SneakyThrows
    void testValidatorGT() {
        // Positive checks
        assertTrue(ConditionValidator.validate(3, "2", ConditionType.GT));
        assertTrue(ConditionValidator.validate(3L, "2.0", ConditionType.GT));
        assertTrue(ConditionValidator.validate(new BigInteger("3"), "2.0", ConditionType.GT));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.1"), "2.0", ConditionType.GT));
        assertTrue(ConditionValidator.validate(2.1F, "2.0", ConditionType.GT));
        assertTrue(ConditionValidator.validate(2.1D, "2.0", ConditionType.GT));
        assertTrue(ConditionValidator.validate(2.1D, "2", ConditionType.GT));
        assertTrue(ConditionValidator.validate("EG", "E", ConditionType.GT));
        assertTrue(ConditionValidator.validate("32", "31", ConditionType.GT));
        // Negative checks
        assertFalse(ConditionValidator.validate(1, "2", ConditionType.GT));
        assertFalse(ConditionValidator.validate(1L, "2.0", ConditionType.GT));
        assertFalse(ConditionValidator.validate(new BigInteger("1"), "2.0", ConditionType.GT));
        assertFalse(ConditionValidator.validate(new BigDecimal("1.1"), "2.0", ConditionType.GT));
        assertFalse(ConditionValidator.validate(1.1F, "2.0", ConditionType.GT));
        assertFalse(ConditionValidator.validate(1.1D, "2.0", ConditionType.GT));
        assertFalse(ConditionValidator.validate("E", "EG", ConditionType.GT));
        assertFalse(ConditionValidator.validate(3, "G", ConditionType.GT));
        assertFalse(ConditionValidator.validate("3.1", "31", ConditionType.GT));
    }

    @Test
    @SneakyThrows
    void testValidatorLT() {
        // Positive checks
        assertTrue(ConditionValidator.validate(2, "3", ConditionType.LT));
        assertTrue(ConditionValidator.validate(2L, "3.0", ConditionType.LT));
        assertTrue(ConditionValidator.validate(new BigInteger("2"), "3.0", ConditionType.LT));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.101"), "2.102", ConditionType.LT));
        assertTrue(ConditionValidator.validate(2.0F, "2.1", ConditionType.LT));
        assertTrue(ConditionValidator.validate(2.0D, "2.1", ConditionType.LT));
        assertTrue(ConditionValidator.validate(2D, "2.1", ConditionType.LT));
        assertTrue(ConditionValidator.validate("E", "EG", ConditionType.LT));
        assertTrue(ConditionValidator.validate("31", "32", ConditionType.LT));
        // Negative checks
        assertFalse(ConditionValidator.validate(2, "1", ConditionType.LT));
        assertFalse(ConditionValidator.validate(2L, "1.0", ConditionType.LT));
        assertFalse(ConditionValidator.validate(new BigInteger("2"), "1.0", ConditionType.LT));
        assertFalse(ConditionValidator.validate(new BigDecimal("2.0"), "1.1", ConditionType.LT));
        assertFalse(ConditionValidator.validate(2.0F, "1.1", ConditionType.LT));
        assertFalse(ConditionValidator.validate(2.0D, "1.1", ConditionType.LT));
        assertFalse(ConditionValidator.validate("EG", "E", ConditionType.LT));
        assertFalse(ConditionValidator.validate("G", "3", ConditionType.LT));
        assertFalse(ConditionValidator.validate("31", "3.1", ConditionType.LT));
    }

    @Test
    @SneakyThrows
    void testValidatorGE() {
        // Positive checks
        assertTrue(ConditionValidator.validate(3, "2", ConditionType.GE));
        assertTrue(ConditionValidator.validate(3L, "2.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(new BigInteger("3"), "2.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.1"), "2.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(2.1F, "2.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(2.1D, "2.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(2.1D, "2", ConditionType.GE));
        assertTrue(ConditionValidator.validate("EG", "E", ConditionType.GE));
        assertTrue(ConditionValidator.validate("32", "31", ConditionType.GE));
        assertTrue(ConditionValidator.validate(3, "3", ConditionType.GE));
        assertTrue(ConditionValidator.validate(3L, "3.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(new BigInteger("3"), "3.0", ConditionType.GE));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.1"), "2.1", ConditionType.GE));
        assertTrue(ConditionValidator.validate(2.1F, "2.1", ConditionType.GE));
        assertTrue(ConditionValidator.validate(2.1D, "2.1", ConditionType.GE));
        assertTrue(ConditionValidator.validate("E", "E", ConditionType.GE));
        assertTrue(ConditionValidator.validate("32", "32", ConditionType.GE));
        // Negative checks
        assertFalse(ConditionValidator.validate(1, "2", ConditionType.GE));
        assertFalse(ConditionValidator.validate(1L, "2.0", ConditionType.GE));
        assertFalse(ConditionValidator.validate(new BigInteger("1"), "2.0", ConditionType.GE));
        assertFalse(ConditionValidator.validate(new BigDecimal("1.1"), "2.0", ConditionType.GE));
        assertFalse(ConditionValidator.validate(1.1F, "2.0", ConditionType.GE));
        assertFalse(ConditionValidator.validate(1.1D, "2.0", ConditionType.GE));
        assertFalse(ConditionValidator.validate("E", "EG", ConditionType.GE));
        assertFalse(ConditionValidator.validate(3, "G", ConditionType.GE));
        assertFalse(ConditionValidator.validate("3.1", "31", ConditionType.GE));
    }

    @Test
    @SneakyThrows
    void testValidatorLE() {
        // Positive checks
        assertTrue(ConditionValidator.validate(2, "3", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2L, "3.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(new BigInteger("2"), "3.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.0"), "2.1", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2.0F, "2.1", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2.0D, "2.1", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2D, "2.1", ConditionType.LE));
        assertTrue(ConditionValidator.validate("E", "EG", ConditionType.LE));
        assertTrue(ConditionValidator.validate("31", "32", ConditionType.LE));
        assertTrue(ConditionValidator.validate(3, "3", ConditionType.LE));
        assertTrue(ConditionValidator.validate(3L, "3.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(new BigInteger("3"), "3.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(new BigDecimal("2.101"), "2.102", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2.0F, "2.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2.0D, "2.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate(2D, "2.0", ConditionType.LE));
        assertTrue(ConditionValidator.validate("E", "E", ConditionType.LE));
        assertTrue(ConditionValidator.validate("31", "31", ConditionType.LE));
        // Negative checks
        assertFalse(ConditionValidator.validate(2, "1", ConditionType.LE));
        assertFalse(ConditionValidator.validate(2L, "1.0", ConditionType.LE));
        assertFalse(ConditionValidator.validate(new BigInteger("2"), "1.0", ConditionType.LE));
        assertFalse(ConditionValidator.validate(new BigDecimal("2.0"), "1.1", ConditionType.LE));
        assertFalse(ConditionValidator.validate(2.0F, "1.1", ConditionType.LE));
        assertFalse(ConditionValidator.validate(2.0D, "1.1", ConditionType.LE));
        assertFalse(ConditionValidator.validate("EG", "E", ConditionType.LE));
        assertFalse(ConditionValidator.validate("G", "3", ConditionType.LE));
        assertFalse(ConditionValidator.validate("31", "3.1", ConditionType.LE));
    }

    @Test
    @SneakyThrows
    void testValidatorNULL() {
        // Positive checks
        assertTrue(ConditionValidator.validate(null, null, ConditionType.NULL));
        // Negative checks
        assertFalse(ConditionValidator.validate("some_string", null, ConditionType.NULL));
        assertFalse(ConditionValidator.validate(1, null, ConditionType.NULL));
        assertFalse(ConditionValidator.validate(new Object(), null, ConditionType.NULL));
    }

    @Test
    @SneakyThrows
    void testValidatorANY() {
        // Positive checks
        assertTrue(ConditionValidator.validate("some_string", null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(1, null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(1L, null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(1.123D, null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(1.123F, null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(BigInteger.valueOf(123), null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(BigDecimal.valueOf(123.123D), null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(true, null, ConditionType.ANY));
        assertTrue(ConditionValidator.validate(new Object(), null, ConditionType.ANY));
        // Negative checks
        assertFalse(ConditionValidator.validate(null, null, ConditionType.ANY));
    }

    @Test
    @SneakyThrows
    void testValidatorTRUE() {
        // Positive checks
        assertTrue(ConditionValidator.validate(true, null, ConditionType.TRUE));
        assertTrue(ConditionValidator.validate(Boolean.TRUE, null, ConditionType.TRUE));
        assertTrue(ConditionValidator.validate("true", null, ConditionType.TRUE));
        // Negative checks
        assertFalse(ConditionValidator.validate(1, null, ConditionType.TRUE));
        assertFalse(ConditionValidator.validate("1", null, ConditionType.TRUE));
    }

    @Test
    @SneakyThrows
    void testValidatorFALSE() {
        // Positive checks
        assertTrue(ConditionValidator.validate(false, null, ConditionType.FALSE));
        assertTrue(ConditionValidator.validate(Boolean.FALSE, null, ConditionType.FALSE));
        assertTrue(ConditionValidator.validate("false", null, ConditionType.FALSE));
        assertTrue(ConditionValidator.validate(0, null, ConditionType.FALSE));
        assertTrue(ConditionValidator.validate("0", null, ConditionType.FALSE));
        assertTrue(ConditionValidator.validate("SOMEINVALIDSTRING", null, ConditionType.FALSE));
        // Negative checks
        assertFalse(ConditionValidator.validate(true, null, ConditionType.FALSE));
        assertFalse(ConditionValidator.validate(Boolean.TRUE, null, ConditionType.FALSE));
        assertFalse(ConditionValidator.validate("true", null, ConditionType.FALSE));
    }
}
