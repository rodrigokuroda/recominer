package br.edu.utfpr.recominer.model;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePairAprioriOutput extends FilePairOutput {

    private FilePairApriori filePairApriori;

    public FilePairAprioriOutput(FilePair filePair) {
        super(filePair);
    }

    public FilePairApriori getFilePairApriori() {
        return filePairApriori;
    }

    public void setFilePairApriori(FilePairApriori filePairApriori) {
        this.filePairApriori = filePairApriori;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();

        toString.append(filePairApriori.toString());

        appendInteger(toString, issuesId.size());
        appendSetInteger(toString, issuesId);

        appendInteger(toString, commitsId.size());
        appendSetInteger(toString, commitsId);

//        if (filePairApriori.getFile2().equals(filePairApriori.getFileWithHighestConfidence())) {
//            appendInteger(toString, commitsFile2Id.size());
//            appendSetInteger(toString, commitsFile2Id);
//
//            appendInteger(toString, commitsFile1Id.size());
//            appendSetInteger(toString, commitsFile1Id);
//
//        } else {
            appendInteger(toString, commitsFile1Id.size());
            appendSetInteger(toString, commitsFile1Id);

            appendInteger(toString, commitsFile2Id.size());
            appendSetInteger(toString, commitsFile2Id);
//        }
        appendInteger(toString, defectIssuesId.size());
        appendSetInteger(toString, defectIssuesId);

        appendInteger(toString, futureDefectIssuesId.size());
        appendSetInteger(toString, futureDefectIssuesId);

        appendInteger(toString, futureIssuesId.size());
        appendSetInteger(toString, futureIssuesId);

        return toString.toString();
    }

    public String toStringAprioriOnly() {
        return new StringBuilder(filePairApriori.toString()).append(filePairApriori.toStringPairFileApriori()).toString();
    }

    public static String getToStringHeader() {
        return new StringBuilder()
                .append(FilePairApriori.getToStringHeader())
                .append("issues;issuesId;")
                .append("commits;commitsId;")
                .append("commitsFile1;commitsFile1Id;")
                .append("commitsFile2;commitsFile2Id;")
                .append("defectIssues;defectIssuesId;")
                .append("futureDefectIssues;futureDefectIssuesId;")
                .append("futureIssues;futureIssuesId")
                .toString();
    }

    public static String getToStringHeaderAprioriOnly() {
        return FilePair.getToStringHeader()
                + FilePairApriori.getToStringHeaderPairFileApriori();
    }
}
