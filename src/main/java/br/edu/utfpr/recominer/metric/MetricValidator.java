package br.edu.utfpr.recominer.metric;

import br.edu.utfpr.recominer.model.FilePair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class MetricValidator {

    private static final String CSV_SEPARATOR = ";";

    public static void main(String[] args) throws IOException, ParseException {

        String csvCurrentRelase = "C:\\Users\\a562273\\Downloads\\Apache\\Aries 3\\Aries file pairs metrics - 1.0.csv"; // release in analysis
        String csvNextRelase = "C:\\Users\\a562273\\Downloads\\Apache\\Aries 3\\Aries file pairs metrics - 1.1.csv"; // next release of release in analysis

        MetricValidator obj = new MetricValidator();

        Map<String, Integer> csvCurrentHeader = obj.readCsvHeader(csvCurrentRelase);
        Map<FilePair, String[]> csvCurrentValues = obj.readCsvValues(csvCurrentRelase);

        Map<String, Integer> csvNextHeader = obj.readCsvHeader(csvNextRelase);
        Map<FilePair, String[]> csvNextValues = obj.readCsvValues(csvNextRelase);

        int lineNumber = 0;
        int futureFilesMatches = 0;
        for (Map.Entry<FilePair, String[]> entrySet : csvCurrentValues.entrySet()) {
            lineNumber++;
            FilePair key = entrySet.getKey();
            String[] value = entrySet.getValue();

//            for (Map.Entry<String, Integer> entrySet1 : csvCurrentHeader.entrySet()) {
//                String columnName = entrySet1.getKey();
//                Integer index = entrySet1.getValue();
//                if (!value[index].equals(csvNextValues.get(key)[index])) {
//                    System.out.println(key + " value not equals for " + columnName);
//                }
//            }

            // validates futureIssues
            int currentFutureDefects = NumberFormat.getNumberInstance().parse(value[csvCurrentHeader.get("futureDefects")]).intValue();
            if (csvNextValues.containsKey(key)) {
                futureFilesMatches++;
                // futureDefects should be same of taskDefects of next release
                int nextFutureDefects = NumberFormat.getNumberInstance().parse(csvNextValues.get(key)[csvNextHeader.get("taskDefect")]).intValue();
                if (currentFutureDefects != nextFutureDefects) {
                    System.out.println(key + " future issues not valid: line " + lineNumber);
                }
            }

        }
        System.out.println("Future file matches: " + futureFilesMatches);
        System.exit(0);
    }

    public Map<String, Integer> readCsvHeader(String csvFile) throws IOException {
        BufferedReader br = null;

        Map<String, Integer> headerIndexes = new LinkedHashMap<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));

            String[] header = br.readLine().split(CSV_SEPARATOR);

            int i = 0;
            for (String headerColumn : header) {
                headerIndexes.put(headerColumn, i++);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return headerIndexes;
    }

    public Map<FilePair, String[]> readCsvValues(String csvFile) throws IOException {

        BufferedReader br = null;
        String line;

        Map<FilePair, String[]> maps = new LinkedHashMap<>();

        try {
            FileReader fileReader = new FileReader(csvFile);
            br = new BufferedReader(fileReader);
            br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_SEPARATOR);

                FilePair filePair = new FilePair(values[0], values[1]);
                maps.put(filePair, values);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }

        return maps;
    }
}
