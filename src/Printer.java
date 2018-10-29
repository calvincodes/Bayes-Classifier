import java.text.DecimalFormat;
import java.util.List;

public class Printer {

    private static DecimalFormat df = new DecimalFormat("0.000000000000");

    public void printResults(String algorithmType, List<InstanceEntry> classifiedTestData) {

        if (algorithmType.equals("n")) {
            printNaiveBayes(classifiedTestData);
        }
    }

    private void printNaiveBayes(List<InstanceEntry> testData) {

        for (int i = 0; i < testData.get(0).getAttributeLabels().length - 1; i++) {
            System.out.println(testData.get(0).getAttributeLabels()[i] + " class");

        }
        System.out.println();

        int correctClassification = 0;
        for (InstanceEntry testEntry : testData) {
            System.out.println(
                    testEntry.getPredictedClassLabelStr() + " "
                            + testEntry.getClassLabelStr() + " "
                            + df.format(testEntry.getPredictionConfidence()));
            if (testEntry.getClassLabel() == testEntry.getPredictedClassLabel()) {
                correctClassification++;
            }
        }
        System.out.println("\n" + correctClassification + "\n");
    }
}
