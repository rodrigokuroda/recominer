package br.edu.utfpr.recominer.model;

import java.util.Objects;

/**
 * A file pair in issue.
 *
 * @author Rodrigo Kuroda
 */
public class FilePairIssue {

    private final FilePair filePair;
    private Integer issue;

    public FilePairIssue(String fileName, String fileName2, Integer issue) {
        filePair = new FilePair(fileName, fileName2);
        this.issue = issue;
    }

    public FilePairIssue(FilePair fileFile, Integer issue) {
        this.filePair = fileFile;
        this.issue = issue;
    }

    public FilePair getFileFile() {
        return filePair;
    }

    public String getFile1() {
        return filePair.getFile1().getFileName();
    }

    public String getFile2() {
        return filePair.getFile2().getFileName();
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilePairIssue) {
            FilePairIssue other = (FilePairIssue) obj;
            if (filePair.equals(other.getFileFile())
                    && this.issue.equals(other.issue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (Objects.hashCode(filePair.getFile1()) + Objects.hashCode(filePair.getFile2()));
        hash = 53 * hash + Objects.hashCode(this.issue);
        return hash;
    }

    @Override
    public String toString() {
        return filePair.getFile1() + ";" + filePair.getFile2() + ";" + issue;
    }

}
