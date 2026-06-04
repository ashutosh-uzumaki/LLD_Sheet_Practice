package Logger.dispatcher;

import Logger.models.LogEvent;
import Logger.sinks.Sink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncDispatcher implements LogDispatcher{
    private final BlockingQueue<LogEvent> blockingQueue;
    private final List<Sink> sinkList;
    private final Thread workerThread;

    public AsyncDispatcher(List<Sink> sinkList){
        this.sinkList = new ArrayList<>(sinkList);
        blockingQueue = new LinkedBlockingQueue<>();
        workerThread = new Thread(() -> {
            processLogs();
        }, "dispatcher");
        workerThread.start();
    }

    @Override
    public void dispatch(LogEvent event){
        try{
            blockingQueue.put(event);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private void processLogs() {
        while (true) {
            try {
                LogEvent event = blockingQueue.take();
                for (Sink sink : sinkList) {
                    sink.log(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
