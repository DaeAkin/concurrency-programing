package donghyeon.dev.concurrency.threadpool;

import java.util.concurrent.*;

/**
 * 단일 스레드 Executor에서 데드락이 발생하는 작업 구조
 * 이런 코드는 금물!
 */
public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();
    
    public void run() throws ExecutionException, InterruptedException {
        System.out.println("run()" + Thread.currentThread().getName());
        Future<String> submit = exec.submit(new RenderPageTask());
        submit.get();
    }
    
    public class RenderPageTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println(" call() ");
            Future<String> header, footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = renderBody();
            //데드락 발생
            /**
             * 이전 작업이 추가한 두 번째 작업은 큐에 쌓인 상태로 이전 작업이 끝나기를 기다릴 것이고,
             * 이전 작업은 추가된 작업이 실행되어 그 결과를 알려주기를 기다릴 것이기 때문이다.
             * 하지만 이코드는 데드락이 아니다.. 
             * 하지만 ThreadDeadlock.run()처럼 사용하면
             * RenderPageTask가 스레드풀 안에서 실행하게 되고,
             * 나머지 header와 footer를 가져오는 작업도 동일한 스레드 풀 안에서 돌기때문에
             * 결과적으로 데드락이 걸린다.
             * https://stackoverflow.com/questions/29101862/why-does-header-get-footer-get-result-in-deadlock-when-using-a-single-t
             * 
             * 그래서 완전히 독립적이지 않은 작업을 Executor에 등록할 때는 항상 스레드 부족 데드락이 발생할 수 있다는 사실을 염두에 둬야 하며
             * 작업을 구현한 코드나 Executor를 설정하는 설정 파일 등에 항상 스레드 풀의 크기나 설정에 대한 내용을 설명해야 한다.
             */
            return header.get() + page + footer.get();
        }
        
        public String renderBody() {
            return "renderBody";
        }
    }
    
    private static class LoadFileTask implements Callable<String>{

        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public String call() throws Exception {
            System.out.println("LoadFileTask" + Thread.currentThread().getName());
            return null;
        }
    }
}
