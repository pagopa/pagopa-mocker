package it.gov.pagopa.mocker.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UnmarshalledBody {

    private Map<String, Object> bodyFields;

    public UnmarshalledBody(Map<String, Object> bodyFields) {
        this.bodyFields = bodyFields;
    }

    public Object getFieldValue(String fieldName) {
        Object analyzedNode = null;
        List<String> decomposedFields = Arrays.asList(fieldName.split("\\."));
        Map<String, Object> analyzedMap = this.bodyFields;
        String finalFieldName = decomposedFields.get(decomposedFields.size() - 1);
        for (String field : decomposedFields) {
            int id = -1;
            if (field.contains("[")) {
                id = Integer.parseInt(field.substring(field.indexOf('[') + 1, field.indexOf(']')));
                field = field.substring(0, field.indexOf('['));
            }
            analyzedNode = analyzedMap.get(field);
            if (analyzedNode instanceof List) {
                analyzedMap = (Map<String, Object>) ((List<Object>) analyzedNode).get(id);
            } else if (analyzedNode instanceof Map) {
                analyzedMap = (Map<String, Object>) analyzedNode;
            }
        }
        return (analyzedNode instanceof Map) ? ((Map<String, Object>) analyzedNode).get(finalFieldName) : analyzedNode;
    }
}
