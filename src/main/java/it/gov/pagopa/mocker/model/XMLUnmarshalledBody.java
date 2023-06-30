package it.gov.pagopa.mocker.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class XMLUnmarshalledBody implements UnmarshalledBody {

    private ParsedNode node;

    public XMLUnmarshalledBody(ParsedNode node) {
        this.node = node;
    }

    public Object getFieldValue(String fieldName) {
        ParsedNode analyzedNode = null;
        int id = -1;
        List<String> decomposedFields = Arrays.asList(fieldName.split("\\."));
        ParsedNode analyzedMap = this.node;
        Iterator<String> it = decomposedFields.iterator();
        while (it.hasNext() && analyzedMap != null) {
            String field = it.next();
            if (field.contains("[")) {
                id = Integer.parseInt(field.substring(field.indexOf('[') + 1, field.indexOf(']')));
                field = field.substring(0, field.indexOf('['));
            }
            analyzedNode = analyzedMap.getChild(field);
            if (analyzedNode != null && analyzedNode.isList()) {
                analyzedMap = analyzedNode.getChild(id);
            } else {
                analyzedMap = analyzedNode;
            }
        }
        return getNodeContent(analyzedNode, id);
    }

    private Object getNodeContent(ParsedNode analyzedNode, int id) {
        if (analyzedNode != null) {
            return analyzedNode.isList() ? analyzedNode.getValue(id) : analyzedNode.getValue();
        }
        return null;
    }
}
