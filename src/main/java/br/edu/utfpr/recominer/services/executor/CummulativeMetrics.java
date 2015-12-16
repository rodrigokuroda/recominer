package br.edu.utfpr.recominer.services.executor;

import br.edu.utfpr.recominer.model.Commit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class CummulativeMetrics {

    private final Commit commit;
    private AtomicLong issues;
    private AtomicLong committers;
    private AtomicLong files;
    private AtomicLong insertedLines;
    private AtomicLong deletedLines;
    private AtomicLong modifiedLines;
    private AtomicLong cummulativeIssues;
    private AtomicLong cummulativeCommitters;
    private AtomicLong cummulativeFiles;
    private AtomicLong cummulativeInsertedLines;
    private AtomicLong cummulativeDeletedLines;
    private AtomicLong cummulativeModifiedLines;

    public CummulativeMetrics(final Commit commit) {
        this.commit = commit;
    }

    public Commit getCommit() {
        return commit;
    }

    public long addAndGetIssues(long issuesSize) {
        return issues.addAndGet(issuesSize);
    }

    public long addAndGetCommitters(long committersSize) {
        return committers.addAndGet(committersSize);
    }

    public long addAndGetFiles(long filesSize) {
        return files.addAndGet(filesSize);
    }

    public long addAndGetInsertedLines(long insertedLinesSize) {
        return insertedLines.addAndGet(insertedLinesSize);
    }

    public long addAndGetDeletedLines(long deletedLinesSize) {
        return deletedLines.addAndGet(deletedLinesSize);
    }

    public long addAndGetModifiedLines(long modifiedLinesSize) {
        return modifiedLines.addAndGet(modifiedLinesSize);
    }

    public long addAndGetCummulativeIssues(long issuesSize) {
        return cummulativeIssues.addAndGet(issuesSize);
    }

    public long addAndGetCummulativeCommitters(long committersSize) {
        return cummulativeCommitters.addAndGet(committersSize);
    }

    public long addAndGetCummulativeFiles(long filesSize) {
        return cummulativeFiles.addAndGet(filesSize);
    }

    public long addAndGetCummulativeInsertedLines(long insertedLinesSize) {
        return cummulativeInsertedLines.addAndGet(insertedLinesSize);
    }

    public long addAndGetCummulativeDeletedLines(long deletedLinesSize) {
        return cummulativeDeletedLines.addAndGet(deletedLinesSize);
    }

    public long addAndGetCummulativeModifiedLines(long modifiedLinesSize) {
        return cummulativeModifiedLines.addAndGet(modifiedLinesSize);
    }

    public long getIssues() {
        return issues.get();
    }

    public long getCommitters() {
        return committers.get();
    }

    public long getFiles() {
        return files.get();
    }

    public long getInsertedLines() {
        return insertedLines.get();
    }

    public long getDeletedLines() {
        return deletedLines.get();
    }

    public long getModifiedLines() {
        return modifiedLines.get();
    }

    public long getCummulativeIssues() {
        return cummulativeIssues.get();
    }

    public long getCummulativeCommitters() {
        return cummulativeCommitters.get();
    }

    public long getCummulativeFiles() {
        return cummulativeFiles.get();
    }

    public long getCummulativeInsertedLines() {
        return cummulativeInsertedLines.get();
    }

    public long getCummulativeDeletedLines() {
        return cummulativeDeletedLines.get();
    }

    public long getCummulativeModifiedLines() {
        return cummulativeModifiedLines.get();
    }

    public void add(final CummulativeMetrics cummulativeMetrics) {
        addAndGetIssues(cummulativeMetrics.issues.get());
        addAndGetCommitters(cummulativeMetrics.committers.get());
        addAndGetFiles(cummulativeMetrics.files.get());
        addAndGetInsertedLines(cummulativeMetrics.insertedLines.get());
        addAndGetDeletedLines(cummulativeMetrics.deletedLines.get());
        addAndGetModifiedLines(cummulativeMetrics.modifiedLines.get());
        addAndGetCummulativeIssues(cummulativeMetrics.cummulativeIssues.get());
        addAndGetCummulativeCommitters(cummulativeMetrics.cummulativeCommitters.get());
        addAndGetCummulativeFiles(cummulativeMetrics.cummulativeFiles.get());
        addAndGetCummulativeInsertedLines(cummulativeMetrics.cummulativeInsertedLines.get());
        addAndGetCummulativeDeletedLines(cummulativeMetrics.cummulativeDeletedLines.get());
        addAndGetCummulativeModifiedLines(cummulativeMetrics.cummulativeModifiedLines.get());
    }
}
