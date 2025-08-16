package com.tjardas.iisapi.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "SearchResponse", namespace = "http://example.com/countries")
public class SearchResponse {
    private List<Countries.Country> countries;

    @XmlElement(name = "Country", namespace = "http://example.com/countries")
    public List<Countries.Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Countries.Country> countries) {
        this.countries = countries;
    }

}
