package com.tjardas.iisapi.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "Players", namespace = "http://example.com/players")
@XmlType(namespace = "http://example.com/players")
public class Players {
    private List<Player> players;

    @XmlElement(name = "Player", namespace = "http://example.com/players")
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @XmlType(namespace = "http://example.com/players")
    public static class Player {
        private String name;
        private String team;
        private int season;
        private float points;

        @XmlElement(namespace = "http://example.com/players")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement(namespace = "http://example.com/players")
        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        @XmlElement(namespace = "http://example.com/players")
        public int getSeason() {
            return season;
        }

        public void setSeason(int season) {
            this.season = season;
        }

        @XmlElement(namespace = "http://example.com/players")
        public float getPoints() {
            return points;
        }

        public void setPoints(float points) {
            this.points = points;
        }
    }
}
