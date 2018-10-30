package WeatherStations;

// Measurement Class
//----------------------

// This class consists of two attributes.
// 1. time : an integer, representing the time of measurement.
// 2. temperature: a double number, representing the reading.


public class Measurement implements java.io.Serializable {

    private int time;
    private double temperature;

//  Below constructor accepts time and temperature as parameters and assigns
//  them to the class fields

    public Measurement(int time, double temperature) {
        this.time = time;
        this.temperature = temperature;
    }

//  Getter and setter methods for attributes of measurement class.

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "time=" + time +
                ", temperature=" + temperature +
                '}';
    }
}
