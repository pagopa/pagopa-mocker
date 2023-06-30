package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.model.ParsedNode;
import it.gov.pagopa.mocker.model.XMLUnmarshalledBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XMLParser {

    private final DocumentBuilder builder;

    public XMLParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(Constants.APACHE_DISALLOW_DOCTYPE_DECL_FEATURE, true);
        builder = factory.newDocumentBuilder();
    }

    public XMLUnmarshalledBody parse(String xml) throws IOException, SAXException {
        String trimmedXml = xml.replaceAll(Constants.REGEX_XML_TRIM, Constants.REGEX_XML_TRIM_REPLACEMENT);
        Document document = builder.parse(new ByteArrayInputStream(trimmedXml.getBytes()));
        return new XMLUnmarshalledBody((ParsedNode) extractFields(document.getDocumentElement()));
    }

    private Object extractFields(Node node) {
        ParsedNode finalNode = new ParsedNode();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            String fieldName = normalizeFieldName(currentNode.getNodeName());
            Object fieldValue = null;
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                fieldValue = extractFields(currentNode);
            }
            else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                finalNode.setValue(currentNode.getTextContent());
                return finalNode;
            }

            Node sibling = currentNode.getNextSibling();
            finalNode.setAsList(finalNode.isList() || (sibling != null && currentNode.getNodeName().equals(sibling.getNodeName())));
            finalNode.add(fieldName, fieldValue);
        }
        return finalNode;
    }

    private static String normalizeFieldName(String fieldName) {
        return fieldName.replaceAll(Constants.REGEX_NORMALIZE_FIELDNAME, Constants.EMPTY_STRING);
    }
}
