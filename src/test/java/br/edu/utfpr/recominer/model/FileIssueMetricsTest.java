package br.edu.utfpr.recominer.model;

import br.edu.utfpr.recominer.model.Commit;
import br.edu.utfpr.recominer.model.EmptyIssueMetrics;
import br.edu.utfpr.recominer.model.FileIssueMetrics;
import br.edu.utfpr.recominer.metric.committer.Committer;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class FileIssueMetricsTest {

    public FileIssueMetricsTest() {
    }

    @Test
    public void testToString() {
        FileIssueMetrics m = new FileIssueMetrics("A.java", "B.java", new Commit(1, new Committer(1, "Foo", "Bar"), new Date()), new EmptyIssueMetrics());
        Assert.assertEquals("A.java;B.java;0", m.toString());
    }

    @Test
    public void testEqualsTrue() {
        FileIssueMetrics m = new FileIssueMetrics("A.java", "B.java", new Commit(1, new Committer(1, "Foo", "Bar"), new Date()), new EmptyIssueMetrics());
        FileIssueMetrics m2 = new FileIssueMetrics("A.java", "C.java", new Commit(1, new Committer(1, "Foo", "Bar"), new Date()), new EmptyIssueMetrics());
        Assert.assertTrue(m.hashCode() == m2.hashCode());
        Assert.assertTrue(m.equals(m));
        Assert.assertTrue(m2.equals(m2));
        Assert.assertTrue(m.equals(m2));
        Assert.assertTrue(m2.equals(m));
    }

    @Test
    public void testEqualsFalse() {
        FileIssueMetrics m = new FileIssueMetrics("A.java", "B.java", new Commit(1, new Committer(1, "Foo", "Bar"), new Date()), new EmptyIssueMetrics());
        FileIssueMetrics m2 = new FileIssueMetrics("A.java", "B.java", new Commit(2, new Committer(1, "Foo", "Bar"), new Date()), new EmptyIssueMetrics());
        Assert.assertFalse(m.equals(m2));
        Assert.assertFalse(m2.equals(m));
        Assert.assertNotEquals(m.hashCode(), m2.hashCode());
    }

//    @Test
//    public void testHeaderAndLineWithSameQuantity() {
//        final IssueMetrics issueMetrics = new IssueMetrics(1, "ISSUE 1", "http://test.edu.br/", "This is an example of issue.", "Bug", "High", "Rodrigo", "Rodrigo", 0, 0, Arrays.asList(new String[]{"This is a comment.", "This is a comment.", "This is a comment."}), 2, 1, new Timestamp(2014, 01, 01, 0, 0, 0, 0), new Timestamp(2014, 02, 01, 0, 0, 0, 0));
//        final Committer committer = new Committer(1, "Foo", "Bar");
//        final Commit commit = new Commit(1, committer, new Date());
//        final FileIssueMetrics m = new FileIssueMetrics("A.java", "B.java", commit, issueMetrics);
//        m.setCommitMetrics(new CommitMetrics(commit));
//        m.setCommitterFileMetrics(new CommitterFileMetrics(committer, new File(1, "A.java"), 1.0, 0.5));
//        m.setFilePairApriori(new FilePairApriori(1, 1, 1, 2));
//        m.setNetworkMetrics(new EmptyNetworkMetrics());
//        Assert.assertEquals(m.getHeader().split(";").length, m.toString().split(";").length);
//    }

}
