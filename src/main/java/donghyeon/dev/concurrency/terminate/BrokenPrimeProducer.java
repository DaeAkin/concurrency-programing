package donghyeon.dev.concurrency.terminate;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 안좋은 방식의 처리
 * 
 * 프로듀서 스레드는 소수를 찾아내는 작업을 진행하고
 * 찾아낸 소수는 블로킹 큐에 집어 넣는다.
 * 그런데 컨슈머가 가져가는 것보다 프로듀서가 소수를 찾아내는 속도가 더 빠르다면
 * 큐는 곧 가득 찰 것이며 큐의 put 메서드는 블록될 것이다.
 * 이런 상태에서 부하가 걸린 컨슈머가 큐에 put 하려도 대기 중인 프로듀서의 작업을 취소시키려 한다면
 * 어떤 일이 벌어 질까?
 * cancel 메서드를 호출해 cancelled 플래그를 설정할 수는 있겠지만,
 * 프로듀서는 put 메서드에서 멈춰 있고,
 * put 메서드에서 멈춘 작업을 풀어줘야 할 컨슈머가 더 이상 작업을 처리하지 못하기 때문에
 * cancelled 변수를 확인할 수 없다.
 */
public class BrokenPrimeProducer {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    public BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }
    
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!cancelled)
                queue.put(p = p.nextProbablePrime());
        } catch (InterruptedException consumed) {
            
        }
    }
    
    public void cancel() {
        cancelled = true;
    }
    
    public void start() {
        // 구현 생략
    }
    
    public boolean needMorePrimes() {
        //구현 생략
        return true;
    }
    
    void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new ArrayBlockingQueue<BigInteger>(10);
        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();
        
    }
}
