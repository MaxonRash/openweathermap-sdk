package com.github.maxonrash.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maxonrash.dto.response.weather.current.CurrentWeatherResponseDTO;
import com.github.maxonrash.exception.CallPerMinuteExceededException;
import com.github.maxonrash.exception.IncorrectLatAndLonStringException;
import com.github.maxonrash.exception.InternalErrorException;
import com.github.maxonrash.exception.InvalidApiKeyException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link GetCurrentWeatherService} using OpenWeatherMap.org for getting current weather
 */
@Slf4j
public class GetCurrentWeatherServiceImpl implements GetCurrentWeatherService {
    /**
     * Returns deserialized JSON response {@link CurrentWeatherResponseDTO} for specified <i>latitude</i> and <i>longitude</i>
     *
     * @param latAndLon string representation of <i>latitude</i> and <i>longitude</i>. Must match the pattern
     * "^lat=-?\d{1,2}\.\d{2,8}&lon=-?\d{1,3}\.\d{2,8}$" Example: "lat=55.7522&lon=37.6156"
     * @param apiKey API Key for accessing a weather API
     * @return instance of {@link CurrentWeatherResponseDTO}
     * @throws InvalidApiKeyException if API key is incorrect
     * @throws CallPerMinuteExceededException if the limit of calls per minuted was exceeded
     * @throws InternalErrorException if an error on weather API side has occurred
     */
    @Override
    public CurrentWeatherResponseDTO getCurrentWeatherByLatAndLonString(String latAndLon, String apiKey) throws InvalidApiKeyException, CallPerMinuteExceededException, InternalErrorException {
        if (!Pattern.compile("^lat=-?\\d{1,2}\\.\\d{2,8}&lon=-?\\d{1,3}\\.\\d{2,8}$").matcher(latAndLon).find()) {
            log.info("String representation of coordinates of city to update info about: " + latAndLon);
            throw new IncorrectLatAndLonStringException();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.openweathermap.org/data/2.5/weather?%s&appid=%s", latAndLon, apiKey)))
                .header("Content-Type", "application/json")
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();
        log.info("Trying to getCurrentWeather with link : " + String.format("https://api.openweathermap.org/data/2.5/weather?%s&appid=%s", latAndLon, "apiKeyHere"));
        HttpResponse<String> response;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response =  client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            JsonNode node = objectMapper.readTree(response.body());
            if (node.has("cod")) {
                int responseCode = node.get("cod").asInt();
                switch (responseCode) {
                    case 200 -> {
                        log.info("Full JSON string of successful call with code 200: ");
                        log.info(response.body());
                        return objectMapper.readValue(response.body(), CurrentWeatherResponseDTO.class);
                    }
                    case 401 -> throw new InvalidApiKeyException();
                    case 429 -> throw new CallPerMinuteExceededException();
                    default -> throw new InternalErrorException(node.get("message").asText());
                }
            }
            return objectMapper.readValue(response.body(), CurrentWeatherResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
