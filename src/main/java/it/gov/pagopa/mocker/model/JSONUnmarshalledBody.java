package it.gov.pagopa.mocker.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JSONUnmarshalledBody implements UnmarshalledBody {

    private Map<String, Object> bodyFields;

    public JSONUnmarshalledBody(Map<String, Object> bodyFields) {
        this.bodyFields = bodyFields;
    }

    public Object getFieldValue(String fieldName) {
        Object analyzedNode = null;
        int id = -1;
        List<String> decomposedFields = Arrays.asList(fieldName.split("\\."));
        Map<String, Object> analyzedMap = this.bodyFields;
        for (String field : decomposedFields) {
            if (field.contains("[")) {
                id = Integer.parseInt(field.substring(field.indexOf('[') + 1, field.indexOf(']')));
                field = field.substring(0, field.indexOf('['));
            }
            analyzedNode = analyzedMap.get(field);
            if (analyzedNode instanceof List && id != -1) {
                analyzedMap = (Map<String, Object>) ((List<Object>) analyzedNode).get(id);
            } else if (analyzedNode instanceof Map) {
                analyzedMap = (Map<String, Object>) analyzedNode;
            }
        }
        return getNodeContent(analyzedNode, decomposedFields.get(decomposedFields.size() - 1), id);
    }

    private Object getNodeContent(Object analyzedNode, String finalFieldName, Integer id) {
        if (analyzedNode instanceof Map) {
            return ((Map<String, Object>) analyzedNode).get(finalFieldName);
        } else if (analyzedNode instanceof List && id != -1) {
            return ((List<Map<String, Object>>) analyzedNode).get(id).get(finalFieldName.substring(0, finalFieldName.indexOf('[')));
        } else {
            return analyzedNode;
        }
    }
}
