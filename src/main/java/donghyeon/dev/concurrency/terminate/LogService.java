package donghyeon.dev.concurrency.terminate;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogWriter에 추가한 안정적인 종료 방법
 * 
 * 단일 연산으로 종료됐는지를 확인하며
 * 로그 메시지를 추가할 수 있는 권한이라고 볼 수 있는 카운터를 하나 증가시키는 방법을 사용함.
 */
public class LogService {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService(LoggerThread loggerThread, PrintWriter writer) {
        this.loggerThread = loggerThread;
        this.writer = writer;
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown)
                throw new IllegalArgumentException();
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        public LoggerThread(Writer writer) {
            this.writer = (PrintWriter) writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        /**
                         * 재시도
                         */
                    }
                }
            } finally {
                writer.close();
            }
        }
    }

}
