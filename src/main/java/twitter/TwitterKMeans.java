package Assignment4;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

public class TwitterKMeans {

    public static void main(String[] args) {

//        Set the configuration required the Spark
//        1. AppName - Name of the Application
//        2. Master - host name of the Spark Master (local for local mode) and number of cores

        SparkConf sparkConf = new SparkConf()
                .setAppName("TwitterKMeans")
                .setMaster("local[3]");

//        Setup SparkContext using above spark configuration
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

//        Read the twitter 2D text file into Spark RDD.
//        Each line is a comma separated row with coordinates in first two columns,
//        time stamp, user id, flag to indicate spam/no spam (1/0), and actual tweet.
        JavaRDD<String> textData = jsc.textFile("twitter2D.txt");

//        Create a Vector of co-ordinates from each line by splitting the line using ',' separator
//        and creating a Vector with 1st two columns.
        JavaRDD<Vector> coordinates = textData.map(l -> {
            String[] items = l.split(",");
            return Vectors.dense(Double.parseDouble(items[0]), Double.parseDouble(items[1]));
        });

//        Cache the coordinates dataset
        coordinates.cache();

//        Set Number of clusters to 4 and
//        Number of Iterations to 20
        int numClusters = 4;
        int numIterations = 20;


//        Train the KMeans model using the coordinates dataset to find the centroids
//        of each clusters. Number of clusters is set to 4 and Number of Iterations
//        is set to 20.
        KMeansModel model = KMeans.train(coordinates.rdd(), numClusters, numIterations);

//        predict method of the model will accepts a vector of coordinates and returns the cluster
//        index.
//        To print the output, input data is iterated, split into array of Strings by ',',
//        for each pair of coordinate in the data, call predict method to get the cluster.
        textData.foreach(l -> {
            String[] items = l.split(",");
            int cluster = model.predict(Vectors.dense(Double.parseDouble(items[0]), Double.parseDouble(items[1])));
            System.out.println("Tweet \"" + items[items.length-1] + "\" is in cluster " + cluster);
        });

//        Shutdown the SparkContext
        jsc.stop();
        jsc.close();
    }
}
