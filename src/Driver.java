import java.util.List;

public class Driver {

    private static ArffFileReader arffFileReader  = new ArffFileReader();
    private static NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
    private static Printer printer = new Printer();

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        if (args.length != 3) {
            System.err.println("Usage bayes <train-set-file> <test-set-file> <n|t>");
            System.exit(-1);
        }

        String trainingFile = args[0];
        String testFile = args[1];
        String modelType = args[2];

        /* ****************************************************************** */

        // Step 1: Read the ARFF file

        List<InstanceEntry> trainingData = arffFileReader.readArff(trainingFile);
        List<InstanceEntry> testData = arffFileReader.readArff(testFile);

        /* ****************************************************************** */
        /* ****************************************************************** */

        switch (modelType) {
            case "n":

                naiveBayesClassifier.trainModel(trainingData);

                for (InstanceEntry testEntry : testData) {
                    naiveBayesClassifier.classify(testEntry);
                }

                printer.printResults(modelType, trainingData);

                break;
            case "t":

                break;
            default:
                System.err.println("Model type can be n or t");
                break;
        }


        long endTime = System.nanoTime();
        System.out.println("Took "+ (double)(endTime - startTime) / 1000000000.0 + " s");
    }
}
