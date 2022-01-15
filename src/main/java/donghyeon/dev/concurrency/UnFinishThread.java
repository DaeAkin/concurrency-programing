package donghyeon.dev.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UnFinishThread {
   private static final ExecutorService exec = Executors.newSingleThreadExecutor();

     public static void main(String[] args) {
         System.out.println("main start");
          exec.execute(() -> {
              try {
                  Thread.sleep(10000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          });
      }

}
