package com.tjardas.iisapi.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherService {

    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";

    public List<String> getTemperature(String cityName) {
        List<String> results = new ArrayList<>();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(DHMZ_URL);
            try (CloseableHttpResponse response = client.execute(request);
                 InputStream content = response.getEntity().getContent()) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(content);

                NodeList cities = doc.getElementsByTagName("Grad");
                for (int i = 0; i < cities.getLength(); i++) {
                    Element city = (Element) cities.item(i);
                    String name = city.getElementsByTagName("GradIme").item(0).getTextContent().trim();
                    if (name.toLowerCase().contains(cityName.toLowerCase())) {
                        String temp = city.getElementsByTagName("Temp").item(0).getTextContent().trim();
                        results.add(name + ": " + temp);
                    }
                }
            }
        } catch (Exception e) {
            return Collections.singletonList("Error retrieving temperature: " + e.getMessage());
        }
        if (results.isEmpty()) {
            return Collections.singletonList("City not found");
        }
        return results;
    }
}