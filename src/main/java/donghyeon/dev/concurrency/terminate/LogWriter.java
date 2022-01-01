package donghyeon.dev.concurrency.terminate;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 종료 기능이 구현되지 않은
 * 프로듀서-컨슈머 패턴의 로그 서비스 
 * 
 * 만약 로그 출력 전담 스레드에 문제가 생기면,
 * 출력 스레드가 올바로 동작하기 전까지
 * BlockingQueue가 막혀버리는 경우가 발생할 수 있어서 좋지 않다.
 * 
 * 이 클래스를 실제 상용 제품에 활용하려면
 * 애플리케이션을 종료하려 할 때 로그 출력 전담 스레드가 
 * 계속 실행되느라 JVM이 정상적으로 멈추지 않는 현상을 방지해야 한다.
 */
public class LogWriter {
    private static final int CAPACITY = 10;
    
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }
    
    public void start() {logger.start();}
    
    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    /**
     * 로그 서비스에 종료 기능을 덧붙이지만 안정적이지 않은 방법
     * 종료 요청 플래그 변수를 이용한다.
     * 
     * 이 방법은 실행 도중 경쟁 조건에 들어갈 수 있다.
     * log 메서드의 구현 모습은 <b>확인하고 동작</b>하는 과정을 거친다.
     * 프로듀서는 로그 출력 서비스가 아직 종료되지 않았다고 판단하고 
     * 실제로 종료된 이후에도 로그 메시지를 큐에 쌓으려고 대기 상태에 들어갈 가능성이 있다.
     * 그러면 해당 프로듀서 스레드는 log 메서드에서 영원히 대기 상태에 머무를 위험이 있다.
     * @param msg
     * @throws InterruptedException
     */
//    public void log(String msg) throws InterruptedException {
//        if(!shutdownRequested) 
//            queue.put(msg);
//        else
//            throw new IllegalStateException("logger is shut down");
//    }
    

    private class LoggerThread extends Thread{
        private final PrintWriter writer;

        public LoggerThread(Writer writer) {
            this.writer = (PrintWriter) writer;
        }

        @Override
        public void run() {
            try {
                while (true)
                    writer.println(queue.take());
            } catch (InterruptedException ignored) {
                
            } finally {
                writer.close();
            }
        }
    }
}
