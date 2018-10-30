package basics;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

public class StartUp {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("StartUp").setMaster("local[4]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        List<Integer> data = Arrays.asList(1,2,3,4,5,6);
        JavaRDD<Integer> distData = sc.parallelize(data);
        distData.reduce((a,b) -> a + b);
        distData.collect().forEach(System.out::println);
        sc.stop();
        sc.close();
    }

}
