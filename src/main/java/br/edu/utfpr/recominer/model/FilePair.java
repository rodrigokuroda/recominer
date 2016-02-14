package br.edu.utfpr.recominer.model;

import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FilePair {

    private Integer id;
    private final File file1;
    private final File file2;

    public FilePair(String file1, String file2) {
        this.file1 = new File(file1);
        this.file2 = new File(file2);
    }

    public FilePair(File file1, File file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.file1) + Objects.hashCode(this.file2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FilePair other = (FilePair) obj;
        if (Objects.equals(this.file1, other.file1)
                && Objects.equals(this.file2, other.file2)) {
            return true;
        }
        if (Objects.equals(this.file1, other.file2)
                && Objects.equals(this.file2, other.file1)) {
            return true;
        }
        return false;
    }

    /**
     * Prints the filename, filename2. The issueWeight and issues are printed
     * only if was added issues.
     *
     * @return String The attributes separated by semicolon (;)
     */
    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append(file1).append(';').append(file2).append(';');
        return toString.toString();
    }

    public static String getToStringHeader() {
        return "file1;file2;";
    }
}
