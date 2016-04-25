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

    public static final int THREADS = 2;//Runtime.getRuntime().availableProcessors();
    List<Thread> workers = new ArrayList<>();

    public ThreadPool() {
    }

    public void createWorkers(Runnable task) {
        for (int i = 0; i < THREADS; i++) {
            workers.add(new Thread(task));
        }

    }

    public void runWorkers() {
        for (Thread worker : workers) {
            worker.start();
            System.out.println(worker.getName());
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
