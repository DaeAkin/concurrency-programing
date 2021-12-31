package donghyeon.dev.concurrency.terminate;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * 인터럽트를 사용해 작업을 취소
 */
public class PrimeProducer extends Thread{
    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted())
                queue.put(p = p.nextProbablePrime());
        } catch (InterruptedException consumed) {
            /* 스레드 종료 */
        }
    }
    
    public void cancel() { interrupt();}
}
