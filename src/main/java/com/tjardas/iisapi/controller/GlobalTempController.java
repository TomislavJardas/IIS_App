package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.model.CountryEntity;
import com.tjardas.iisapi.repository.CountryRepository;
import com.tjardas.iisapi.xml.Countries;
import com.tjardas.iisapi.service.CsvService;
import com.tjardas.iisapi.service.XmlValidationService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GlobalTempController {

    private final CsvService csvService;
    private final XmlValidationService xmlValidationService;
    private final CountryRepository countryRepository;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE, path = "/countries")
    public String getCountriesAsXml(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String subRegion,
            @RequestParam(required = false) Integer year) throws Exception {

        Countries countries = csvService.getFilteredCountries(country, subRegion, year);
        JAXBContext context = JAXBContext.newInstance(Countries.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(countries, writer);
        return writer.toString();
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/validateAndSaveXml")
    public String validateAndSaveXml(@RequestBody String xml) throws Exception {
        xmlValidationService.validateXml(xml, "entity-schema.xsd");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        NodeList countryNodes = document.getElementsByTagName("Country");
        List<CountryEntity> countryEntities = new ArrayList<>();

        for (int i = 0; i < countryNodes.getLength(); i++) {
            var node = countryNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                var countryEntity = new CountryEntity();

                countryEntity.setName(element.getElementsByTagName("name").item(0).getTextContent());
                countryEntity.setSubRegion(element.getElementsByTagName("subRegion").item(0).getTextContent());
                countryEntity.setYear(Integer.parseInt(element.getElementsByTagName("year").item(0).getTextContent()));
                countryEntity.setValue(Float.parseFloat(element.getElementsByTagName("value").item(0).getTextContent()));

                countryEntities.add(countryEntity);
            }
        }

        countryRepository.saveAll(countryEntities);
        countryRepository.findAll().forEach(countryEntity -> log.info(countryEntity.toString()));

        return "XML successfully validated and saved!";
    }
}
