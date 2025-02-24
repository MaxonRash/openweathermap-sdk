package com.github.maxonrash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maxonrash.dto.response.geocoding.GetGeocodingResponseDTO;
import com.github.maxonrash.dto.response.weather.current.*;
import com.github.maxonrash.entity.CurrentWeatherEntity;
import com.github.maxonrash.exception.CallPerMinuteExceededException;
import com.github.maxonrash.exception.CityWithThisNameIsNotFoundException;
import com.github.maxonrash.exception.InternalErrorException;
import com.github.maxonrash.exception.InvalidApiKeyException;
import com.github.maxonrash.service.GetCurrentWeatherService;
import com.github.maxonrash.service.GetCurrentWeatherServiceImpl;
import com.github.maxonrash.service.GetGeocodingService;
import com.github.maxonrash.service.GetGeocodingServiceImpl;
import com.github.maxonrash.store.StoredCitiesData;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing CurrentWeatherSDK class")
public class CurrentWeatherSDKTest {
    private static String apiKey;
    private static Type onDemandModeType;
    private GetGeocodingService geocodingService;
    private GetCurrentWeatherService currentWeatherService;
    private static CurrentWeatherResponseDTO currentWeatherDTO;
    private static GetGeocodingResponseDTO geocodingResponseDTO;
    @BeforeAll
    public static void initAll() {
        apiKey = "123abcdefghijk456lmnop7890qrstuvw";
        onDemandModeType = Type.ON_DEMAND;

        geocodingResponseDTO = new GetGeocodingResponseDTO();
        geocodingResponseDTO.setLat(55.7522);
        geocodingResponseDTO.setLon(37.6156);

        currentWeatherDTO = CurrentWeatherResponseDTO.builder().coord(new CoordDTO(37.6156, 55.7522))
                .weather(new WeatherDTO[]{new WeatherDTO(100, "Clouds", "broken clouds", "10d")})
                .base("stations")
                .main(new MainDTO(284.2, 282.93, 283.06, 286.82, 1021, 60, 1021, 910))
                .visibility(10000)
                .wind(new WindDTO(4.09, 121, 3.47))
                .rain(new RainDTO(2.73))
                .clouds(new CloudsDTO(83))
                .dt(System.currentTimeMillis()/1000)
                .sys(new SysDTO(1, 6736, "Russia", 1740199086, 1740235699))
                .timezone(10800)
                .id(9876234598L)
                .name("Moscow")
                .cod(200)
                .build();
    }
    @BeforeEach
    public void init() {
        geocodingService = mock(GetGeocodingServiceImpl.class);
        currentWeatherService = mock(GetCurrentWeatherServiceImpl.class);
    }

    @AfterEach
    public void deleteInternalList() {
        CurrentWeatherSDK.deleteAllKeys();
    }

    @Test
    public void whenCreateTwoObjectsWithSameKey_thenReturnSameObject() {
        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        CurrentWeatherSDK sdk2 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        CurrentWeatherSDK sdk3 = CurrentWeatherSDK.create(apiKey, Type.POLLING, geocodingService, currentWeatherService); // Should just change Type in same object
        assertTrue((sdk1 == sdk2) && (sdk2 == sdk3));
    }

    @Test
    public void whenCreateTwoObjectsWithDifferentKeys_thenReturnDifferentObjects() {
        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        CurrentWeatherSDK sdk2 = CurrentWeatherSDK.create(apiKey + "abc", onDemandModeType, geocodingService, currentWeatherService);
        CurrentWeatherSDK sdk3 = CurrentWeatherSDK.create(apiKey + "bcd", Type.POLLING, geocodingService, currentWeatherService);
        assertTrue((sdk1 != sdk2) && (sdk2 != sdk3) && (sdk1 != sdk3));
    }

    @Test
    public void whenObjectWithThisApiKeyAlreadyExists_thenReturnTrue() {
        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        assertAll(
                () -> assertTrue(CurrentWeatherSDK.isObjectWithThisApiKeyAlreadyExists(apiKey)),
                () -> assertFalse(CurrentWeatherSDK.isObjectWithThisApiKeyAlreadyExists(apiKey + "abc"))
        );
    }

    @Test
    public void whenDeleteApiKey_thenDeleteObjectFromInternalList() {
        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        CurrentWeatherSDK.delete(apiKey);
        assertFalse(CurrentWeatherSDK.isObjectWithThisApiKeyAlreadyExists(apiKey));
    }

    @Test
    public void whenSetType_thenTypeForTheObjectIsChanged() {
        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        sdk1.setCurrentModeType(Type.POLLING);
        assertSame(sdk1.getCurrentModeType(), Type.POLLING);
    }

    @Test
    public void whenGetGeocodingByCityName_thenReturnProperString() throws CityWithThisNameIsNotFoundException, CallPerMinuteExceededException, InternalErrorException, InvalidApiKeyException {
        //given
        GetGeocodingResponseDTO[] response = new GetGeocodingResponseDTO[]{geocodingResponseDTO};

        //when
        when(geocodingService.getGeocodingByCityName("Moscow", apiKey)).thenReturn(response);

        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);
        sdk1.getGeocodingInfo("Moscow");


        //then
        double latFromResponse;
        latFromResponse = geocodingService.getGeocodingByCityName("Moscow", apiKey)[0].getLat();
        double lonFromResponse;
        lonFromResponse = geocodingService.getGeocodingByCityName("Moscow", apiKey)[0].getLon();
        double finalLatFromResponse = latFromResponse;
        double finalLonFromResponse = lonFromResponse;
        assertAll(
                () -> assertTrue(Math.abs(response[0].getLat() - finalLatFromResponse) <= 0.0001),
                () -> assertTrue(Math.abs(response[0].getLon() - finalLonFromResponse) <= 0.0001)
        );
    }

    @Test
    public void ifModTypeIsSetToPolling_thenUpdateAllCitiesIsCalled() throws CityWithThisNameIsNotFoundException, CallPerMinuteExceededException, InternalErrorException, InvalidApiKeyException {
        //given
        GetGeocodingResponseDTO[] response = new GetGeocodingResponseDTO[]{geocodingResponseDTO};

        //when
        when(geocodingService.getGeocodingByCityName("Moscow", apiKey)).thenReturn(response);
        when(currentWeatherService.getCurrentWeatherByLatAndLonString("lat=55.7522&lon=37.6156", apiKey)).thenReturn(currentWeatherDTO);

        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, Type.POLLING, geocodingService, currentWeatherService);

        //then
        try (MockedStatic<StoredCitiesData> storeMock = mockStatic(StoredCitiesData.class)) {
            storeMock.when(() -> StoredCitiesData.updateAllCitiesInMemory(apiKey)).thenAnswer((Answer<Void>) invocation -> null);

            sdk1.retrieveCurrentWeatherJSON("Moscow");
            storeMock.verify(() -> StoredCitiesData.updateAllCitiesInMemory(apiKey));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whenRetrieveCurrentWeather_thenReturnProperJson() throws CityWithThisNameIsNotFoundException, CallPerMinuteExceededException, InternalErrorException, InvalidApiKeyException {
        //given
        GetGeocodingResponseDTO[] response = new GetGeocodingResponseDTO[]{geocodingResponseDTO};

        //when
        when(geocodingService.getGeocodingByCityName("Moscow", apiKey)).thenReturn(response);
        when(currentWeatherService.getCurrentWeatherByLatAndLonString("lat=55.7522&lon=37.6156", apiKey)).thenReturn(currentWeatherDTO);

        CurrentWeatherSDK sdk1 = CurrentWeatherSDK.create(apiKey, onDemandModeType, geocodingService, currentWeatherService);

        //then
        try (MockedStatic<StoredCitiesData> storeMock = mockStatic(StoredCitiesData.class)) {
            storeMock.when(()-> StoredCitiesData.isStoredCityWeatherIsUpToDate(response[0].getLat(), response[0].getLon()))
                    .thenReturn(false);

            String json = sdk1.retrieveCurrentWeatherJSON("Moscow");

            CurrentWeatherEntity entity = CurrentWeatherResponseDTO.convertDTOtoEntity(currentWeatherDTO);

            assertEquals(json, new ObjectMapper().writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
