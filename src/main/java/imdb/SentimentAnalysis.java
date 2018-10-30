package imdb;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

import java.util.Arrays;

public class SentimentAnalysis {

    public static void main(String[] args) {

//        Set the configuration required the Spark
//        1. AppName - Name of the Application
//        2. Master - host name of the Spark Master (local for local mode) and number of cores

        SparkConf sparkConf = new SparkConf()
                .setAppName("SentimentAnalysis")
                .setMaster("local[3]");

//        Setup SparkContext using above spark configuration
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

//        Read the imdb labelled data text file into Spark RDD.
//        Each review in the file is a line and it consists of review text and a number indicating
//        positive or negative sentiment (0 -> negative and 1 -> positive)
        JavaRDD<String> textData = jsc.textFile("imdb_labelled.txt");

//        HashingTF maps a sequence of terms to their term frequencies using hashing.
//        This is used to transform the reviews text to a sparse Vector.
//        131072 (2^17) indicate number of features

        final HashingTF tf = new HashingTF(131072);

//        Convert the data into LabeledPoint
//        Input for the LabeledPoint is label (double) and words converted to Vector (features)
//        As each record in input file is separated with tab and label is present at the end,
//        1. Extracted the sentiment (0 or 1) and parsed it into double.
//        2. Extracted the review, split into words and transformed into Vector of features.
        JavaRDD<LabeledPoint> data = textData
                .map(s -> new LabeledPoint(Double.parseDouble(s.split("\t")[1]), tf.transform(Arrays.asList(s.split("\t")[0].split(" ")))));

//        Split the data into training and test with 60-40 split.
//        1. Use sample function to extract a fraction of 0.6 into train
//        2. Cache the training dataset
//        3. Create test set by subtracting the training set.
        JavaRDD<LabeledPoint> train = data.sample(false, 0.6, 10L);
        train.cache();
        JavaRDD<LabeledPoint> test = data.subtract(train);

//        Train the SVM mode with the training data. The train method of SVMWithSGD accepts scala RDD object,
//        hence rdd() method is called to convert Java RDD to scala RDD.

//        GDIterations - number of iterations of gradient descent to run.
        int iterations = 100;
        final SVMModel model = SVMWithSGD.train(train.rdd(), iterations);

//        Use the test data set to predict the sentiment (positive or negative)
        JavaRDD<Tuple2<Object, Object>> predictedAndActual = test
                .map(p -> new Tuple2<>(model.predict(p.features()), p.label()));

//        Print labels (sentiments) of first 10 movies
        predictedAndActual.take(10).forEach(
                p -> System.out.println("Predicted: " + p._1 + " Actual: " + p._2));

//        ** Question b **

//        Clear the default threshold so that SVM will output raw prediction score.
        model.clearThreshold();

//        Use the test data set to predict the raw score.
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test
                .map(p -> new Tuple2<>(model.predict(p.features()), p.label()));

//        Use the Binary classification metrics to evaluate the model i.e.
//        calculate the Area Under the ROC curve
        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
//        Calculate the area under ROC curve
        double auROC = metrics.areaUnderROC();

//        Print the Area Under the ROC curve
        System.out.println("Area Under the ROC Curve (AUROC) : " + auROC);

//        Shutdown the SparkContext
        jsc.stop();
        jsc.close();
    }
}
