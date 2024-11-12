package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();

    public static void main(String[] args) {
        processArgs(args);
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();
        int[] arraySizes = {100000, 500000, 1000000, 2000000, 4000000, 8000000, 16000000, 32000000, 64000000};
        int[] cutoffValues = {5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000, 1280000, 2560000};
        int[] array;
        ArrayList<String> results = new ArrayList<>();
        results.add("ArraySize,Cutoff,Execution_Time(ms)");
        for (int size : arraySizes) {
            for (int cutoff : cutoffValues) {
                ParSort.cutoff = cutoff;
                array = new int[size];
                for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                ParSort.sort(array, 0, array.length);
                long time;
                long startTime = System.currentTimeMillis();
                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    ParSort.sort(array, 0, array.length);
                }
                long endTime = System.currentTimeMillis();
                time = (endTime - startTime) / 10;
                System.out.printf("Array Size: %d, Cutoff: %d, Avg Time: %d ms\n", size, cutoff, time);
                results.add(size + "," + cutoff + "," + time);
            }
        }
        writeToFile(results);
    }

    private static void writeToFile(ArrayList<String> results) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.csv")))) {
            for (String line : results) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Results have been saved to result.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }


}