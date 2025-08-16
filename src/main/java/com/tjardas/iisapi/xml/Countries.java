// Countries.java
package com.tjardas.iisapi.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "Countries", namespace = "http://example.com/countries")
@XmlType(namespace = "http://example.com/countries")
public class Countries {
    private List<Country> countries;

    @XmlElement(name = "Country", namespace = "http://example.com/countries")
    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    @XmlType(namespace = "http://example.com/countries")
    public static class Country {
        private String name;
        private String subRegion;
        private int year;
        private float value;

        @XmlElement(namespace = "http://example.com/countries")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement(namespace = "http://example.com/countries")
        public String getSubRegion() {
            return subRegion;
        }

        public void setSubRegion(String subRegion) {
            this.subRegion = subRegion;
        }

        @XmlElement(namespace = "http://example.com/countries")
        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        @XmlElement(namespace = "http://example.com/countries")
        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }
}