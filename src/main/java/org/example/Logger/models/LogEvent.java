package Logger.models;

import Logger.enums.LogLevel;

import java.time.Instant;

public final class LogEvent {
    private final LogLevel logLevel;
    private final String message;
    private final Instant timeStamp;

    public LogEvent(LogLevel logLevel, String message, Instant timeStamp){
        this.logLevel = logLevel;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }
}
