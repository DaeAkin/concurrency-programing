package donghyeon.dev.concurrency.threadpool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutionException;

class ThreadDeadlockTest {

    @Test
    @Timeout(1)
    void ThreadDeadlock() throws ExecutionException, InterruptedException {
        ThreadDeadlock threadDeadlock = new ThreadDeadlock();
        threadDeadlock.run();
    }
}
