package gol.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author s305054, s305089, s305084
 */
public class ThreadPool {

    public static final int THREAD_NR = Runtime.getRuntime().availableProcessors();

    private final List<Thread> threads = new ArrayList<>();

    public ThreadPool() {
    }

    /**
     * Add this task to the threadpool.
     * @param task 
     */
    public void addWork(Runnable task) {
        threads.add(new Thread(task));
    }

    public void runWorkers() {
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                System.err.println("Could not join threads.. \n" + ex);
            }
        }
        threads.clear();
    }

}
