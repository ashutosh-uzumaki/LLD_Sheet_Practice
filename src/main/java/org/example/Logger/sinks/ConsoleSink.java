package Logger.sinks;

import Logger.enums.LogLevel;
import Logger.formatters.Formatter;

public class ConsoleSink extends Sink{

    public ConsoleSink(Formatter formatter, LogLevel level){
        super(formatter, level);
    }

    @Override
    protected void write(String formattedString){
        System.out.println(formattedString);
    }
}
