package Logger.formatters;

import Logger.models.LogEvent;

public class JsonFormatter
        implements Formatter {

    @Override
    public String format(
            LogEvent event
    ) {

        StringBuilder sb =
                new StringBuilder();

        sb.append("{")
                .append("\"timestamp\"")
                .append(":")
                .append("\"")
                .append(event.getTimeStamp())
                .append("\"")
                .append(",")

                .append("\"message\"")
                .append(":")
                .append("\"")
                .append(event.getMessage())
                .append("\"")
                .append(",")

                .append("\"level\"")
                .append(":")
                .append("\"")
                .append(event.getLogLevel())
                .append("\"")

                .append("}");

        return sb.toString();
    }
}