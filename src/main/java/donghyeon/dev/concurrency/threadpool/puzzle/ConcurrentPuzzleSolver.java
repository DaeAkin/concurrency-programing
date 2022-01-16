package donghyeon.dev.concurrency.threadpool.puzzle;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * 병렬로 동작하는 퍼즐 풀기 프로그램
 * 
 * @param <P>
 * @param <M>
 */
public class ConcurrentPuzzleSolver<P, M> {

    private final Puzzle<P, M> puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;
    final ValueLatch<Node<P, M>> solution =
            new ValueLatch<>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        this.puzzle = puzzle;
        this.exec = exec;
        this.seen = seen;
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));
            //최종 결과를 찾을 때까지 대기
            Node<P, M> solnNode = solution.getValue();
            return solnNode == null ? null : solnNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }
    
    protected Runnable newTask(P p, M m, Node<P,M> n) {
        return new SolverTask(p,m,n);
    }
    
    class SolverTask extends Node<P, M> implements Runnable {
        public SolverTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
        }
        

        @Override
        public void run() {
            if (solution.isSet() 
            || seen.putIfAbsent(pos,true) != null)
                return; //최종 결과를 구했거나 해당 위치를 이미 탐색했던 경우
            if (puzzle.isGoal(pos))
                solution.setValue(this);
            else 
                for(M m : puzzle.legalMoves(pos))
                    exec.execute(newTask(puzzle.move(pos,m),m,this));
        }
    }


}
