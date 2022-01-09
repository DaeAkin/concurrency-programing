package donghyeon.dev.concurrency.terminate.invokeall;

import java.util.*;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 종료된 이후에도 실행이 중단된 작업이 어떤 것인지 알려주는 ExecutorService
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }
    
    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated())
            throw new IllegalStateException();
        return new ArrayList(tasksCancelledAtShutdown);
    }

    @Override
    public void execute(Runnable command) {
        exec.execute(() -> {
            try {
                command.run();
            } finally {
                if(isShutdown() && Thread.currentThread().isInterrupted())
                    tasksCancelledAtShutdown.add(command);
            }
        });
    }
    
    @Override
    public void shutdown() {
        exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout,unit);
    }
    
}
