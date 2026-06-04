package Logger.sinks;

import Logger.enums.LogLevel;
import Logger.formatters.Formatter;
import Logger.models.LogEvent;

public abstract class Sink {
    private final Formatter formatter;
    private final LogLevel threshold;

    public Sink(Formatter formatter, LogLevel threshold){
        this.formatter = formatter;
        this.threshold = threshold;
    }

    public final void log(LogEvent logEvent){
        if(!logEvent.getLogLevel().shouldLog(threshold)){
            return;
        }

        String formattedString = formatter.format(logEvent);
        write(formattedString);
    }
    protected abstract void write(String formattedString);
}
