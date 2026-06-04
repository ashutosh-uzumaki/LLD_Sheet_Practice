package Logger.formatters;

import Logger.models.LogEvent;

public interface Formatter {
    String format(LogEvent event);
}
