package donghyeon.dev.concurrency.threadpool;

import java.util.concurrent.ThreadFactory;

/**
 * 직접 작성한 스레드 팩토리
 * 
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r,poolName);
    }
}
