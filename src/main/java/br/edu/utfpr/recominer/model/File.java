package br.edu.utfpr.recominer.model;

import java.util.Objects;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class File {

    private final Integer id;
    private final String fileName;

    public File(final Integer id, final String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    @Deprecated
    public File(final String fileName) {
        this(null, fileName);
    }

    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.fileName);
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
        final File other = (File) obj;
        if (Objects.equals(this.fileName, other.fileName)) {
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
        return fileName;
    }

    public static String getToStringHeader() {
        return "file";
    }
}
