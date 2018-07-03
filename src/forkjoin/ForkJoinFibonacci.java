package forkjoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

/**
 * Generating fibonacci series up to n numbers
 * using fork join concept
 */
public class ForkJoinFibonacci {

    private static int getChunkSize(final int nChunks, final int nElements) {
        // Integer ceil
        return (nElements + nChunks - 1) / nChunks;
    }

    private static int getChunkStartInclusive(final int chunk, final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    private static int getChunkEndExclusive(final int chunk, final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        return (end > nElements) ? nElements : end;
    }

    public static void main(String... args) {
        int n = 50, task = 5;
        ArrayList<Long> series = new ArrayList<>();

        FiboTaskTest[] chunkOfTask = new FiboTaskTest[task];

        for (int i = 0; i < task; i++) {
            chunkOfTask[i] = new FiboTaskTest(getChunkStartInclusive(i, task, n), getChunkEndExclusive(i, task, n));

            if (i < task - 1)
                chunkOfTask[i].fork(); // fork until the last chunk
            else
                chunkOfTask[i].compute(); // start to compute
        }

        // join each chunks which we have forked
        for (int i = 0; i < task - 1; i++)
            chunkOfTask[i].join();

        // Compute sum from each chunks
        for (int i = 0; i < task; i++)
            series.addAll(chunkOfTask[i].taskSet);

        System.out.println("final stream");
        series.stream().forEach(System.out::println);
    }


    private static final Map<Integer, Long> lookup = new HashMap<>();

    public static class FiboTaskTest extends RecursiveAction {

        private final int startingIndex;
        private final int endingIndex;
        private final ArrayList<Long> taskSet;

        public FiboTaskTest(int startingIndex, int endingIndex) {
            this.startingIndex = startingIndex;
            this.endingIndex = endingIndex;
            this.taskSet = new ArrayList<>();
        }

        public Long recursiveFiboWithMemoization(int n) {
            if (n == 0 || n == 1) return 1L;
            else {
                Long looked = ForkJoinFibonacci.lookup.get(n);
                if (looked != null)
                    return looked;
                else {
                    Long fiboValue = recursiveFiboWithMemoization(n - 2) + recursiveFiboWithMemoization(n - 1);
                    ForkJoinFibonacci.lookup.put(n, fiboValue);
                    return fiboValue;
                }
            }
        }

        @Override
        protected void compute() {
            for (int i = startingIndex; i <= endingIndex; i++)
                taskSet.add(recursiveFiboWithMemoization(i));
        }
    }
}
