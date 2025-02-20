package com.gihub.maxonrash.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihub.maxonrash.dto.response.geocoding.CityResponseDTO;
import com.gihub.maxonrash.dto.response.geocoding.GeocodingResponseDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class GetGeocodingService {
    public CityResponseDTO[] getGeocodingByCityNameService(String cityName, String apiKey) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(""))
                .uri(URI.create(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", cityName, apiKey)))
                .header("Content-Type", "application/json")
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response =  client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
//            return new ObjectMapper().readValue("{\"cities\": " + response.body() + "}", GeocodingResponseDTO.class);
            return new ObjectMapper().readValue(response.body(), CityResponseDTO[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
