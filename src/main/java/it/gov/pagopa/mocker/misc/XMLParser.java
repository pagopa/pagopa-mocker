package it.gov.pagopa.mocker.misc;
import it.gov.pagopa.mocker.model.UnmarshalledBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class XMLParser {

    private final DocumentBuilder builder;

    public XMLParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(Constants.APACHE_DISALLOW_DOCTYPE_DECL_FEATURE, true);
        builder = factory.newDocumentBuilder();
    }

    public UnmarshalledBody parse(String xml) throws IOException, SAXException {
        String trimmedXml = xml.replaceAll(Constants.REGEX_XML_TRIM, Constants.REGEX_XML_TRIM_REPLACEMENT);
        Document document = builder.parse(new ByteArrayInputStream(trimmedXml.getBytes()));
        return new UnmarshalledBody(extractFields(document.getDocumentElement()));
    }

    public static Map<String, Object> extractFields(Node node) {
        Map<String, Object> map = new HashMap<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            String fieldName = normalizeFieldName(currentNode.getNodeName());
            Object fieldValue = null;
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                fieldValue = extractFields(currentNode);
            }
            else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                map.put(currentNode.getParentNode().getNodeName(), currentNode.getTextContent());
                return map;
            }
            if (map.containsKey(fieldName)) {
                Object nestedFieldContent = map.get(fieldName);
                if (nestedFieldContent instanceof List) {
                    ((List<Object>) nestedFieldContent).add(fieldValue);
                }
                else {
                    List<Object> fields = new LinkedList<>();
                    fields.add(nestedFieldContent);
                    fields.add(fieldValue);
                    map.put(fieldName, fields);
                }
            }
            else {
                map.put(fieldName, fieldValue);
            }
        }
        return map;
    }

    private static String normalizeFieldName(String fieldName) {
        return fieldName.replaceAll(Constants.REGEX_NORMALIZE_FIELDNAME, Constants.EMPTY_STRING);
    }
}
