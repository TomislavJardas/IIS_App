package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.exception.MalformedXmlException;
import com.tjardas.iisapi.exception.SaveOperationException;
import com.tjardas.iisapi.exception.XmlValidationException;
import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.service.NbaApiService;
import com.tjardas.iisapi.service.XmlValidationService;
import com.tjardas.iisapi.xml.Players;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final NbaApiService nbaApiService;
    private final XmlValidationService xmlValidationService;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE, path = "/players")
    public String getPlayersAsXml(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) Integer season) throws Exception {

        Players players = nbaApiService.getFilteredPlayers(name, team, season);
        JAXBContext context = JAXBContext.newInstance(Players.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(players, writer);
        return writer.toString();
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/validateAndSaveXml")
    public String validateAndSaveXml(@RequestBody String xml) throws Exception {
        xmlValidationService.validateXml(xml, "player-schema.xsd", "XSD");

        Document document = parseXmlDocument(xml);
        NodeList playerNodes = document.getElementsByTagName("Player");
        List<PlayerEntity> playerEntities = new ArrayList<>();

        for (int i = 0; i < playerNodes.getLength(); i++) {
            Node node = playerNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                PlayerEntity playerEntity = new PlayerEntity();

                try {
                    playerEntity.setName(element.getElementsByTagName("name").item(0).getTextContent());
                    playerEntity.setTeam(element.getElementsByTagName("team").item(0).getTextContent());
                    playerEntity.setSeason(Integer.parseInt(element.getElementsByTagName("season").item(0).getTextContent()));
                    playerEntity.setPoints(Float.parseFloat(element.getElementsByTagName("points").item(0).getTextContent()));
                } catch (NumberFormatException | NullPointerException e) {
                    throw new XmlValidationException("XML validation failed.", List.of("Invalid numeric field value in XML payload."), e);
                }

                playerEntities.add(playerEntity);
            }
        }

        playerEntities.forEach(player -> {
            try {
                PlayerEntity created = nbaApiService.createPlayer(player);
                log.info("Saved PlayerEntity to PocketBase: {}", created);
            } catch (RuntimeException e) {
                throw new SaveOperationException("Validation succeeded, but saving failed.", e);
            }
        });

        return "XML successfully validated and saved to PocketBase players collection!";
    }

    private Document parseXmlDocument(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new MalformedXmlException("Malformed XML.", e);
        }
    }
}
