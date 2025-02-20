package com.gihub.maxonrash.dto.response.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponseDTO {
    private List<CityResponseDTO> cities;
}
