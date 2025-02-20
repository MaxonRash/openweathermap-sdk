package com.gihub.maxonrash.dto.response.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CityResponseDTO {
    private String name;
    private Map<String, String> local_names;
    private double lat;
    private double lon;
    private String country;
    private String state;

}
