package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.model.enumeration.ConditionType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class ConditionValidator {

    private ConditionValidator() {}

    private static final Map<ConditionType, BiFunction<Object, String, Boolean>> CONDITION_MAP;
    static {
        Map<ConditionType, BiFunction<Object, String, Boolean>> map = new EnumMap<>(ConditionType.class);
        map.put(ConditionType.REGEX, (fieldValue, conditionValue) -> fieldValue != null && Pattern.compile(conditionValue).matcher((String) fieldValue).find());
        map.put(ConditionType.EQ, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) == 0);
        map.put(ConditionType.NEQ, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) != 0);
        map.put(ConditionType.GT, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) > 0);
        map.put(ConditionType.LT, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) < 0);
        map.put(ConditionType.GE, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) >= 0);
        map.put(ConditionType.LE, (fieldValue, conditionValue) -> fieldValue != null && compare(fieldValue, conditionValue) <= 0);
        map.put(ConditionType.NULL, (fieldValue, conditionValue) -> fieldValue == null);
        map.put(ConditionType.ANY, (fieldValue, conditionValue) -> fieldValue != null);
        map.put(ConditionType.TRUE, (fieldValue, conditionValue) -> fieldValue != null && Boolean.parseBoolean(fieldValue.toString()));
        map.put(ConditionType.FALSE, (fieldValue, conditionValue) -> fieldValue != null && !Boolean.parseBoolean(fieldValue.toString()));
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
            result = compare(conditionValue, BigDecimal.valueOf((Integer) fieldValue));
        } else if (fieldValue instanceof Float) {
            result = compare(conditionValue, BigDecimal.valueOf((Float) fieldValue));
        } else if (fieldValue instanceof Double) {
            result = compare(conditionValue, BigDecimal.valueOf((Double) fieldValue));
        } else if (fieldValue instanceof Long) {
            result = compare(conditionValue, BigDecimal.valueOf((Long) fieldValue));
        } else if (fieldValue instanceof BigInteger) {
            result = compare(conditionValue, BigDecimal.valueOf(((BigInteger) fieldValue).longValue()));
        } else if (fieldValue instanceof BigDecimal) {
            result = compare(conditionValue, (BigDecimal) fieldValue);
        }
        return result * -1; // invert the order of comparison
    }

    private static int compare(String conditionValue, String fieldValue) {
        return conditionValue.compareTo(fieldValue);
    }

    private static int compare(String conditionValue, BigDecimal fieldValue) {
        BigDecimal bigDecimalConditionValue = new BigDecimal(conditionValue);
        int scale = bigDecimalConditionValue.scale();
        return bigDecimalConditionValue.compareTo(fieldValue.setScale(scale > 0 ? scale : 10, RoundingMode.HALF_UP));
    }
}
