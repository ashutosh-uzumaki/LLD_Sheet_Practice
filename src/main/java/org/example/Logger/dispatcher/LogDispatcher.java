package Logger.dispatcher;

import Logger.models.LogEvent;

public interface LogDispatcher {
    void dispatch(LogEvent event);
}
