package tech.easily.jobscheduler.executor;

import java.util.concurrent.Executor;

/**
 * implements it to provide custom thread strategy
 * <p>
 * Created by lemon on 06/01/2018.
 */

public interface ExecutorProvider {

    /**
     * @return
     */
    Executor getExecutor();

    /**
     * the scheduler will schedule the job according the concurrency
     *
     * @return the concurrency of this executor service
     */
    int getConcurrency();
}
