import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ArffFileReader {

    public List<InstanceEntry> readArff(String fileName) {

        FileReader file = null;
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'" + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }

        List<InstanceEntry> arffEntries = new ArrayList<>();

        String line;
        BufferedReader bufferedReader = new BufferedReader(file);

        try {

            List<String> attributeLabelsList = new ArrayList<>();
            HashMap<String, List<String>> possibleAttributeValuesMap = new HashMap<>();
            String[] allClassLabels = new String[2];
            while((line = bufferedReader.readLine()) != null) {

                if (line.startsWith("@attribute")) {

                    String[] header = line.split(" ");
                    String attributeTag = header[1]
                                            .replace("'", " ")
                                            .replace("'", " ")
                                            .replaceAll("\\s+", "");
                    attributeLabelsList.add(attributeTag);
                    StringBuilder possibleAttributeValues = new StringBuilder();
                    for (int i = 2; i < header.length; i++) {
                        possibleAttributeValues.append(header[i]);
                    }
                    String possibleAttributeValuesStr = possibleAttributeValues.toString();
                    possibleAttributeValuesStr = possibleAttributeValuesStr.replace('{', ' ');
                    possibleAttributeValuesStr = possibleAttributeValuesStr.replace('}', ' ');
                    possibleAttributeValuesStr = possibleAttributeValuesStr.replaceAll("\\s+", "");
                    possibleAttributeValuesMap.put(attributeTag, Arrays.asList(possibleAttributeValuesStr.split(",")));
                    if (attributeTag.equalsIgnoreCase("class")) {
                        allClassLabels = possibleAttributeValuesStr.split(",");
                    }
                } else if (line.startsWith("@") || line.startsWith("%")) {

                    continue;
                } else {

                    List<String> featureValues = Arrays.asList(line.split(","));
                    InstanceEntry instanceEntry = new InstanceEntry();
                    int featureLen = featureValues.size();
                    instanceEntry.setClassLabel(
                            featureValues.get(featureLen - 1)
                                    .equalsIgnoreCase(allClassLabels[0]) ? 0 : 1); // Extract class label.
                    // Trim class label and set feature values
                    instanceEntry.setFeatureValues(featureValues.toArray(new String[0]));
                    instanceEntry.setAttributeLabels(attributeLabelsList.toArray(new String[0]));
                    instanceEntry.setPossibleAttributeValues(possibleAttributeValuesMap);
                    instanceEntry.setAllClassLabels(allClassLabels);
                    arffEntries.add(instanceEntry);
                }
            }
            bufferedReader.close();
        } catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'" + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }

        return arffEntries;
    }
}
