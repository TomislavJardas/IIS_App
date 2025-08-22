package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.model.PlayerEntity;
import com.tjardas.iisapi.repository.PlayerRepository;
import com.tjardas.iisapi.xml.Players;
import com.tjardas.iisapi.service.NbaApiService;
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
public class PlayerController {

    private final NbaApiService nbaApiService;
    private final XmlValidationService xmlValidationService;
    private final PlayerRepository playerRepository;

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
        xmlValidationService.validateXml(xml, "player-schema.xsd");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        NodeList playerNodes = document.getElementsByTagName("Player");
        List<PlayerEntity> playerEntities = new ArrayList<>();

        for (int i = 0; i < playerNodes.getLength(); i++) {
            Node node = playerNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                PlayerEntity playerEntity = new PlayerEntity();

                playerEntity.setName(element.getElementsByTagName("name").item(0).getTextContent());
                playerEntity.setTeam(element.getElementsByTagName("team").item(0).getTextContent());
                playerEntity.setSeason(Integer.parseInt(element.getElementsByTagName("season").item(0).getTextContent()));
                playerEntity.setPoints(Float.parseFloat(element.getElementsByTagName("points").item(0).getTextContent()));

                playerEntities.add(playerEntity);
            }
        }

        playerRepository.saveAll(playerEntities);
        playerRepository.findAll().forEach(playerEntity -> log.info(playerEntity.toString()));

        return "XML successfully validated and saved!";
    }
}
