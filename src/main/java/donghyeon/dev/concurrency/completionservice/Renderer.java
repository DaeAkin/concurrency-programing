package donghyeon.dev.concurrency.completionservice;

import java.util.List;
import java.util.concurrent.*;

/**
 * @see page199
 * 
 * 각각의 이미지 파일을 다운로드 받는 작업을 생성하고 
 * Executor를 활용해 다운로드 작업을 실행한다.
 * 이렇게 하면 이전에 순서대로 다운로드 하던 부분을 병렬화 하는 것이고,
 * 이미지 파일을 전부 다운로드 받는 데 걸리는 전체 시간을 줄일 수 있다.
 * 그리고 다운로드 받은 이미지는 CompletionService를 통해 찾아가도록 하면 
 * 이미지 파일을 다운로드 받는 순간 해당하는 위치에 그림을 그려 놓을 수 있다.
 * 
 * CompletionService는 특정한 배치 작업을 관리하는 모습을 띤다고 볼 수 있다.
 * 물론 CompletionService에 전체 몇 개의 작업을 등록했는지와 
 * 그 가운데 몇개의 결과를 받아왔는지를 관리한다면, 해당 배치 작업이 모두 끝났는지도 쉽게 확인할 수 있다.
 */
public class Renderer {
    public static final String SOURCE = "https://google.com";
    
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }
     public static void main(String[] args) {
         long startTime = System.nanoTime();

         ExecutorService executorService = Executors.newFixedThreadPool(10);
         Renderer renderer = new Renderer(executorService);
         renderer.renderPage(SOURCE);
         
         long endTime   = System.nanoTime();
         long totalTime = endTime - startTime;
         System.out.println(totalTime);
       
      }
    
    void renderPage(CharSequence source) {
        final List<ImageInfo> info = scanForImageInfo(source);
        ExecutorCompletionService<ImageData> completionService
                = new ExecutorCompletionService<>(executor);
        for (ImageInfo imageInfo : info) {
            completionService.submit(() -> imageInfo.downLoadImage());
        }
        renderText(source);
        
        try {
            for (int t = 0, n = info.size(); t < n; t++) {
                Future<ImageData> f = completionService.take();
                ImageData image = f.get();
                renderImage(image);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
    }
    
    private void renderImage(ImageData imageData) {
        System.out.println("Image rendered");
    }
    
    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        System.out.println("Scan Image Info");
        return List.of(new ImageInfo());
    }
    
    private void renderText(CharSequence source) {
        //7초의 렌더 작업이 있다고 가정
        try {
            System.out.println("Rendering text ");
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    
    static class ImageInfo {
        
        public ImageData downLoadImage() {
            try {
                System.out.println("Getting Image");
                //5초의 I/O 작업이 있다고 가정
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ImageData();
        }
        
    }
    
    static class ImageData { 
        
    }
}
