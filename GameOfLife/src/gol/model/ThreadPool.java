package gol.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows the user to add work that will run simultaneously with different
 * threads. Each thread have the same priority. After the threads have completed
 * their task, they are deleted. Example usage:
 * <pre>
 * {@code
 * threadPool.addWork(() -> fooMethod() );
 * threadPool.addWork(() -> barMethod() );
 *
 * threadPool.runWorkers();
 * }
 * </pre>
 *
 * @author s305054, s305089, s305084
 */
public class ThreadPool {

    /**
     * Number of availableProcessors on this computer multiplied by 2, since we
     * assume the computer has hyper threading
     */
    public static final int THREAD_NR = Runtime.getRuntime().availableProcessors() * 2;

    private final List<Thread> threads = new ArrayList<>();

    public ThreadPool() {
    }

    /**
     * Add this task to list for later execution with {@link #runWorkers()}.
     *
     * @param task An task that should be executed
     */
    public void addWork(Runnable task) {
        threads.add(new Thread(task));
    }

    /**
     * Runs each work that is present. The calling thread waits for each work to
     * finish.
     */
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
