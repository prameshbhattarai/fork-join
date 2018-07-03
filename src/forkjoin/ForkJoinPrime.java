package forkjoin;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.RecursiveAction;

/**
* Finding the prime series up to n numbers
* using fork join concept
*/
public class ForkJoinPrime {

    public static void main(String... str) {
        int n = 10000;
        int[] series = ForkJoinPrime.processor(n);
        System.out.print("[");
        for (int i = 0; i < series.length; i++)
            System.out.println(series[i] + ",");
        System.out.print("]");
    }

    public static int[] processor(int n) {
        TreeSet<Integer> listSeries = new TreeSet<>();
        PrimeTask leftChunk = new PrimeTask(2, n/2);
        PrimeTask rightChunk = new PrimeTask(n/2, n);

        leftChunk.fork();
        rightChunk.compute();
        leftChunk.join();

        listSeries.addAll(leftChunk.getPrimeSeries());
        listSeries.addAll(rightChunk.getPrimeSeries());
        return listSeries.stream().mapToInt(Integer::intValue).toArray();
    }

    public static class PrimeTask extends RecursiveAction {

        private ArrayList<Integer> primeSeries;

        private final int startingIndex;

        private final int endingIndex;

        private static boolean isPrime(int n) {
            for (int i = 2; i < n; i++)
                if (n % i == 0) return false;
            return true;
        }

        public PrimeTask(int startingIndex, int endingIndex) {
            this.startingIndex = startingIndex;
            this.endingIndex = endingIndex;
            this.primeSeries = new ArrayList<>();
        }

        public ArrayList<Integer> getPrimeSeries() {
            return primeSeries;
        }

        @Override
        protected void compute() {
            for (int i = startingIndex; i <= endingIndex; i++) {
                if (isPrime(i)) primeSeries.add(i);
            }

        }
    }
}
