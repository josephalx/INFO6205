package edu.neu.coe.info6205.sort;

import edu.neu.coe.info6205.util.Config;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static edu.neu.coe.info6205.util.Config.CUTOFF_DEFAULT;
import static java.util.Arrays.binarySearch;

/**
 * Interface to define all the helper methods (and which does not require the underlying type to be Comparable).
 * CONSIDER pulling the methods up from NonComparableHelper.
 *
 * @param <X> the underlying type.
 */
public interface Helper<X> extends AutoCloseable, Comparator<X>, Instrument {

    default X[] copyArray(X[] a) {
        return Arrays.copyOf(a, a.length);
    }

    /**
     * Implementation of comparison that does absolutely nothing else!!
     *
     * @param x1 the first X value.
     * @param x2 the second X value.
     * @return -1, 0, or 1 as appropriate.
     */
    int pureComparison(X x1, X x2);

    /**
     * @return true if this is an instrumented Helper.
     */
    default boolean instrumented() {
        return false;
    }

    Comparator<X> getComparator();

    /**
     * Method to do any required preProcessing.
     *
     * @param xs the array to be sorted.
     * @return the array after any pre-processing.
     */
    default X[] preProcess(X[] xs) {
        // CONSIDER invoking init from here.
        return xs;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swap(X[] xs, int i, int j) {
        if (i != j) {
            X temp = xs[i];
            xs[i] = xs[j];
            xs[j] = temp;
        }
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param v  the value of xs[i].
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swap(X[] xs, X v, int i, int j) {
        xs[i] = xs[j];
        xs[j] = v;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     * @param w  the value of xs[j].
     */
    default void swap(X[] xs, int i, int j, X w) {
        xs[j] = xs[i];
        xs[i] = w;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param v  the value of xs[i].
     * @param w  the value of xs[j].
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swap(X[] xs, X v, int i, int j, X w) {
        xs[j] = v;
        xs[i] = w;
    }

    /**
     * Method to perform a stable swap, i.e., between xs[i] and xs[i-1]
     *
     * @param xs the array of X elements.
     * @param i  the index of the higher of the adjacent elements to be swapped.
     */
    default void swapStable(X[] xs, int i) {
        swap(xs, i - 1, i);
    }

    /**
     * Get the element at xs[i].
     *
     * @param xs the source array.
     * @param i  the target index.
     * @return the value of xs[i].
     */
    default X get(X[] xs, int i) {
        return xs[i];
    }

    /**
     * Set the element at xs[i].
     *
     * @param xs the destination array.
     * @param i  the target index.
     * @param x  the value to assign to xs[i].
     */
    default void set(X[] xs, int i, X x) {
        xs[i] = x;
    }

    default void copy(X[] source, int i, X[] target, int j) {
        copy(source[i], target, j);
    }

    default void copy(X x, X[] target, int j) {
        target[j] = x;
    }

    default void copyBlock(X[] source, int i, X[] target, int j, int n) {
        System.arraycopy(source, i, target, j, n);
    }

    default void distributeBlock(X[] source, int from, int to, X[] target, Function<X, Integer> f) {
        for (int i = from; i < to; i++) {
            X value = source[i];
            target[f.apply(value)] = value;
        }
    }

    /**
     * Method to generate an ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] ordered(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(i);
        return result;
    }

    /**
     * Method to generate a partially ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] partialOrdered(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(i);
        for (int i = 1; i < m; i += 2) swapStable(result, i);
        return result;
    }

    /**
     * Method to generate an reverse-ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] reverse(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(m - i - 1);
        return result;
    }

    /**
     * Method to generate an array of randomly chosen X elements.
     *
     * @param m     the number of random elements required.
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    X[] random(int m, Class<X> clazz, Function<Random, X> f);

    /**
     * Method to generate an array of randomly chosen X elements.
     * The length of the returned array is dependent on the value of n used to initialize this Helper.
     *
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] random(Class<X> clazz, Function<Random, X> f) {
        return random(getN(), clazz, f);
    }

    /**
     * Method to generate an array of two randomly chosen X elements.
     *
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] randomPair(Class<X> clazz, Function<Random, X> f) {
        return random(2, clazz, f);
    }

    /**
     * @return the description of this Helper.
     */
    String getDescription();

    /**
     * Get the configuration associated with this Helper.
     *
     * @return an instance of Config.
     */
    Config getConfig();

    /**
     * Compare v with element j.
     *
     * @param xs the array.
     * @param j  the index of the second comparand.
     * @param v  the first comparand.
     * @return the result of comparing xs[i] to w.
     */
    default int compare(X[] xs, X v, int j) {
        return compare(v, xs[j]);
    }

    /**
     * Compare element i of xs with w.
     *
     * @param xs the array.
     * @param i  the index of the first comparand.
     * @param w  the other comparand.
     * @return the result of comparing xs[i] to w.
     */
    default int compare(X[] xs, int i, X w) {
        return compare(xs[i], w);
    }

    /**
     * Compare elements i and j of xs within the subarray lo...hi
     *
     * @param xs the array.
     * @param i  one of the indices.
     * @param j  the other index.
     * @return the result of comparing xs[i] to xs[j]
     */
    default int compare(X[] xs, int i, int j) {
        return compare(xs[i], xs[j]);
    }

    /**
     * Compare values v and w and return true if v is less than w.
     *
     * @param v the first value.
     * @param w the second value.
     * @return true if v is less than w.
     */
    default boolean less(X v, X w) {
        return compare(v, w) < 0;
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is less than w.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param w  the second value.
     * @return true if v is less than w.
     */
    default boolean less(X[] xs, int i, X w) {
        return less(xs[i], w);
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is less than w.
     *
     * @param xs the array.
     * @param v  the first value.
     * @param j  the index of the second value.
     * @return true if v is less than w.
     */
    default boolean less(X[] xs, X v, int j) {
        return less(v, xs[j]);
    }

    /**
     * Compare values xs[i] and xs[j] and return true if xs[i] is less than xs[j].
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param j  the index of the second value.
     * @return true if v is less than w.
     */
    default boolean less(X[] xs, int i, int j) {
        return less(xs, xs[i], j);
    }

    /**
     * Method to determine if a pair of adjacent elements of an array is in sequence.
     * Used by sorted method.
     * It is an attempt to optimize the process, although it's questionable if it really does.
     *
     * @param xs the array of X elements.
     * @param x the left-hand element (should be smaller or equal).
     * @param i the index of the right-hand element.
     * @return the right-hand element if x <= xs[i], otherwise return null;
     */
    default X inSequence(X[] xs, X x, int i) {
        X x1 = xs[i];
        if (pureComparison(x, x1) <= 0) return x1;
        else return null;
    }

    /**
     * Method to sort a pair of adjacent elements.
     * It is the caller's responsibility to ensure that to - from = 2
     *
     * @param xs   the array of X elements.
     * @param from the index of the first element.
     * @param to   one plus the index of the second element.
     */
    default boolean sortPair(X[] xs, int from, int to) {
        if (to == from + 2)
            return swapConditional(xs, from, to - 1);
        return false;
    }

    /**
     * Method to sort a trio of adjacent elements.
     * It is the caller's responsibility to ensure that to - from = 3
     *
     * @param xs   the array of X elements.
     * @param from the index of the first element.
     * @param to   one plus the index of the third element.
     */
    default void sortTrio(X[] xs, int from, int to) {
        if (to == from + 3) {
            X a = get(xs, from);
            X b = get(xs, from + 1);
            X c = get(xs, from + 2);
            boolean swappedAB = swapConditional(xs, a, from, from + 1, b);
            if (swappedAB) b = a;
            boolean swappedBC = swapConditional(xs, b, from + 1, from + 2, c);
            if (!swappedAB && !swappedBC) return;
            if (swappedBC) swapConditional(xs, from, from + 1, c);
        }
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, int i, int j) {
        return swapConditional(xs, xs[i], i, j);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j].
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, int i, int j, X w) {
        return swapConditional(xs, xs[i], i, j, w);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, X v, int i, int j) {
        return swapConditional(xs, v, i, j, xs[j]);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j].
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, X v, int i, int j, X w) {
        if (i == j) return false;
        if (i > j) return swapConditional(xs, w, j, i, v);
        int cf = compare(v, w);
        if (cf > 0) {
            xs[i] = w;
            xs[j] = v;
        }
        return cf > 0;
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[i-1], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapStableConditional(X[] xs, int i) {
        return swapConditional(xs, i - 1, i);
    }

    /**
     * Method to perform a stable swap using half-exchanges,
     * i.e. between xs[i] and xs[j] such that xs[j] is moved to index i,
     * and xs[i] thru xs[j-1] are all moved up one.
     * This type of swap is used by insertion sort.
     * <p>
     * TODO this method does not seem to work.
     *
     * @param xs the array of Xs.
     * @param i  the index of the destination of xs[j].
     * @param j  the index of the right-most element to be involved in the swap.
     */
    void swapInto(X[] xs, int i, int j);

    /**
     * Method to perform a stable swap using half-exchanges, and binary search, i.e., x[i] is moved leftwards to its proper place, and all elements from the destination of x[i] through x[i-1] are moved up one place.
     * This type of swap is used by insertion sort.
     *
     * @param xs the array of X elements, whose elements 0 through i-1 MUST be sorted.
     * @param i  the index of the element to be swapped into the ordered array xs[0...i-1].
     */
    default void swapIntoSorted(X[] xs, int i) {
        int j = binarySearch(xs, 0, i, xs[i]);
        if (j < 0) j = -j - 1;
        if (j < i) swapInto(xs, j, i);
    }

    /**
     * CONSIDER eliminate this method as it has been superseded by swapConditional. However, maybe the latter is a better name.
     * Method to fix a potentially unstable inversion.
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the two elements to be swapped.
     * @param j  the index of the higher of the two elements to be swapped.
     */
    default void fixInversion(X[] xs, int i, int j) {
        swapConditional(xs, i, j);
    }

    /**
     * CONSIDER eliminate this method as it has been superseded by swapStableConditional. However, maybe the latter is a better name.
     * Method to fix a stable inversion.
     *
     * @param xs the array of X elements.
     * @param i  the index of the higher of the two adjacent elements to be swapped.
     */
    default void fixInversion(X[] xs, int i) {
        swapStableConditional(xs, i);
    }

    /**
     * Return index of first inversion in xs.
     *
     * @param xs an array of Xs.
     * @return -1 if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, it returns the index of the offending element.
     */
    default int findInversion(X[] xs, int from, int to) {
        X x = xs[from];
        for (int i = from + 1; i < to; i++) {
            x = inSequence(xs, x, i);
            if (x == null) return i;
        }
        return -1;
    }

    /**
     * Return index of first inversion in xs.
     *
     * @param xs an array of Xs.
     * @return -1 if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, it returns the index of the offending element.
     */
    default int findInversion(X[] xs) {
        return findInversion(xs, 0, xs.length);
    }

    /**
     * Return true if xs is sorted, i.e., has no inversions.
     *
     * @param xs an array of Xs.
     * @return true if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, false.
     */
    default boolean isSorted(X[] xs) {
        return findInversion(xs) == -1;
    }

    /**
     * Return true if xs is sorted, i.e., has no inversions.
     *
     * @param xs an array of Xs.
     * @return true if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, false.
     */
    default boolean isSorted(X[] xs, int from, int to) {
        return findInversion(xs, from, to) == -1;
    }

    /**
     * Method to post-process the array xs after sorting.
     *
     * @param xs the array that has been sorted.
     */
    default void postProcess(X[] xs) {
        // XXX do nothing
    }

    default int cutoff() {
        return CUTOFF_DEFAULT;
    }

    default int MSDCutoff() {
        return CUTOFF_DEFAULT;
    }

    /**
     * This method discriminates <code>x</code> according to the value <code>d</code>.
     * In the typical situation, where X is String, this method yields a substring of <code>x</code> starting at index <code>d</code>.
     *
     * @param x the X value to be discriminated (typically a String).
     * @param d the discriminator, assuming X = String, this is the index of the first significant character.
     * @return the substring, as an X.
     */
    @SuppressWarnings("unchecked")
    default X discriminate(X x, int d) {
        if (x instanceof String) {
            return (X) discriminateString((String) x, d);
        } else throw new SortException("subString not defined for " + x.getClass());
    }

    /**
     * This method discriminates <code>x</code> according to the value <code>d</code>.
     * This method yields a substring of <code>x</code> starting at index <code>d</code>.
     *
     * @param x the String to be discriminated.
     * @param d the discriminator, this is the index of the first significant character.
     * @return the substring.
     */
    static String discriminateString(String x, int d) {
        if (d < x.length()) return x.substring(d);
        else return " ";
    }

    /**
     * This method compares to X elements, using <code>d</code>> as the discriminator.
     * This method works only when X = String.
     *
     * @param x1 the first element.
     * @param x2 the second element.
     * @param d  the discriminator (an int).
     *           If X is a String, then d is the index of the first significant character.
     * @return the result of comparing the subStrings of x1 and x2.
     */
    default int compareSubstrings(X x1, X x2, int d) {
        return compare(discriminate(x1, d), discriminate(x2, d));
    }

    /**
     * @param n the size to be managed.
     * @throws HelperException if n is inconsistent.
     */
    void init(int n);

    /**
     * Get the current value of N.
     *
     * @return the value of N.
     */
    int getN();

    /**
     * Close this Helper, freeing up any resources used.
     */
    void close();

    /**
     * Count the number of inversions of this array.
     *
     * @param xs an array of Xs.
     * @return the number of inversions.
     */
    default long inversions(X[] xs) {
        return 0;
    }

    default void registerDepth(int depth) {
    }

    default int maxDepth() {
        return 0;
    }

    default String showStats() {
        return "";
    }

    Helper<X> clone(String description, int N);

    Helper<X> clone(String description, Comparator<X> comparator, int N);

    default Helper<X> clone(String description) {
        return clone(description, getN());
    }


}