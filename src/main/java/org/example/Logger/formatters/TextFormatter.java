package Logger.formatters;

import Logger.models.LogEvent;

public class TextFormatter implements Formatter{
    @Override
    public String format(LogEvent logEvent){
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(logEvent.getTimeStamp());
        sb.append(']');
        sb.append(" ");
        sb.append('[');
        sb.append(logEvent.getLogLevel());
        sb.append(']');
        sb.append(" ");
        sb.append('[');
        sb.append(logEvent.getMessage());
        sb.append(']');
        return sb.toString();
    }
}
