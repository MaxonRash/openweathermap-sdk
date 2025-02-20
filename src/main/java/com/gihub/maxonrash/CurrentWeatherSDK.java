package com.gihub.maxonrash;

import com.gihub.maxonrash.service.GetGeocodingService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CurrentWeatherSDK {
    /*private*/ protected static List<CurrentWeatherSDK> currentWeatherSDKList;
    private String apiKey;
//    private String cityName;

    private CurrentWeatherSDK() {}
    private CurrentWeatherSDK(String apiKey/*, String cityName*/) {
        this.apiKey = apiKey;
//        this.cityName = cityName;
    }

    public static CurrentWeatherSDK create(String apiKey/*, String cityName*/) {
        if (currentWeatherSDKList == null) {
            currentWeatherSDKList = new ArrayList<>();
        }
        for (CurrentWeatherSDK obj : currentWeatherSDKList) {
            if (obj.apiKey.equals(apiKey)) {
                return obj;
            }
        }
        CurrentWeatherSDK currentWeatherSDK = new CurrentWeatherSDK(apiKey/*, cityName*/);
        CurrentWeatherSDK.currentWeatherSDKList.add(currentWeatherSDK);
        return currentWeatherSDK;
    }

    public void delete(String apiKey) {

    }

    public String getInfo(String cityName) {
        GetGeocodingService service = new GetGeocodingService();
        var info = service.getGeocodingByCityNameService(cityName, this.apiKey);
        return String.valueOf(info[0].getLat());
    }

    @Override
    public String toString() {
        return "CurrentWeatherSDK{" +
                "apiKey='" + apiKey + '\'' +
                '}';
    }
}
