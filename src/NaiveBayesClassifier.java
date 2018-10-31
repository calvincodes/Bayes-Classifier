import java.util.ArrayList;
import java.util.List;

public class NaiveBayesClassifier {

    private List<FrequencyTable> trainingFrequencies = new ArrayList<>();
    private static boolean IS_TRAINED = false;

    public void trainModel(List<InstanceEntry> trainingData) {

        for (int i = 0; i < trainingData.get(0).getFeatureValues().length; i++) {
            trainingFrequencies.add(i, new FrequencyTable());
        }

        for (InstanceEntry instanceEntry : trainingData) {

            int actualClassLabel = instanceEntry.getClassLabel();

            // Last entry in each row is the class label, hence i < len - 1
            for (int i = 0; i < instanceEntry.getFeatureValues().length - 1; i++) {

                String featureValue = instanceEntry.getFeatureValues()[i];
                if (actualClassLabel == 0) {
                    FrequencyTable frequencyTable = trainingFrequencies.get(i);
                    frequencyTable.getFeatureValue2Class0Freq().put(
                            featureValue, frequencyTable.getFeatureValue2Class0Freq()
                                    .getOrDefault(featureValue, 0) + 1);
                    frequencyTable.setTotalClass0Freq(frequencyTable.getTotalClass0Freq() + 1);
                    frequencyTable.getPossibleFeatureValues().add(featureValue);
                } else {
                    FrequencyTable frequencyTable = trainingFrequencies.get(i);
                    frequencyTable.getFeatureValue2Class1Freq().put(
                            featureValue, frequencyTable.getFeatureValue2Class1Freq()
                                    .getOrDefault(featureValue, 0) + 1);
                    frequencyTable.setTotalClass1Freq(frequencyTable.getTotalClass1Freq() + 1);
                    frequencyTable.getPossibleFeatureValues().add(featureValue);
                }
            }
        }

        IS_TRAINED = true;
    }

    public void classify(InstanceEntry instanceEntry) {

        if (!IS_TRAINED) {
            System.err.println("Please train the model before classification.");
            System.exit(-1);
        }

        // probabilityClass0 = (likelihood*classPriorProbability)/predictorPriorProbability
        double probabilityClass0ProportionalTo = 1d;
        double probabilityClass1ProportionalTo = 1d;

        // Last entry in each row is the class label, hence i < len - 1
        for (int i = 0; i < instanceEntry.getFeatureValues().length - 1; i++) {

            String featureValue = instanceEntry.getFeatureValues()[i];

            FrequencyTable frequencyTable = trainingFrequencies.get(i);

            int featureAndClass0Freq =
                    frequencyTable.getFeatureValue2Class0Freq().getOrDefault(featureValue, 0);
            double totalClass0Freq =
                    frequencyTable.getTotalClass0Freq();
            int featureAndClass1Freq =
                    frequencyTable.getFeatureValue2Class1Freq().getOrDefault(featureValue, 0);
            double totalClass1Freq =
                    frequencyTable.getTotalClass1Freq();

            // Smoothing using Laplace estimates (pseudocounts of 1)
            probabilityClass0ProportionalTo = probabilityClass0ProportionalTo
                    * (
                            (featureAndClass0Freq + 1) /
                                    (totalClass0Freq
                                            + instanceEntry.getPossibleAttributeValues()
                                                .get(instanceEntry.getAttributeLabels()[i])
                                                .size()
                                    )
                    );

            // Smoothing using Laplace estimates (pseudocounts of 1)
            probabilityClass1ProportionalTo = probabilityClass1ProportionalTo
                    * (
                            (featureAndClass1Freq + 1) /
                                    (totalClass1Freq
                                            + instanceEntry.getPossibleAttributeValues()
                                            .get(instanceEntry.getAttributeLabels()[i])
                                            .size()
                                    )
                    );
        }

        // multiplying with probability of class 0
        probabilityClass0ProportionalTo
                = probabilityClass0ProportionalTo
                * (
                            (trainingFrequencies.get(0).getTotalClass0Freq() + 1) /
                                    (double) (trainingFrequencies.get(0).getTotalClass0Freq()
                                                + trainingFrequencies.get(0).getTotalClass1Freq()
                                                + 2)
                    );

        // multiplying with probability of class 1
        probabilityClass1ProportionalTo
                = probabilityClass1ProportionalTo
                * (
                        (trainingFrequencies.get(0).getTotalClass1Freq() + 1) /
                                (double)(trainingFrequencies.get(0).getTotalClass0Freq()
                                        + trainingFrequencies.get(0).getTotalClass1Freq()
                                        + 2)
                    );

        // Finding the normalized probabilities
        double p0 = probabilityClass0ProportionalTo / (probabilityClass0ProportionalTo + probabilityClass1ProportionalTo);
        double p1 = probabilityClass1ProportionalTo / (probabilityClass0ProportionalTo + probabilityClass1ProportionalTo);

        if (p0 > p1) {
            instanceEntry.setPredictedClassLabel(0);
            instanceEntry.setPredictionConfidence(p0);
        } else {
            instanceEntry.setPredictedClassLabel(1);
            instanceEntry.setPredictionConfidence(p1);
        }
    }

    public double[] getProbabilitiesOfClassesProportionalTo(
            InstanceEntry instanceEntry, String featureValue, int featureIndex) {

        double probabilityClass0ProportionalTo = 1d;
        double probabilityClass1ProportionalTo = 1d;

        FrequencyTable frequencyTable = trainingFrequencies.get(featureIndex);

        int featureAndClass0Freq =
                frequencyTable.getFeatureValue2Class0Freq().getOrDefault(featureValue, 0);
        double totalClass0Freq =
                frequencyTable.getTotalClass0Freq();
        int featureAndClass1Freq =
                frequencyTable.getFeatureValue2Class1Freq().getOrDefault(featureValue, 0);
        double totalClass1Freq =
                frequencyTable.getTotalClass1Freq();

        // Smoothing using Laplace estimates (pseudocounts of 1)
        probabilityClass0ProportionalTo = probabilityClass0ProportionalTo
                * (
                (featureAndClass0Freq + 1) /
                        (totalClass0Freq
                                + instanceEntry.getPossibleAttributeValues()
                                .get(instanceEntry.getAttributeLabels()[featureIndex])
                                .size()
                        )
        );

        // Smoothing using Laplace estimates (pseudocounts of 1)
        probabilityClass1ProportionalTo = probabilityClass1ProportionalTo
                * (
                (featureAndClass1Freq + 1) /
                        (totalClass1Freq
                                + instanceEntry.getPossibleAttributeValues()
                                .get(instanceEntry.getAttributeLabels()[featureIndex])
                                .size()
                        )
        );

        return new double[]{probabilityClass0ProportionalTo, probabilityClass1ProportionalTo};
    }
}

