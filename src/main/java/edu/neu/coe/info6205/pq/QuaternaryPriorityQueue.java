package edu.neu.coe.info6205.pq;

import java.util.Comparator;
import java.util.Random;
public class QuaternaryPriorityQueue<K> extends PriorityQueue<K> {


    public QuaternaryPriorityQueue(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        super(n, 1, max, comparator, floyd);
    }

    @Override
    protected int parent(int k) {
        // Parent index in a 4-ary heap
        return (k - getFirst() - 1) / 4 + getFirst();
    }

    @Override
    protected int firstChild(int k) {
        // First child index in a 4-ary heap
        return 4 * (k - getFirst()) + getFirst() + 1;
    }

    @Override
    protected void sink(int k) {
        // Adapted sink operation for 4 children
        int i = k;
        while (firstChild(i) <= size() + getFirst() - 1) {
            int bestChild = bestChild(i);
            if (bestChild == -1 || !unordered(i, bestChild)) {
                break;
            }
            swap(i, bestChild);
            i = bestChild;
        }
    }

    private int bestChild(int k) {
        int firstChild = firstChild(k);
        int best = -1;
        for (int i = 0; i < 4; i++) {
            int childIndex = firstChild + i;
            if (childIndex <= size() + getFirst() - 1) {
                if (best == -1 || unordered(best, childIndex)) {
                    best = childIndex;
                }
            }
        }
        return best;
    }
}