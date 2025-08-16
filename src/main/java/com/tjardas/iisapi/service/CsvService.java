package com.tjardas.iisapi.service;

import com.tjardas.iisapi.xml.Countries;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvService {
    private final List<Countries.Country> records = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL = "https://api-nba-v1.p.rapidapi.com/players/statistics?game=8133"; // Replace with actual API URL
    private static final String API_KEY = "1878146bcdmsh2932967a89b2b3ap1e33a9jsn34f5e618dc13"; // Replace with your RapidAPI key

    @PostConstruct
    public void loadApiData() {
        try {
            // Set HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", API_KEY);
            headers.set("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com"); // Replace with the actual host
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create an HTTP request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // Make the GET request to the API
            ResponseEntity<Countries.Country[]> response = restTemplate.exchange(
                    API_URL, HttpMethod.GET, requestEntity, Countries.Country[].class);

            // Convert response body to a list and add to records
            if (response.getBody() != null) {
                records.addAll(Arrays.asList(response.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching data from API: " + e.getMessage());
        }
    }

    public Countries getFilteredCountries(String country, String subRegion, Integer year) {
        List<Countries.Country> filtered = records.stream()
                .filter(r -> country == null || r.getName().equalsIgnoreCase(country))
                .filter(r -> subRegion == null || r.getSubRegion().equalsIgnoreCase(subRegion))
                .filter(r -> year == null || r.getYear() == year)
                .toList();

        Countries countries = new Countries();
        countries.setCountries(filtered);
        return countries;
    }
}
