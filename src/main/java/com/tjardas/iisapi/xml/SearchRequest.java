package com.tjardas.iisapi.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SearchRequest", namespace = "http://example.com/players")
public class SearchRequest {
    private String searchTerm;

    @XmlElement(name = "SearchTerm", namespace = "http://example.com/players")
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
