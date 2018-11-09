import java.util.List;

public class Driver {

    private static ArffFileReader arffFileReader  = new ArffFileReader();
    private static NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
    private static TanBayesClassifier tanBayesClassifier = new TanBayesClassifier();
    private static Printer printer = new Printer();

    public static void main(String[] args) {

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

                printer.printResults(modelType, testData);

                break;
            case "t":

                tanBayesClassifier.trainModel(trainingData);

                for (InstanceEntry testEntry : testData) {
                    tanBayesClassifier.classify(testEntry);
                }

                printer.printResults(modelType, testData);

                break;
            default:
                System.err.println("Model type can be n or t");
                System.exit(-1);
                break;
        }
    }
}
