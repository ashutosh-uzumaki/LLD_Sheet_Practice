package Logger.service;

import Logger.dispatcher.LogDispatcher;
import Logger.enums.LogLevel;
import Logger.models.LogEvent;
import Logger.sinks.Sink;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static volatile Logger instance;
    private final LogDispatcher dispatcher;

    private Logger(LogDispatcher dispatcher){
       this.dispatcher = dispatcher;
    }

    public static Logger getInstance(LogDispatcher dispatcher){
        if(instance == null){
            synchronized (Logger.class){
                if(instance == null){
                    instance = new Logger(dispatcher);
                }
            }
        }
        return instance;
    }

    public void info(String msg){
        log(LogLevel.INFO, msg);
    }
    public void debug(String msg){
        log(LogLevel.DEBUG, msg);
    }

    public void error(String msg){
        log(LogLevel.ERROR, msg);
    }

    private void log(LogLevel level, String msg){
        LogEvent event = new LogEvent(level, msg, Instant.now());
        dispatcher.dispatch(event);
    }

}
