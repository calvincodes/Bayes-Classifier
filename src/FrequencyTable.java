import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FrequencyTable {

    private Set<String> possibleFeatureValues = new HashSet();
    private String[] featureValues;
    private HashMap<String, Integer> featureValue2Class0Freq = new HashMap<>();
    private HashMap<String, Integer> featureValue2Class1Freq = new HashMap<>();
    private long totalClass0Freq;
    private long totalClass1Freq;

    public Set<String> getPossibleFeatureValues() {
        return possibleFeatureValues;
    }

    public void setPossibleFeatureValues(Set<String> possibleFeatureValues) {
        this.possibleFeatureValues = possibleFeatureValues;
    }

    public String[] getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(String[] featureValues) {
        this.featureValues = featureValues;
    }

    public HashMap<String, Integer> getFeatureValue2Class0Freq() {
        return featureValue2Class0Freq;
    }

    public void setFeatureValue2Class0Freq(HashMap<String, Integer> featureValue2Class0Freq) {
        this.featureValue2Class0Freq = featureValue2Class0Freq;
    }

    public HashMap<String, Integer> getFeatureValue2Class1Freq() {
        return featureValue2Class1Freq;
    }

    public void setFeatureValue2Class1Freq(HashMap<String, Integer> featureValue2Class1Freq) {
        this.featureValue2Class1Freq = featureValue2Class1Freq;
    }

    public long getTotalClass0Freq() {
        return totalClass0Freq;
    }

    public void setTotalClass0Freq(long totalClass0Freq) {
        this.totalClass0Freq = totalClass0Freq;
    }

    public long getTotalClass1Freq() {
        return totalClass1Freq;
    }

    public void setTotalClass1Freq(long totalClass1Freq) {
        this.totalClass1Freq = totalClass1Freq;
    }

    @Override
    public String toString() {
        return "FrequencyTable{" +
                "possibleFeatureValues=" + possibleFeatureValues +
                ", featureValues=" + Arrays.toString(featureValues) +
                ", featureValue2Class0Freq=" + featureValue2Class0Freq +
                ", featureValue2Class1Freq=" + featureValue2Class1Freq +
                ", totalClass0Freq=" + totalClass0Freq +
                ", totalClass1Freq=" + totalClass1Freq +
                '}';
    }
}
