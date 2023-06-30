package it.gov.pagopa.mocker.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParsedNode {

    private Object value;

    private List<ParsedNode> elements;

    private Map<String, ParsedNode> children;

    private boolean isList;

    public ParsedNode() {
        this.value = null;
        this.isList = false;
        this.elements = new LinkedList<>();
        this.children = new HashMap<>();
    }

    public boolean isList() {
        return isList;
    }

    public void setAsList(boolean isList) {
        this.isList = isList;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void add(String name, Object node) {
        if (this.isList) {
            this.elements.add((ParsedNode) node);
        } else {
            this.children.put(name, (ParsedNode) node);
        }
    }

    public ParsedNode getChild(String name) {
        return this.children.get(name);
    }

    public ParsedNode getChild(int index) {
        return (index > -1 && index < this.elements.size()) ? this.elements.get(index) : null;
    }

    public Object getValue(int index) {
        return (index > -1 && index < this.elements.size() && this.elements.get(index) != null) ? this.elements.get(index).getValue() : null;
    }

    public Object getValue() {
        return value;
    }
}