package donghyeon.dev.concurrency.threadpool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 직접 작성한 스레드 클래스
 * 
 */
public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    // 몇 번째 스레드인지 추적하는 용도
    private static final AtomicInteger created = new AtomicInteger();
    // 생성된 스레드의 개수를 추적하는 용도
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();


    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable target, String name) {
        super(target, name + "-" + created.incrementAndGet());
        
        setUncaughtExceptionHandler((t, e) -> log.log(Level.SEVERE,
                "UNCAUGHT in thread " + t.getName(), e)
        );
    }

    @Override
    public void run() {
        // debug 플래그를 복사해 계속해서 동일한 값을 갖도록 한다.
        boolean debug = debugLifecycle;
        if (debug) 
            log.log(Level.FINE, "Created "+getName());
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if(debug)
                log.log(Level.FINE, "Exiting "+getName());
        }
    }
    
    public static int getThreadsAlive() {
        return alive.get();
    }
    
    public static boolean getDebug() {
        return debugLifecycle;
    }
    
    public static void setDebug(boolean b) {
        debugLifecycle = b;
    }
}
