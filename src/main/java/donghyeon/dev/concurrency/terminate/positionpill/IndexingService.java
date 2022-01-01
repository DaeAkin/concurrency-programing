package donghyeon.dev.concurrency.terminate.positionpill;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 독약 객체를 사용해 서비스를 종료
 */
public class IndexingService {
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread producer = new CrawlerThread();
    private final FileFilter fileFilter;
    private final File root;
    private final BlockingQueue queue = new LinkedBlockingQueue();

    public IndexingService(FileFilter fileFilter, File root) {
        this.fileFilter = fileFilter;
        this.root = root;
    }

    class CrawlerThread extends Thread {
        @Override
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {

            } finally {
                while (true) {
                    try {
                        queue.put(POISON);
                    } catch (InterruptedException e1) {
                        /**
                         * 재시도
                         */
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException{
            System.out.println("크롤링을 한다.");
        }
    }

    class IndexerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    File file = (File) queue.take();
                    if (file == POISON)
                        break;
                    else 
                        indexFile(file);
                }
            } catch (InterruptedException consumed) {
                
            }
        }

        private void indexFile(File file) {
            System.out.println("파일을 인덱싱 한다.");
        }
    }

    public void start() {
        producer.start();
        consumer.start();
    }

    public void stop() {
        producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }
}
