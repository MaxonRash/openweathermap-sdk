### Intro

This SDK can be used for obtaining current weather information and cities' geocodes in JSON format from <a href="https://openweathermap.org/">OpenWeatherMap.org</a> or another resource if you use not a "default" implementation of `GetCurrentWeatherService` or `GetGeocodingService`

### Installation

*Maven*

```xml
<dependency>
    <groupId>com.github.maxonrash</groupId>
    <artifactId>openweathermap-sdk</artifactId>
    <version>1.0.4</version>
</dependency>
```

### Usage

- Create an instance of `CurrentWeatherSDK`  
- Use method `retrieveCurrentWeatherJSON(cityName)` to receive current weather information for specified city  
- Use method `getGeocodingInfoJSON(cityName)` to receive geocoding info for specified city