package edu.neu.coe.info6205.pq;

import java.util.Comparator;

public class FibonacciHeap<T extends Comparable<T>> {
    private Node<T> minNode;
    private int numNodes;
    private final Comparator<T> comparator;

    // Constructor accepting a comparator
    public FibonacciHeap(Comparator<T> comparator) {
        this.minNode = null;
        this.numNodes = 0;
        this.comparator = comparator.reversed(); // Reverse the comparator for max heap
    }

    public void insert(T key) {
        Node<T> node = new Node<>(key);
        minNode = mergeLists(minNode, node);
        numNodes++;
    }

    public Node<T> removeMin() {
        if (minNode == null) {
            return null;
        }

        numNodes--;

        Node<T> removedMinNode = minNode;

        if (minNode.child != null) {
            Node<T> child = minNode.child;
            do {
                child.parent = null;
                child = child.next;
            } while (child != minNode.child);
        }

        Node<T> nextInRootList = minNode.next == minNode ? null : minNode.next;

        removeNodeFromList(minNode);

        minNode = mergeLists(nextInRootList, consolidate());

        return removedMinNode;
    }

    public Node<T> findMin() {
        return minNode;
    }

    public boolean isEmpty() {
        return minNode == null;
    }

    public int getNumNodes() {
        return numNodes;
    }

    private Node<T> consolidate() {
        // Consolidation logic to merge trees of equal degree
        // ...
        return minNode;
    }

    private void removeNodeFromList(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private Node<T> mergeLists(Node<T> a, Node<T> b) {
        if (a == null) return b;
        if (b == null) return a;

        if (compare(b.key, a.key) < 0) {
            Node<T> temp = a;
            a = b;
            b = temp;
        }

        Node<T> aNext = a.next;
        a.next = b.next;
        b.next.prev = a;
        b.next = aNext;
        aNext.prev = b;

        return a;
    }

    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return a.compareTo(b);
        }
    }

    public static class Node<T> {
        public final T key;
        private Node<T> child, parent, next, prev;

        public Node(T key) {
            this.key = key;
            this.next = this.prev = this;
        }
    }
}