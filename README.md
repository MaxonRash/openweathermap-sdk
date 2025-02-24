### Intro

This SDK can be used for obtaining current weather information and cities' geocodes in JSON format from <a href="https://openweathermap.org/">OpenWeatherMap.org</a> or another resource if you use not a "default" implementation of `GetCurrentWeatherService` or `GetGeocodingService`

### Installation

*Maven*

```xml
<dependency>
    <groupId>com.github.maxonrash</groupId>
    <artifactId>openweathermap-sdk</artifactId>
    <version>1.1.2</version>
</dependency>
```

Note: to be able to use dependency GitHub requires a private key to be set in `settings.xml` in your maven directory. On Windows OS it should be something like this - `C:\Users\username\.m2`. Content:  
```xml
<settings>
<servers>
    <server>
        <id>github</id>
	<username>{your_username}</username>
	<password>{your_private_key}</password>
    </server>
</servers>
</settings>
```

### Usage

- Create an instance of `CurrentWeatherSDK`  
- Use method `retrieveCurrentWeatherJSON(cityName)` to receive current weather information for specified city  
- Use method `getGeocodingInfoJSON(cityName)` to receive geocoding info for specified city

A sample project with detailed examples can be found here: <a href="https://github.com/MaxonRash/openweathermap-sdk-usage-example">Sample project</a> 