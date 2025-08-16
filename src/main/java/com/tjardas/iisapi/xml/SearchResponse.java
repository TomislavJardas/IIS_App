package com.tjardas.iisapi.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "SearchResponse", namespace = "http://example.com/players")
public class SearchResponse {
    private List<Players.Player> players;

    @XmlElement(name = "Player", namespace = "http://example.com/players")
    public List<Players.Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Players.Player> players) {
        this.players = players;
    }

}
