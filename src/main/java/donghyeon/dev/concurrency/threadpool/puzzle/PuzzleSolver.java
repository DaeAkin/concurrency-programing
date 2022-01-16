package donghyeon.dev.concurrency.threadpool.puzzle;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 최종 결과가 없다는 사실을 확인하는 기능이 추가된 버전
 * 
 * @param <P>
 * @param <M>
 */
public class PuzzleSolver<P,M> extends ConcurrentPuzzleSolver<P,M> {

    private final AtomicInteger taskCount = new AtomicInteger(0);

    public PuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        super(puzzle, exec, seen);
    }
    
    class CountingSolverTask extends SolverTask {

        public CountingSolverTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();
        }
        
        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0)
                    solution.setValue(null);
            }
        }
    }
}
