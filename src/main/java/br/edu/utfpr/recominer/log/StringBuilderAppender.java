package br.edu.utfpr.recominer.log;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class StringBuilderAppender {

    private final StringBuilder sb;
    private final int maxLines;
    private int lines;

    public StringBuilderAppender(final int maxLines) {
        this.sb = new StringBuilder();
        this.maxLines = maxLines;
        this.lines = 0;
    }

    public void appendLine(final String line) {
        if (lines >= maxLines) {
            sb.delete(0, sb.indexOf("\n") + 1);
        } else {
            lines++;
        }
        sb.append(line).append("\n");
    }

    public int getMaxLines() {
        return maxLines;
    }

    public int getLines() {
        return lines;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
