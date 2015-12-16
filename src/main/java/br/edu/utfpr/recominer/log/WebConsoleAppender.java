package br.edu.utfpr.recominer.log;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class WebConsoleAppender extends AbstractAppender {

    private static final List<StringBuilderAppender> APPENDERS = new ArrayList<>();

    private int maxLines = 0;

    protected WebConsoleAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        this.maxLines = maxLines;
    }

    @PluginFactory
    public static WebConsoleAppender createAppender(@PluginAttribute("name") String name,
            @PluginAttribute("maxLines") int maxLines,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginElement("Layout") Layout<?> layout,
            @PluginElement("Filters") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for JTextAreaAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WebConsoleAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    // Add the target JTextArea to be populated and updated by the logging information.
    public static void addAppender(final StringBuilderAppender appender) {
        WebConsoleAppender.APPENDERS.add(appender);
    }

    @Override
    public void append(LogEvent event) {
        final String message = new String(this.getLayout().toByteArray(event));

        // Append formatted message to text area using the Thread.
        APPENDERS.stream()
                .filter(appender -> appender != null)
                .forEach(appender -> appender.appendLine(message));
    }
}
