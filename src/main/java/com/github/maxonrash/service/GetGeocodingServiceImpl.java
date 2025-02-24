package com.github.maxonrash.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maxonrash.dto.response.geocoding.GetGeocodingResponseDTO;
import com.github.maxonrash.exception.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link GetGeocodingService} using OpenWeatherMap.org for getting city's name geocode
 */
public class GetGeocodingServiceImpl implements GetGeocodingService {
    /**
     * Returns an array of found cities {@link GetGeocodingResponseDTO} for specified cityName
     *
     * @param cityName name of city that must follow pattern "^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$" Example: Rostov_na_donu
     * @param apiKey API Key for accessing a weather API
     * @return array of {@link GetGeocodingResponseDTO}
     * @throws InvalidApiKeyException if API key is incorrect
     * @throws CityWithThisNameIsNotFoundException if the city with specified name is not found by weather API
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    @Override
    public GetGeocodingResponseDTO[] getGeocodingByCityName(String cityName, String apiKey) throws InvalidApiKeyException, CityWithThisNameIsNotFoundException, CallPerMinuteExceededException, InternalErrorException {
        if (cityName == null || !Pattern.compile("^[\\w&&[^\\d]][\\w-_&&[^\\d]]{0,30}$").matcher(cityName).find()) {
            throw new IncorrectCityNameException();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(response.body());
            if (node.has("cod")) {
                int responseCode = node.get("cod").asInt();
                switch (responseCode) {
                    case 401 -> throw new InvalidApiKeyException();
                    case 429 -> throw new CallPerMinuteExceededException();
                    default -> throw new InternalErrorException(node.get("message").asText());
                }
            }
            GetGeocodingResponseDTO[] responseDeserialized = objectMapper.readValue(response.body(), GetGeocodingResponseDTO[].class);
            if (responseDeserialized.length < 1) {
                throw new CityWithThisNameIsNotFoundException(cityName);
            }
            return responseDeserialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
