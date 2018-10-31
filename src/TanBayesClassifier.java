import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TanBayesClassifier {

    private NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
    private HashMap<String, String> child2ParentStringMap = new HashMap<>();
    private HashMap<Integer, Integer> child2ParentIndexMap = new HashMap<>();
    private HashMap<String, Double> tanProbabilities = new HashMap<>();
    private Integer freqY0 = 0, freqY1 = 0;
    private static boolean IS_TRAINED = false;

    public void trainModel(List<InstanceEntry> trainingData, boolean print) {

        naiveBayesClassifier.trainModel(trainingData);

        int featureCount = trainingData.get(0).getFeatureValues().length - 1; // Removing 1, as last entry is class label.
        String[] attributeLabels = trainingData.get(0).getAttributeLabels();

        Double[][] edgeWeights = computeEdgeWeights(trainingData);
        // Get parent for each node using prim's algorithm
        int[] parent = getParentForEachNode(edgeWeights, featureCount);
        for (int i = 0; i < featureCount; i++) {
            String parentNode = parent[i] != -1 ? attributeLabels[parent[i]] : "class";
            child2ParentStringMap.put(attributeLabels[i], parentNode);
            child2ParentIndexMap.put(i, parent[i]);
        }

        calculateTanProbabilities(trainingData);

        if (print) {
            for (int i = 0; i < featureCount; i++) {
                System.out.print(attributeLabels[i] + " " + child2ParentStringMap.get(attributeLabels[i]));
                if (!"class".equals(child2ParentStringMap.get(attributeLabels[i]))) {
                    System.out.print(" class");
                }
                System.out.print("\n");
            }
            System.out.print("\n");
        }

        IS_TRAINED = true;
    }

    public void classify(InstanceEntry instanceEntry) {

        if (!IS_TRAINED) {
            System.err.println("Please train the model before classification.");
            System.exit(-1);
        }

        int featureCount = instanceEntry.getFeatureValues().length - 1; // Removing 1, as last entry is class label.
        String[] attributeLabels = instanceEntry.getAttributeLabels();

        double probabilityClass0ProportionalTo = 1d;
        double probabilityClass1ProportionalTo = 1d;
        for (int i = 0; i < featureCount; i++) {
            String childNode = attributeLabels[i];
            String childVal = instanceEntry.getFeatureValues()[i];
            int parentOfNode_i = child2ParentIndexMap.get(i);
            if (parentOfNode_i != -1) {
                String parentNode = attributeLabels[parentOfNode_i];
                String parentVal = instanceEntry.getFeatureValues()[parentOfNode_i];
                probabilityClass0ProportionalTo
                        *= tanProbabilities.get(childNode + "=" + childVal + "|" + parentNode + "=" + parentVal + ",y=0");
                probabilityClass1ProportionalTo
                        *= tanProbabilities.get(childNode + "=" + childVal + "|" + parentNode + "=" + parentVal + ",y=1");
            } else {
                double[] naivePobabilityOfClassesProportionalTo
                        = naiveBayesClassifier.getProbabilitiesOfClassesProportionalTo(instanceEntry, childVal, i);
                probabilityClass0ProportionalTo
                        *= naivePobabilityOfClassesProportionalTo[0];
                probabilityClass1ProportionalTo
                        *= naivePobabilityOfClassesProportionalTo[1];
            }
        }

        // multiplying with probability of class 0
        probabilityClass0ProportionalTo
                = probabilityClass0ProportionalTo
                * (
                (freqY0 + 1) /
                        (double) (freqY0 + freqY1 + 2)
        );

        // multiplying with probability of class 1
        probabilityClass1ProportionalTo
                = probabilityClass1ProportionalTo
                * (
                (freqY1+ 1) /
                        (double)(freqY0 + freqY1 + 2)
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

    private void calculateTanProbabilities(List<InstanceEntry> trainingData) {

        int featureCount = trainingData.get(0).getFeatureValues().length - 1; // Removing 1, as last entry is class label.
        String[] attributeLabels = trainingData.get(0).getAttributeLabels();
        HashMap<String, List<String>> possibleAttributeValues = trainingData.get(0).getPossibleAttributeValues();

        for (int i = 0; i < featureCount; i++) {
            String childNode = attributeLabels[i];
            int parentOfNode_i = child2ParentIndexMap.get(i);
            if (parentOfNode_i != -1) {
                String parentNode = attributeLabels[parentOfNode_i];

                for (String childVal : possibleAttributeValues.get(childNode)) {
                    for (String parentVal : possibleAttributeValues.get(parentNode)) {

                        int freqChildValParentValY0 = getFreqXiXjY(trainingData, i, parentOfNode_i, childVal, parentVal, 0);
                        int freqParentValY0 = getFreqXiY(trainingData, parentOfNode_i, parentVal, 0);
                        double probChildVal_given_parentValAndY0
                                = (freqChildValParentValY0 + 1)
                                    /
                                // Why is this smoothing taking attributeList size of child and not parent?
                                (double)(freqParentValY0 + possibleAttributeValues.get(childNode).size());
                        String keyY0 = childNode + "=" + childVal + "|" + parentNode + "=" + parentVal + ",y=0";
                        tanProbabilities.put(keyY0, probChildVal_given_parentValAndY0);

                        int freqChildValParentValY1 = getFreqXiXjY(trainingData, i, parentOfNode_i, childVal, parentVal, 1);
                        int freqParentValY1 = getFreqXiY(trainingData, parentOfNode_i, parentVal, 1);
                        double probChildVal_given_parentValAndY1
                                = (freqChildValParentValY1 + 1)
                                /
                                // Why is this smoothing taking attributeList size of child and not parent?
                                (double)(freqParentValY1 + possibleAttributeValues.get(childNode).size());
                        String keyY1 = childNode + "=" + childVal + "|" + parentNode + "=" + parentVal + ",y=1";
                        tanProbabilities.put(keyY1, probChildVal_given_parentValAndY1);
                    }
                }
            }
        }
    }

    private int[] getParentForEachNode(Double[][] edgeWeights, int featureCount) {

        double[] allCosts = new double[featureCount];
        int[] prev = new int[featureCount];
        for (int i = 0; i < featureCount; i++) {
            allCosts[i] = -1;
            prev[i] = -1;
        }

        allCosts[0] = 0; // Initialize with vertex 0.
        Set<Integer> visitedNodes = new HashSet<>();
        while (visitedNodes.size() != featureCount) {

            double maxCost = Integer.MIN_VALUE;

            int nodeWithMaxCost = -1;
            for (int i = 0; i < featureCount; i++) {
                if (!visitedNodes.contains(i)) {

                    if (allCosts[i] > maxCost) {
                        maxCost = allCosts[i];
                        nodeWithMaxCost = i;
                    }
                    // Tie Breaker Conditions
                    // If there are ties in selecting maximum weight edges, use the following preference criteria:
                    // (1) prefer edges emanating from variables listed earlier in the input file,
                    // (2) if there are multiple maximum weight edges emanating from the first such variable,
                    // prefer edges going to variables listed earlier in the input file.
                    else if (allCosts[i] == maxCost) {
                        if (i < nodeWithMaxCost) {
                            maxCost = allCosts[i];
                            nodeWithMaxCost = i;
                        }
                    }

                }
            }
            visitedNodes.add(nodeWithMaxCost);

            for (int j = 0; j < featureCount; j++) {
                if (j != nodeWithMaxCost && !visitedNodes.contains(j)) {
                    if (allCosts[j] < edgeWeights[nodeWithMaxCost][j]) {
                        allCosts[j] = edgeWeights[nodeWithMaxCost][j];
                        prev[j] = nodeWithMaxCost;
                    }
                }
            }
        }

        return prev;
    }

    private Double[][] computeEdgeWeights(List<InstanceEntry> trainingData) {

        int featureCount = trainingData.get(0).getFeatureValues().length - 1; // Removing 1, as last entry is class label.
        Double[][] edgeWeights = new Double[featureCount][featureCount];
        for (int i = 0; i < featureCount; i++) {
            for (int j = 0; j < featureCount; j++) {
                edgeWeights[i][j] = 0d;
            }
        }

        String[] attributeLabels = trainingData.get(0).getAttributeLabels();
        HashMap<String, List<String>> possibleAttributeValues = trainingData.get(0).getPossibleAttributeValues();

        int[] classLabels = new int[]{0,1};
        for (int classLabel : classLabels) {

        }

        for (InstanceEntry trainingEntry : trainingData) {
            if (trainingEntry.getClassLabel() == 0) {
                freqY0++;
            } else {
                freqY1++;
            }
        }

        for (int i = 0; i < featureCount; i++) {
            for (int j = 0; j < featureCount; j++) {

                if (i == j) {
                    continue;
                }

                List<String> possibleAttributeValues_i = possibleAttributeValues.get(attributeLabels[i]);
                List<String> possibleAttributeValues_j = possibleAttributeValues.get(attributeLabels[j]);

                for (String attribute_i : possibleAttributeValues_i) {
                    for (String attribute_j : possibleAttributeValues_j) {

                        int freqXiXjY0 = getFreqXiXjY(trainingData, i, j, attribute_i, attribute_j, 0);
                        double probXiXjY0 =
                                (freqXiXjY0 + 1) // Smoothing using Laplace estimates (pseudo-counts of 1)
                                        /
                                        (double) (
                                                trainingData.size()
                                                        // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                        + (possibleAttributeValues_i.size()
                                                            * possibleAttributeValues_j.size()
                                                            * 2
                                                        )
                                        );

                        int freqXiXjY1 = getFreqXiXjY(trainingData, i, j, attribute_i, attribute_j, 1);
                        double probXiXjY1 =
                                (freqXiXjY1 + 1) // Smoothing using Laplace estimates (pseudo-counts of 1)
                                        /
                                        (double) (
                                                trainingData.size()
                                                        // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                        + (possibleAttributeValues_i.size()
                                                            * possibleAttributeValues_j.size()
                                                            * 2
                                                        )
                                        );

                        double probXiXjGivenY0 =
                                (freqXiXjY0 + 1)
                                        /
                                        (double) (
                                                freqY0
                                                    // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                    + (possibleAttributeValues_i.size()
                                                        * possibleAttributeValues_j.size()
                                                        )
                                                );

                        double probXiXjGivenY1 =
                                (freqXiXjY1 + 1)
                                        /
                                        (double) (
                                                freqY1
                                                        // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                        + (possibleAttributeValues_i.size()
                                                            * possibleAttributeValues_j.size()
                                                            )
                                        );

                        double probXiGivenY0 =
                                (getFreqXiY(trainingData, i, attribute_i, 0) + 1)
                                        /
                                        (double) (freqY0
                                                    // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                    + possibleAttributeValues_i.size());

                        double probXiGivenY1 =
                                (getFreqXiY(trainingData, i, attribute_i, 1) + 1)
                                        /
                                        (double) (freqY1
                                                // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                + possibleAttributeValues_i.size());

                        double probXjGivenY0 =
                                (getFreqXiY(trainingData, j, attribute_j, 0) + 1)
                                        /
                                        (double) (freqY0
                                                // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                + possibleAttributeValues_j.size());

                        double probXjGivenY1 =
                                (getFreqXiY(trainingData, j, attribute_j, 1) + 1)
                                        /
                                        (double) (freqY1
                                                // Smoothing using Laplace estimates (pseudo-counts of 1)
                                                + possibleAttributeValues_j.size());

                        edgeWeights[i][j] += probXiXjY0
                                                *
                                            (Math.log(probXiXjGivenY0/(probXiGivenY0*probXjGivenY0))
                                                    / Math.log(2));

                        edgeWeights[i][j] += probXiXjY1
                                                *
                                            (Math.log(probXiXjGivenY1/(probXiGivenY1*probXjGivenY1))
                                                    / Math.log(2));
                    }
                }

            }
        }

        return edgeWeights;
    }

    private int getFreqXiXjY(List<InstanceEntry> trainingData,
                             int i, int j, String attribute_i, String attribute_j, int classLabel) {

        int freqXiXjY = 0;
        for (InstanceEntry trainingEntry : trainingData) {
            if (trainingEntry.getFeatureValues()[i].equals(attribute_i)
                    && trainingEntry.getFeatureValues()[j].equals(attribute_j)
                    && (trainingEntry.getClassLabel() == classLabel)
            ) {
                freqXiXjY++;
            }
        }

        return freqXiXjY;
    }

    private int getFreqXiY(List<InstanceEntry> trainingData, int i, String attribute_i, int classLabel) {

        int freqXiY = 0;
        for (InstanceEntry trainingEntry : trainingData) {
            if (trainingEntry.getFeatureValues()[i].equals(attribute_i)
                    && (trainingEntry.getClassLabel() == classLabel)
            ) {
                freqXiY++;
            }
        }

        return freqXiY;
    }
}
