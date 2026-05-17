package com.tjardas.iisapi.service;

import com.tjardas.iisapi.exception.XmlValidationException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class XmlValidationService {
    public void validateXml(String xml, String schemaPath) throws IOException, SAXException {
        validateXml(xml, schemaPath, "XSD");
    }

    public void validateXml(String xml, String schemaPath, String schemaType) throws IOException, SAXException {
        String normalizedType = schemaType == null ? "" : schemaType.trim().toUpperCase(Locale.ROOT);

        if (!"XSD".equals(normalizedType) && !"RNG".equals(normalizedType)) {
            throw new IllegalArgumentException("Supported schema types are XSD and RNG.");
        }

        if ("RNG".equals(normalizedType)) {
            throw new IllegalArgumentException("Supported schema types are XSD and RNG.");
        }

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(Objects.requireNonNull(getClass().getClassLoader().getResource(schemaPath)));
        Validator validator = schema.newValidator();

        try (StringReader reader = new StringReader(xml)) {
            validator.validate(new StreamSource(reader));
        } catch (SAXParseException e) {
            throw new XmlValidationException("XML validation failed.", toFieldErrors(e.getMessage()), e);
        } catch (SAXException e) {
            throw new XmlValidationException("XML validation failed.", toFieldErrors(e.getMessage()), e);
        }
    }

    private Map<String, String> toFieldErrors(String rawMessage) {
        String message = rawMessage == null ? "XML does not match schema." : rawMessage;
        String lower = message.toLowerCase(Locale.ROOT);

        Map<String, String> errors = new LinkedHashMap<>();
        if (lower.contains("points")) {
            errors.put("points", "Points must be a valid number.");
        } else if (lower.contains("season")) {
            errors.put("season", "Season must be a valid integer.");
        } else if (lower.contains("name")) {
            errors.put("name", "Name is required.");
        } else if (lower.contains("team")) {
            errors.put("team", "Team is required.");
        } else {
            errors.put("xml", message);
        }

        return errors;
    }
}
