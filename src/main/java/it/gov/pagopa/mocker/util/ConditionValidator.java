package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.model.enumeration.ConditionType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class ConditionValidator {

    private ConditionValidator() {}

    private static final Map<ConditionType, BiFunction<Object, String, Boolean>> CONDITION_MAP;
    static {
        Map<ConditionType, BiFunction<Object, String, Boolean>> map = new EnumMap<>(ConditionType.class);
        map.put(ConditionType.REGEX, (fieldValue, conditionValue) -> Pattern.compile(conditionValue).matcher((String) fieldValue).find());
        map.put(ConditionType.EQ, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) == 0);
        map.put(ConditionType.NEQ, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) != 0);
        map.put(ConditionType.GT, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) > 0);
        map.put(ConditionType.LT, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) < 0);
        map.put(ConditionType.GE, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) >= 0);
        map.put(ConditionType.LE, (fieldValue, conditionValue) -> compare(fieldValue, conditionValue) <= 0);
        map.put(ConditionType.NULL, (fieldValue, conditionValue) -> fieldValue == null);
        map.put(ConditionType.ANY, (fieldValue, conditionValue) -> fieldValue != null);
        map.put(ConditionType.TRUE, (fieldValue, conditionValue) -> Boolean.parseBoolean(fieldValue.toString()));
        map.put(ConditionType.FALSE, (fieldValue, conditionValue) -> !Boolean.parseBoolean(fieldValue.toString()));
        CONDITION_MAP = Collections.unmodifiableMap(map);
    }

    public static boolean validate(Object fieldValue, String conditionValue, ConditionType conditionType) {
        boolean result = false;
        try {
            BiFunction<Object, String, Boolean> conditionFunction = CONDITION_MAP.get(conditionType);
            result = conditionFunction != null && conditionFunction.apply(fieldValue, conditionValue);
        } catch (NumberFormatException e) {
            log.error(String.format("Error while validating the condition [%s %s %s]. ", fieldValue, conditionType, conditionValue), e);
        }
        return result;
    }

    private static int compare(Object fieldValue, String conditionValue) {
        int result = 0;
        if (fieldValue instanceof String) {
            result = compare(conditionValue, (String) fieldValue);
        } else if (fieldValue instanceof Integer) {
            result = compare(conditionValue, Double.valueOf((Integer) fieldValue));
        } else if (fieldValue instanceof Float) {
            result = compare(conditionValue, Double.valueOf((Float) fieldValue));
        } else if (fieldValue instanceof Double) {
            result = compare(conditionValue, (Double) fieldValue);
        } else if (fieldValue instanceof Long) {
            result = compare(conditionValue, Double.valueOf((Long) fieldValue));
        } else if (fieldValue instanceof BigInteger) {
            result = compare(conditionValue, Double.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof BigDecimal) {
            result = compare(conditionValue, Double.valueOf(fieldValue.toString()));
        }
        return result * -1; // invert the order of comparison
    }

    private static int compare(String conditionValue, String fieldValue) {
        return conditionValue.compareTo(fieldValue);
    }

    private static int compare(String conditionValue, Double fieldValue) {
        return Double.valueOf(conditionValue).compareTo(fieldValue);
    }
}
