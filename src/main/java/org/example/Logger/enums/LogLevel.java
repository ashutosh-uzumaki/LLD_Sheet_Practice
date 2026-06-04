package Logger.enums;

public enum LogLevel {
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    FATAL(5);
    private final int priority;

    LogLevel(int priority){
        this.priority = priority;
    }

    public boolean shouldLog(LogLevel threshold){
        return this.priority >= threshold.priority;
    }
}


