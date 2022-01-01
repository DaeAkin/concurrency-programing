package donghyeon.dev.concurrency.terminate;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * newTaskFor를 사용해 표준을 따르지 않은 작업 중단 방법 적용
 * 
 * @param <T>
 */
public abstract class SocketUsingTask<T> implements CancellableTask<T> {
    private Socket socket;
    
    protected synchronized void setSocket(Socket s) {
        socket = s;
    }
    
    public synchronized void cancel() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
            
        }
    }
    
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}


class CancellingExecutor extends ThreadPoolExecutor {

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected<T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask)
            return ((CancellableTask<T>) callable).newTask();
        else
            return super.newTaskFor(callable);
    }
}

interface CancellableTask<T> extends Callable<T> {
    void cancel();
    RunnableFuture<T> newTask();
}
