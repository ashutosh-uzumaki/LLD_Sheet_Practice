package Logger.dispatcher;

import Logger.models.LogEvent;
import Logger.sinks.Sink;

import java.util.*;

public class SyncDispatcher implements LogDispatcher{
    private final List<Sink> sinkList;

    public SyncDispatcher(List<Sink> sinkList){
        this.sinkList = new ArrayList<>(sinkList);
    }

    @Override
    public void dispatch(LogEvent event){
        for(Sink sink: sinkList){
            sink.log(event);
        }
    }

}
