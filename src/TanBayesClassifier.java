import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TanBayesClassifier {

    private List<FrequencyTable> trainingFrequencies = new ArrayList<>();
    private HashMap<String, String> child2ParentMap = new HashMap<>();
    private static boolean IS_TRAINED = false;

    public void trainModel(List<InstanceEntry> trainingData) {

        int featureCount = trainingData.get(0).getFeatureValues().length - 1; // Removing 1, as last entry is class label.
        String[] attributeLabels = trainingData.get(0).getAttributeLabels();
        for (int i = 0; i < trainingData.get(0).getFeatureValues().length; i++) {
            trainingFrequencies.add(i, new FrequencyTable());
        }

        Double[][] edgeWeights = computeEdgeWeights(trainingData);
        // Get parent for each node using prim's algorithm
        int[] parent = getParentForEachNode(edgeWeights, featureCount);
        for (int i = 0; i < featureCount; i++) {
            String parentNode = parent[i] != -1 ? attributeLabels[parent[i]] : "class";
            child2ParentMap.put(attributeLabels[i], parentNode);
        }

        for (int i = 0; i < featureCount; i++) {
            System.out.print(attributeLabels[i] + " " + child2ParentMap.get(attributeLabels[i]));
            if (!"class".equals(child2ParentMap.get(attributeLabels[i]))) {
                System.out.print(" class");
            }
            System.out.print("\n");
        }

        IS_TRAINED = true;
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

        int freqY0 = 0;
        int freqY1 = 0;
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
