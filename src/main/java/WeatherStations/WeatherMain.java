package WeatherStations;

import java.util.Arrays;

public class WeatherMain {

    public static void main(String[] args) {

//      create sample WeatherStations with some test readings.

        WeatherStation weatherStationA = new WeatherStation("GALWAY", Arrays.asList(
                new Measurement(10, 26.4),
                new Measurement(11, 28.1),
                new Measurement(12, 29.0),
                new Measurement(13, 31.3),
                new Measurement(14, 32.8),
                new Measurement(15, 34.1),
                new Measurement(16, 33.3),
                new Measurement(17, 30.2),
                new Measurement(18, 29.7)));

        WeatherStation weatherStationB = new WeatherStation("DUBLIN", Arrays.asList(
                new Measurement(10, 23.4),
                new Measurement(11, 24.1),
                new Measurement(12, 26.0),
                new Measurement(13, 30.3),
                new Measurement(14, 33.2),
                new Measurement(15, 32.2),
                new Measurement(16, 33.7)));

        WeatherStation weatherStationC = new WeatherStation("GALWAY", Arrays.asList(
                new Measurement(10, 20.0),
                new Measurement(11, 11.7),
                new Measurement(12, -5.4),
                new Measurement(13, 18.7),
                new Measurement(14, 20.9)));

        WeatherStation weatherStationD = new WeatherStation("DUBLIN", Arrays.asList(
                new Measurement(10, 8.4),
                new Measurement(11, 19.2),
                new Measurement(12, 7.2)));

//        Add WeatherStation objects to stations.

        WeatherStation.stations = Arrays.asList(weatherStationA, weatherStationB, weatherStationC, weatherStationD);

//        Testing

//        Positive cases
        System.out.println("Count of temperature 30 approximately measured : " + WeatherStation.countTemperature(30));
        System.out.println("Count of temperature 33 approximately measured : " + WeatherStation.countTemperature(33));

//        Negative case - with zero matches
        System.out.println("Count of temperature -2 approximately measured : " + WeatherStation.countTemperature(-2));
    }
}
