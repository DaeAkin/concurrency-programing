package donghyeon.dev.concurrency.terminate.invokeall;

import net.jcip.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TrackingExecutorService를 사용해 중단된 작업을 나중에 사용할 수 있도록 보관한 모습
 * 
 * 이 클래스는 특정 경쟁 조건에 빠지는 일을 피할 수가 없는데,
 * 이런 경우 때문에 실제로는 작업이 취소됐지만 
 * 겉으로는 해당 작업이 완료됐다고 잘못된 판단을 할 가능성이 있다.
 * 이런 현상은 실행 중이던 작업의 마지막 명령어를 실행하는 시점과
 * 해당 작업이 완료됐다고 기록해두는 시점의 가운데에서
 * 스레드 풀을 종료 시키도록 하면 발생하게 된다.
 * 만약 작업이 멱등 조건을 만족했다면 별로 문제가 되지 않을텐데,
 * 웹 문서 수집기 프로그램에서 일반적으로 작업이 모두 멱등 조건을 만족한다.
 * 아니면 실행이 중단된 작업의 목록을 가져가려는 애플리케이션은
 * 이와 같은 문제가 발생할 수 있다는 점을 확실히 하고,
 * 이와 같이 잘못 판단한 경우에 어떻게 대응할지 대책을 갖고 있어야 한다.
 */
public abstract class WebCrawler {
    private static final int TIME = 10;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    
    private volatile TrackingExecutor exec;
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<>();
    
    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for(URL url : urlsToCrawl) 
            submitCrawlTask(url);
        urlsToCrawl.clear();
    }
    
    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(TIME,UNIT))
                saveUncrawled(exec.getCancelledTasks());
        } finally {
            exec = null;
        }
    }
    protected abstract List<URL> processPage(URL url);
    
    private void saveUncrawled(List<Runnable> uncrawled) {
        for(Runnable task : uncrawled)
            urlsToCrawl.add(((CrawlTask)task).getPage());
    }
    

    private void submitCrawlTask(URL url) {
        exec.execute(new CrawlTask(url));
    }
    
    private class CrawlTask implements Runnable {
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted())
                    return;
                submitCrawlTask(link);
            }
        }
        
        public URL getPage() { return url;}
    }
}
