package donghyeon.dev.concurrency.terminate;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService를 활용한 로그 서비스
 * 
 * LogServiceWithExecutor 클래스에서 스레드를 사용해야 하는 부분에서
 * 스레드를 직접 갖다 쓰기보다는 ExecutorService를 사용해 스레드의 기능을 활용한다.
 * ExecutorService를 특정 클래스의 내부에 캡슐화하면 애플리케이션에서 서비스와 스레드로 이어지는
 * 소유 관계에 한 단계를 더 추가하는 셈이고,
 * 각 단계에 해당하는 클래스는 모두 자신이 소유한 서비스나
 * 스레드의 시작과 종료에 관련된 기능을 관리한다.
 * 
 */
public class LogServiceWithExecutor {
    private static final int TIMEOUT = 10;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final Writer writer;

    public LogServiceWithExecutor(Writer writer) {
        this.writer = writer;
    }

    public void start() {}
    
    public void stop() throws InterruptedException, IOException {
        try {
            exec.shutdown();
            exec.awaitTermination(TIMEOUT,UNIT);
        } finally {
            writer.close();
        }
    }
    
    public void log(String msg) {
        try {
            exec.execute(new WriteTask(msg));
        } catch (RejectedExecutionException ignored) {
            
        }
    }
    
    private class WriteTask implements Runnable{
        private final String msg;

        public WriteTask(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            
        }
    }
}
