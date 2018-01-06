package tech.easily.jobscheduler.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * recycled background thread provided
 * <p>
 * Created by lemon on 206/01/2018.
 */

public final class JobExecutorProvider implements ExecutorProvider {

    private static JobExecutorProvider instance;

    private final Executor innerExecutor;

    private final int concurrency;

    private JobExecutorProvider() {
        this.concurrency = Runtime.getRuntime().availableProcessors();
        innerExecutor = Executors.newFixedThreadPool(concurrency);
    }

    public static JobExecutorProvider getInstance() {
        if (instance == null) {
            synchronized (JobExecutorProvider.class) {
                if (instance == null) {
                    instance = new JobExecutorProvider();
                }
            }
        }
        return instance;
    }

    @Override
    public Executor getExecutor() {
        return innerExecutor;
    }

    @Override
    public int getConcurrency() {
        return concurrency;
    }
}
