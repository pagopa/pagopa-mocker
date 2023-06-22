package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.model.enumeration.ConditionType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class ConditionValidator {

    private ConditionValidator() {}

    private static final Map<ConditionType, BiFunction<Object, String, Boolean>> CONDITION_MAP;
    static {
        Map<ConditionType, BiFunction<Object, String, Boolean>> map = new EnumMap<>(ConditionType.class);
        map.put(ConditionType.REGEX, (fieldValue, conditionValue) -> Pattern.compile(conditionValue).matcher((String) fieldValue).find());
        map.put(ConditionType.EQ, (fieldValue, conditionValue) -> {
            boolean isValid = false;
            if (fieldValue instanceof String) {
                isValid = conditionValue.equals(fieldValue);
            } else if (fieldValue instanceof Double) {
                isValid = Double.valueOf(conditionValue).equals(fieldValue);
            } else if (fieldValue instanceof Boolean) {
                isValid = Boolean.valueOf(conditionValue).equals(fieldValue);
            }
            return isValid;
        });
        map.put(ConditionType.NEQ, (fieldValue, conditionValue) -> {
            boolean isValid = false;
            if (fieldValue instanceof String) {
                isValid = !conditionValue.equals(fieldValue);
            } else if (fieldValue instanceof Double) {
                isValid = !Double.valueOf(conditionValue).equals(fieldValue);
            } else if (fieldValue instanceof Boolean) {
                isValid = !Boolean.valueOf(conditionValue).equals(fieldValue);
            }
            return isValid;
        });
        map.put(ConditionType.GT, (fieldValue, conditionValue) -> fieldValue instanceof Double && ((Double) fieldValue) > Double.parseDouble(conditionValue));
        map.put(ConditionType.LT, (fieldValue, conditionValue) -> fieldValue instanceof Double && ((Double) fieldValue) < Double.parseDouble(conditionValue));
        map.put(ConditionType.GE, (fieldValue, conditionValue) -> fieldValue instanceof Double && ((Double) fieldValue) >= Double.parseDouble(conditionValue));
        map.put(ConditionType.LE, (fieldValue, conditionValue) -> fieldValue instanceof Double && ((Double) fieldValue) <= Double.parseDouble(conditionValue));
        map.put(ConditionType.NULL, (fieldValue, conditionValue) -> fieldValue == null);
        map.put(ConditionType.ANY, (fieldValue, conditionValue) -> fieldValue != null);
        CONDITION_MAP = Collections.unmodifiableMap(map);
    }

    public static boolean validate(Object fieldValue, String conditionValue, ConditionType conditionType) {
        BiFunction<Object, String, Boolean> conditionFunction = CONDITION_MAP.get(conditionType);
        return conditionFunction != null && conditionFunction.apply(fieldValue, conditionValue);
    }

}
