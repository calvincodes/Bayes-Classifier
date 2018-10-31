import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrossValidationDriver {

    private static ArffFileReader arffFileReader = new ArffFileReader();
    private static NFoldStratifiedHelper nFoldStratifiedHelper = new NFoldStratifiedHelper();

    // TODO: must comment this out before submission.
    public static void main_1(String[] args) {

        String arffFile = "chess-KingRookVKingPawn.arff";
        List<InstanceEntry> allTrainingData = arffFileReader.readArff(arffFile);
        int numFolds = 10;

        HashMap<Integer, List<InstanceEntry>> nFoldedInstanceEntries = new HashMap<>();
        nFoldStratifiedHelper.performNFoldStratifiedCrossValidation(
                allTrainingData, numFolds, nFoldedInstanceEntries);

        for (int i = 0; i < numFolds; i++) {

            NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
            TanBayesClassifier tanBayesClassifier = new TanBayesClassifier();

            int correctNaiveClassifications = 0;
            int correctTanClassifications = 0;
            List<InstanceEntry> trainingData = new ArrayList<>();
            List<InstanceEntry> testDataNaive = new ArrayList<>();
            List<InstanceEntry> testDataTan = new ArrayList<>();
            for (int j = 0; j < numFolds; j++) {
                if (i == j) {
                    testDataNaive.addAll(nFoldedInstanceEntries.get(j));
                    testDataTan.addAll(nFoldedInstanceEntries.get(j));
                } else {
                    trainingData.addAll(nFoldedInstanceEntries.get(j));
                }
            }

            naiveBayesClassifier.trainModel(trainingData);
            tanBayesClassifier.trainModel(trainingData, false);

            for (int m = 0; m < testDataNaive.size(); m++) {
                naiveBayesClassifier.classify(testDataNaive.get(m));
                tanBayesClassifier.classify(testDataTan.get(m));

                if (testDataNaive.get(m).getClassLabel() == testDataNaive.get(m).getPredictedClassLabel()) {
                    correctNaiveClassifications++;
                }
                if (testDataTan.get(m).getClassLabel() == testDataTan.get(m).getPredictedClassLabel()) {
                    correctTanClassifications++;
                }
            }

            System.out.println(correctNaiveClassifications/(double)trainingData.size());
            System.out.println(correctTanClassifications/(double)trainingData.size());
            System.out.println();

        }

//        for (List<InstanceEntry> nFoldedInstance : nFoldedInstanceEntries.values()) {
//            int y0 = 0;
//            int y1 = 0;
//            for (InstanceEntry instanceEntry : nFoldedInstance) {
//                if (instanceEntry.getClassLabel() == 0) {
//                    y0++;
//                } else {
//                    y1++;
//                }
//            }
//            System.out.println(y0 + " " + y1 + "\n");
//        }

    }
}
