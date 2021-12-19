package donghyeon.dev.concurrency.completionservice;

import java.util.*;
import java.util.concurrent.*;

/**
 * 제한된 시간 안에 여행 관련 입찰 정보를 가져오도록 요청하는 클래스
 * 병렬성을 높임.
 * TIMEOUT이 발생하면 입찰 취소
 * 예제 코드가 이상한 것 같다.. SKIP.
 * @see P204
 */
public class QuoteTask implements Callable<QuoteTask.TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

     public static void main(String[] args) {
//          QuoteTask quoteTask = new QuoteTask();
      }
    /**
     * 입찰을 한다.
     * @param travelInfo 여행정보
     * @param companies 회사목록
     * @param ranking 정렬
     * @param time 타임아웃 대기 시간
     * @param unit 시간 단위
     * @return
     */
    public List<TravelQuote> getRankedTravelQuotes(TravelInfo travelInfo, Set<TravelCompany> companies,
                                                   Comparator<TravelQuote> ranking, long time,
                                                   TimeUnit unit) throws InterruptedException {
        List<QuoteTask> tasks = new ArrayList<>();
        for(TravelCompany company : companies) {
            tasks.add(new QuoteTask(company,travelInfo));
        }
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<TravelQuote>> futures = executor.invokeAll(tasks,time,unit);
        
        List<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIter = tasks.iterator();
        for(Future<TravelQuote> f : futures) {
            QuoteTask task = taskIter.next();
            try {
                quotes.add(f.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e));
            } catch (CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        
        Collections.sort(quotes,ranking);
        return quotes;
    }
    
    public TravelQuote getFailureQuote(Throwable e) {
        return new TravelQuote();
    }
    
    public TravelQuote getTimeoutQuote(Throwable e) {
        return new TravelQuote();
    }

    @Override
    public TravelQuote call() throws Exception {
        return null;
    }

    static class TravelQuote {
        
    }
    
    static class TravelCompany {
        
        public TravelQuote solicitQuote(TravelInfo travelInfo) {
            int i = ThreadLocalRandom.current().nextInt(1, 5);
            try {
                System.out.println(i + "초 대기");
                Thread.sleep(i * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new TravelQuote();
        }
    }
    
    static class TravelInfo {
        
    }
}


