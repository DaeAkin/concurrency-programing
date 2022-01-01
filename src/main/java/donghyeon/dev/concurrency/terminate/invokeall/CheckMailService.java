package donghyeon.dev.concurrency.terminate.invokeall;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 메서드 내부에서 Executor를 사용하는 모습
 * 
 * checkMail 메서드는 여러 서버를 대상으로 새로 도착한 메일이 있는지를 병렬로 확인한다.
 * 먼저 메서드 내부에 Executor 인스턴스를 하나 생성하고, 각 서버별로 구별된 작업을 실행 시킨다.
 * 그리고 Executor 서비스를 종료시킨 다음, 각 작업이 모두 끝나고 Executor가 종료될 때까지 대기한다.
 */
public class CheckMailService {
    
    boolean checkMail(Set<String> hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (final String host : hosts) {
                exec.execute(() -> {
                    if(checkMail(host)) 
                        hasNewMail.set(true);
                });
            }
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }
    
    boolean checkMail(String host) {
        return true;
    }
}
