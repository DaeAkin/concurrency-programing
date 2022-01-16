package donghyeon.dev.concurrency.threadpool.puzzle;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.CountDownLatch;

/**
 * ConcurrentPuzzleSolver에서 사용했던 결과 값을 포함하는 래치
 * 
 * @param <T>
 */
@ThreadSafe
public class ValueLatch<T> {
    @GuardedBy("this")
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);
    
    public boolean isSet() {
        return done.getCount() == 0;
    }
    
    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }
    
    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
    
}
