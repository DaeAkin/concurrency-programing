package donghyeon.dev.concurrency.terminate;

import java.util.concurrent.*;

public class TimeRunThread {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    /**
     * 작업 실행 전용 스레드에 인터럽트 거는 방법
     * join 메서드를 사용하고 있다. join 메서드의 단점은
     * timeRun 메서드가 리턴됐을 때 정상적으로 스레드가 종료된 것인지
     * join 메서드에서 타임아웃이 걸린 것인지를 알 수 없다는 단점을 그대로 갖고 있다. 
     * 
     * @param r
     * @param timeout
     * @param unit
     */
    public static void timeRun(final Runnable r,
                               long timeout, TimeUnit unit) throws InterruptedException {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }
            
            void rethrow() {
                if(t != null) 
                    throw new RuntimeException(t);
            }
        }
        
        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(() -> taskThread.interrupt(),timeout,unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }

    /**
     * Future를 사용해 작업 중단하기 (좋은 방법) 
     * 
     * @param r
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    public static void futureTimeRun(Runnable r,
                                     long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout,unit);
        } catch (TimeoutException e) {
            // finally 블록에서 작업이 중단될 것이다.
        } catch (ExecutionException e) {
            //작업 내부에서 예외 상황 발생. 예외를 다시 던진다.
            throw new RuntimeException(e.getCause());
        } finally {
            // 이미 종료됐다 하더라도 별다른 악영향은 없다.
            task.cancel(true); //실행중이라면 인터럽트를 건다. 
        }
    }
    
}
