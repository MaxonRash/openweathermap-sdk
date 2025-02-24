package com.github.maxonrash.dto.response.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * This class is used in {@link com.github.maxonrash.service.GetGeocodingServiceImpl GetGeocodingServiceImpl}
 * to deserialize received response from API
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetGeocodingResponseDTO {
    private String name;
    @JsonProperty("local_names")
    private Map<String, String> localNames;
    private double lat;
    private double lon;
    private String country;
    private String state;


}
