package com.tjardas.iisapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class XmlValidationService {
    public void validateXml(String xml, String schemaPath) throws Exception {
        var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = factory.newSchema(getClass().getClassLoader().getResource(schemaPath));

        Validator validator = schema.newValidator();

        try (StringReader reader = new StringReader(xml)) {
            validator.validate(new StreamSource(reader));
        } catch (SAXException e) {
            throw new IllegalArgumentException("XML validation error: " + e.getMessage(), e);
        }
    }

}
