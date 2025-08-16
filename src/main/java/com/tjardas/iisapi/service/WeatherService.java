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

public class WeatherService {

    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";

    public String getTemperature(String cityName) {
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
                    if (cityName.equalsIgnoreCase(name)) {
                        return city.getElementsByTagName("Temp").item(0).getTextContent().trim();
                    }
                }
            }
        } catch (Exception e) {
            return "Error retrieving temperature: " + e.getMessage();
        }
        return "City not found";
    }
}