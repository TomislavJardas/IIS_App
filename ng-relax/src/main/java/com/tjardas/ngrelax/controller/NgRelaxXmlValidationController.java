package com.tjardas.ngrelax.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@RestController
public class NgRelaxXmlValidationController {

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/validate")
    public ResponseEntity<String> validateXml(@RequestBody String xml) throws URISyntaxException, IOException, SAXException {
        validateWithRNG(xml);
        return new ResponseEntity<>("Validation successful!", HttpStatus.OK);
    }

        public void validateWithRNG(String xml) throws SAXException, URISyntaxException, IOException {
        URI rngFileURI = Objects.requireNonNull(getClass().getClassLoader().getResource("entity-schema.rng")).toURI();
        SchemaFactory schemaFactory = new com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory();
        Schema schema = schemaFactory.newSchema(new File(rngFileURI));

        Validator validator = schema.newValidator();

        try (StringReader reader = new StringReader(xml)) {
            validator.validate(new StreamSource(reader));
        } catch (SAXException e) {
            throw new IllegalArgumentException("XML validation error: " + e.getMessage(), e);
        }
    }
}
