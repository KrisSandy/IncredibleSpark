package WeatherStations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

// WeatherStation Class
//----------------------

// This class consists of three attributes.
// 1. city : City of the weather station
// 2. measurements: this attribute is an ArrayList of type Measurement.
//    It will hold all the measurements of a weather station
// 3. stations: An ArrayList of type WeatherStation.

public class WeatherStation implements java.io.Serializable {

    private String city;
    private List<Measurement> measurements;
    public static List<WeatherStation> stations;

//    Below constructor accepts city and list of measurements as parameters and
//    assigns them to the corresponding class fields.

    public WeatherStation(String city, List<Measurement> measurements) {
        this.city = city;
        this.measurements = measurements;
    }

//    Below method accepts a temperature t and calculates the number of times the
//    temperature reading appeared approximately (t-1, t+1) using Spark and returns it.

    public static long countTemperature(double t) {

//        Set the configuration required the Spark
//        1. AppName - Name of the Application
//        2. Master - host name of the Spark Master (local for local mode) and number of cores

        SparkConf sparkConf = new SparkConf()
                .setAppName("WeatherStation")
                .setMaster("local[3]");

//        Setup SparkContext using above spark configuration
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

//        Load the stations data into a Spark RDD
        JavaRDD<WeatherStation> data = jsc.parallelize(stations);

//        Calculate the count of approximate temperature reading as below:
//        1. Flatten the measurements from stations
//        2. Extract the temperatures from measurements
//        3. Filter the temperatures which lie between t-1 and t+1
//        4. Count the temperature values filtered.
        long count = data
                .flatMap(s -> s.measurements.iterator())
                .map(m -> m.getTemperature())
                .filter(temp -> (temp >= t-1 && temp <= t+1))
                .count();

//        Shutdown the SparkContext
        jsc.stop();
        jsc.close();
        return count;
    }

}
