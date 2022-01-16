package donghyeon.dev.concurrency.threadpool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 순차적인 재귀 함수를 병렬화한 모습
 */
public class ParallelRecursiveExample {

    private static final Executor exec = Executors.newFixedThreadPool(10);


    public <T> void sequentialRecursive(List<Node<T>> nodes,
                                        Collection<T> results) {
        for (Node<T> n : nodes) {
            results.add(n.compute());
            sequentialRecursive(n.getChildren(), results);
        }
    }

    public <T> void parallelRecursive(final Executor exec,
                                      List<Node<T>> nodes,
                                      final Collection<T> results) {
        for (final Node<T> n : nodes) {
            exec.execute(() -> results.add(n.compute()));
            parallelRecursive(exec, n.getChildren(),results);
        }
    }


    static class Node<T> {
        public T compute() {
            return (T) new Object();
        }

        public List<Node<T>> getChildren() {
            return List.of(new Node<>());
        }
    }
}
