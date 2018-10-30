package basics;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

public class SVMSample {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf()
                .setAppName("imdb")
                .setMaster("local[3]");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), "sample_libsvm_data.txt").toJavaRDD();
        JavaRDD<LabeledPoint> train = data.sample(false, 0.6, 99L);
        train.cache();
        JavaRDD<LabeledPoint> test = data.subtract(train);

        train.take(10).forEach(System.out::println);

        int numIterators = 100;
        SVMModel svmModel = SVMWithSGD.train(train.rdd(), numIterators);

        svmModel.clearThreshold();

        JavaRDD<Tuple2<Object, Object>> scoresAndLabels = test.map(
                p -> new Tuple2<>(svmModel.predict(p.features()), p.label())
        );

        JavaRDD<Tuple2<Object, Object>> results = scoresAndLabels
                .map(s -> {
                    double result = 0.0;
                    if ((double)s._1 > 0.0) {
                        result = 1.0;
                    }
                    return new Tuple2<>(result, s._2);
                });
        scoresAndLabels.take(5).forEach(System.out::println);
        results.take(5).forEach(System.out::println);

    }

}
