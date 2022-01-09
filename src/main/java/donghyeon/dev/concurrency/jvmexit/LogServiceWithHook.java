package donghyeon.dev.concurrency.jvmexit;

import donghyeon.dev.concurrency.terminate.LogService;
import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogServiceWithHook {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final LogServiceWithHook.LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogServiceWithHook(LogServiceWithHook.LoggerThread loggerThread, PrintWriter writer) {
        this.loggerThread = loggerThread;
        this.writer = writer;
    }

    /**
     * 로그 서비스를 종료하는 종료 훅을 등록
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LogServiceWithHook.this.stop()));
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
                        synchronized (LogServiceWithHook.this) {
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogServiceWithHook.this) {
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
