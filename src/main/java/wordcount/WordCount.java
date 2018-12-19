package basics;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Comparator;

class ValueComparator implements Comparator<Tuple2<String, Integer>>, java.io.Serializable {

    @Override
    public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
        return o2._2.compareTo(o1._2);
    }
}

public class WordCount {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf().setAppName("WordCount").setMaster("local[4]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        JavaRDD<String> textFile = sc.textFile("src/main/resources/holbrook.txt");
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey(Integer::sum);

        counts.takeOrdered(10, new ValueComparator())
                .forEach(System.out::println);

    }

}
