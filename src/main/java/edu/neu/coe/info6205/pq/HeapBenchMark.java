package edu.neu.coe.info6205.pq;

import edu.neu.coe.info6205.util.Benchmark_Timer;
import java.util.*;

public class HeapBenchMark {
    private static final int M = 4095;
    private static final int INSERTIONS = 16000;
    private static final int REMOVALS = 4000;

    public static void main(String[] args) {
        var binaryHeap = new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), false);
        benchmarkPriorityQueue("Max Heap Binary", binaryHeap);
        var binaryHeapFloyd = new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), true);
        benchmarkPriorityQueue("Max Heap Binary Floyd", binaryHeapFloyd);
        var quaternaryHeap = new QuaternaryPriorityQueue<Integer>(M, true, Comparator.naturalOrder(), false);
        benchmarkPriorityQueue("Max Heap Quaternary", quaternaryHeap);
        var quaternaryHeapFloyd = new QuaternaryPriorityQueue<Integer>(M, true, Comparator.naturalOrder(), true);
        benchmarkPriorityQueue("Max Heap Quaternary Floyd", quaternaryHeapFloyd);
        var fibonacciHeap = new FibonacciHeap<Integer>(Comparator.naturalOrder());
        benchmarkPriorityQueue("Fibonacci Heap", fibonacciHeap);
    }

    public static void benchmarkPriorityQueue(String name, PriorityQueue<Integer> pq) {
        var random = new Random();
        List<Integer> spilledElements = new ArrayList<>();
        Benchmark_Timer<Boolean> benchmark = new Benchmark_Timer<>(name, null, b -> {
            for (int i = 0; i < INSERTIONS; i++) {
                int element = random.nextInt(100000);
                pq.give(element);
                if (pq.size() == M) {
                    try {
                        int spilled = pq.take();
                        spilledElements.add(spilled);
                    } catch (PQException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            for (int i = 0; i < REMOVALS; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    System.out.println(e.getMessage());
                }
            }
            Integer highestPrioritySpilled = spilledElements.stream()
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            if (highestPrioritySpilled != null) {
                System.out.println("Highest priority spilled element: " + highestPrioritySpilled);
            } else {
                System.out.println("No elements spilled.");
            }

        }, null);
        double time = benchmark.run(true, 10);
        System.out.println(name + " time: " + time + " ms");
    }

    public static void benchmarkPriorityQueue(String name, FibonacciHeap<Integer> fibHeap) {
        final Integer[] spilledElement = {null};
        Random random = new Random();
        Benchmark_Timer<Boolean> benchmark = new Benchmark_Timer<>(name, null, b -> {
            // Insert 16,000 random elements
            for (int i = 0; i < INSERTIONS; i++) {
                int element = random.nextInt(100000);
                fibHeap.insert(element);

                // Spill if the heap exceeds 4095 elements
                if (fibHeap.getNumNodes() > M) {
                    FibonacciHeap.Node<Integer> removed = fibHeap.removeMin();
                    if (spilledElement[0] == null || removed.key < spilledElement[0]) {
                        spilledElement[0] = removed.key;
                    }
                }
            }

            System.out.println("Spilled Element (highest priority): " + spilledElement[0]);

            // Remove 4,000 elements from the heap
            for (int i = 0; i < REMOVALS; i++) {
                if (!fibHeap.isEmpty()) {
                    fibHeap.removeMin(); // Remove the minimum element
                }
            }

            // Check the final state of the heap after removals
            System.out.println("Final Min after removals: " + (fibHeap.isEmpty() ? "Heap is empty" : fibHeap.findMin().key));
        }, null);
        double time = benchmark.run(true, 10);
        System.out.println(name + " time: " + time + " ms");
    }
}
