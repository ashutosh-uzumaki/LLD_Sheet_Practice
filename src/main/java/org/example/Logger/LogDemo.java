package Logger;

import Logger.enums.LogLevel;
import Logger.formatters.JsonFormatter;
import Logger.formatters.TextFormatter;
import Logger.service.Logger;
import Logger.sinks.ConsoleSink;
import Logger.sinks.FileSink;
import Logger.sinks.Sink;

import java.nio.file.Path;
import java.util.*;

public class LogDemo {
    public static void main(String[] args) {
        List<Sink> sinkList = List.of(new ConsoleSink(new TextFormatter(), LogLevel.INFO));
        Logger logger = Logger.getInstance(sinkList);
        logger.debug("Debug messaging");
        logger.info("Info messaging");
        logger.addSink(new FileSink(new JsonFormatter(), LogLevel.DEBUG, Path.of("logs")));

        logger.debug("Testing Debug Json Formatter");
        logger.info("Testing Info Json Formatter");
    }
}
