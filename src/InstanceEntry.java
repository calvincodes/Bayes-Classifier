import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InstanceEntry {

    private String[] attributeLabels;
    private HashMap<String, List<String>> possibleAttributeValues;
    private String[] featureValues;
    private String classLabel;
    private String[] allClassLabels;
    private String predictedClassLabel;
    private double predictionConfidence;

    public String[] getAttributeLabels() {
        return attributeLabels;
    }

    public void setAttributeLabels(String[] attributeLabels) {
        this.attributeLabels = attributeLabels;
    }

    public HashMap<String, List<String>> getPossibleAttributeValues() {
        return possibleAttributeValues;
    }

    public void setPossibleAttributeValues(HashMap<String, List<String>> possibleAttributeValues) {
        this.possibleAttributeValues = possibleAttributeValues;
    }

    public String[] getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(String[] featureValues) {
        this.featureValues = featureValues;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public String[] getAllClassLabels() {
        return allClassLabels;
    }

    public void setAllClassLabels(String[] allClassLabels) {
        this.allClassLabels = allClassLabels;
    }

    public String getPredictedClassLabel() {
        return predictedClassLabel;
    }

    public void setPredictedClassLabel(String predictedClassLabel) {
        this.predictedClassLabel = predictedClassLabel;
    }

    public double getPredictionConfidence() {
        return predictionConfidence;
    }

    public void setPredictionConfidence(double predictionConfidence) {
        this.predictionConfidence = predictionConfidence;
    }

    @Override
    public String toString() {
        return "InstanceEntry{" +
                "attributeLabels=" + Arrays.toString(attributeLabels) +
                ", possibleAttributeValues=" + possibleAttributeValues +
                ", featureValues=" + Arrays.toString(featureValues) +
                ", classLabel='" + classLabel + '\'' +
                ", allClassLabels=" + Arrays.toString(allClassLabels) +
                ", predictedClassLabel='" + predictedClassLabel + '\'' +
                ", predictionConfidence=" + predictionConfidence +
                '}';
    }
}
