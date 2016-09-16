package br.edu.utfpr.recominer.metric.file;

import br.edu.utfpr.recominer.core.model.CodeChurn;
import br.edu.utfpr.recominer.core.model.Commit;
import br.edu.utfpr.recominer.core.model.File;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda <rodrigokuroda at alunos.utfpr.edu.br>
 */
public class FileMetrics implements Persistable<Integer> {

    public static final String HEADER
            = "fileMetricId;"
            + "fileId;"
            + "committers;" // file's committers
            + "commits;" // file's commits
            + "fileAge;" // file's age in project
            + CodeChurn.HEADER // of 1st file, in current commit
            ;

    private Integer id;
    private File file;
    private Commit commit;
    private CodeChurn codeChurn;
    private long committers;
    private long commits;
    private long age;

    public FileMetrics(Integer id, File file) {
        this.id = id;
        this.file = file;
        this.codeChurn = new CodeChurn(file, 0, 0);
    }

    public FileMetrics(File file, Commit commit,
            CodeChurn codeChurn,
            long committers, long commits,
            long fileAgeInProject) {
        this.file = file;
        this.commit = commit;
        this.codeChurn = codeChurn;
        this.committers = committers;
        this.commits = commits;
        this.age = fileAgeInProject;
    }

    public FileMetrics(Integer id, 
            File file, Commit commit,
            CodeChurn codeChurn,
            long committers, long commits,
            long fileAgeInProject) {
        this.id = id;
        this.file = file;
        this.commit = commit;
        this.codeChurn = codeChurn;
        this.committers = committers;
        this.commits = commits;
        this.age = fileAgeInProject;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public CodeChurn getCodeChurn() {
        return codeChurn;
    }

    public long getAdditions() {
        return codeChurn.getAdditions();
    }

    public void setAdditions(long additions) {
        codeChurn.setAdditions(additions);
    }

    public long getDeletions() {
        return codeChurn.getDeletions();
    }

    public void setDeletions(long deletions) {
        codeChurn.setDeletions(deletions);
    }

    public long getChanges() {
        return codeChurn.getChanges();
    }

    public long getCommitters() {
        return committers;
    }

    public void setCommitters(long committers) {
        this.committers = committers;
    }

    public long getCommits() {
        return commits;
    }

    public void setCommits(long commits) {
        this.commits = commits;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileMetrics other = (FileMetrics) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";")
                .append(file.getId()).append(";")
                .append(committers).append(";")
                .append(commits).append(";")
                .append(age).append(";")
                .append(codeChurn)
                ;

        return sb.toString();
    }

}
