package br.edu.utfpr.recominer.core.model;

import java.util.Date;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class Issue implements Persistable<Integer>, Comparable<Issue> {

    private final Integer id;
    private final String type;
    private final String key;
    private final Date fixDate;
    private final Date submittedOn;
    private Date updatedOn;

    public Issue(Integer id) {
        this.id = id;
        this.type = null;
        this.fixDate = null;
        this.submittedOn = null;
        this.updatedOn = null;
        this.key = null;
    }

    public Issue(Integer id, String type) {
        this.id = id;
        this.type = type;
        this.fixDate = null;
        this.submittedOn = null;
        this.updatedOn = null;
        this.key = null;
    }

    public Issue(Integer id, String type, String key, Date submittedOn, Date fixDate, Date updatedOn) {
        this.id = id;
        this.type = type;
        this.fixDate = fixDate;
        this.key = key;
        this.submittedOn = submittedOn;
        this.updatedOn = updatedOn;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Date getFixDate() {
        return fixDate;
    }

    public Date getSubmittedOn() {
        return submittedOn;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final Issue other = (Issue) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Order by fix date ascendant (older to newer)
     * @param other Another Issue
     */
    @Override
    public int compareTo(Issue other) {
        if (fixDate == null) {
            return 0;
        }
        if (fixDate.after(other.getFixDate())) {
            return 1;
        } else if (fixDate.before(other.getFixDate())) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
