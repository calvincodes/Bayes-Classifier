import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Driver {

    private static ArffFileReader arffFileReader  = new ArffFileReader();
    private static DecimalFormat df = new DecimalFormat("0.000000");

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

        List<InstanceEntry> allTrainingData = arffFileReader.readArff(trainingFile);
        int allTrainingDataSize = allTrainingData.size();

        long endTime = System.nanoTime();
        System.out.println("Took "+ (double)(endTime - startTime) / 1000000000.0 + " s");
    }
}
