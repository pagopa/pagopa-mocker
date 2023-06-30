package it.gov.pagopa.mocker.util;

import it.gov.pagopa.mocker.MockerApplication;
import it.gov.pagopa.mocker.model.UnmarshalledBody;
import it.gov.pagopa.mocker.model.XMLUnmarshalledBody;
import it.gov.pagopa.mocker.utility.TestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = MockerApplication.class)
class XMLParserTest {

    @Test
    @SneakyThrows
    void testParser() {

        XMLParser parser = new XMLParser();
        String content = TestUtil.readXMLFromFile("request/parser_ok.xml");
        XMLUnmarshalledBody unmarshalledBody = parser.parse(content);

        assertEquals("field1", unmarshalledBody.getFieldValue("Body.level1field"));
        assertEquals("field2", unmarshalledBody.getFieldValue("Body.level1obj.level2field"));
        assertEquals("field3", unmarshalledBody.getFieldValue("Body.level1obj.level2obj.level3field"));
        assertEquals("field4", unmarshalledBody.getFieldValue("Body.level1obj.level2obj.level3obj.level4field"));

        assertEquals("field2list1", unmarshalledBody.getFieldValue("Body.level1list[0]"));
        assertEquals("field2list3", unmarshalledBody.getFieldValue("Body.level1list[2]"));
        assertNull(unmarshalledBody.getFieldValue("Body.level1list[15]"));
        assertNull(unmarshalledBody.getFieldValue("Body.level1list.level2element[0]"));

        assertEquals("field1obj2", unmarshalledBody.getFieldValue("Body.level1objlist[1].level3field1"));
        assertNull(unmarshalledBody.getFieldValue("Body.level1objlist.level2obj.level3field1"));

        UnmarshalledBody unmarshalledBody2 = parser.parse("<Envelope></Envelope>");
        assertNull(unmarshalledBody2.getFieldValue("Body.level1list[0]"));
    }
}
