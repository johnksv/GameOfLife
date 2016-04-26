package gol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author s305054, s305089, s305084
 */
public class ThreadPool {

    public static final int THREADS = Runtime.getRuntime().availableProcessors();
    
    private static final Object LOCK = new Object();
    private List<Thread> workers = new ArrayList<>();

    public ThreadPool() {
    }

    public void addWorker(Runnable task) {
        workers.add(new Thread(task));
    }

    public void runWorkers() {
        for (Thread worker : workers) {
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        workers.clear();
    }

}
