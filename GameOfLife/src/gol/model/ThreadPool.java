package gol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author s305054, s305089, s305084
 */
public class ThreadPool {

    public static final int THREADS = Runtime.getRuntime().availableProcessors();

    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREADS);
    private final List<Runnable> TASKS = new ArrayList<>();

    public ThreadPool() {
    }

    public void addWork(Runnable task) {
        TASKS.add(task);
    }

    public void doWork() {
        TASKS.stream().forEach((task) -> {
            EXECUTOR.submit(task);
        });

        EXECUTOR.shutdown();
    }

    public boolean isFinished() {
        return EXECUTOR.isTerminated();
    }

}
