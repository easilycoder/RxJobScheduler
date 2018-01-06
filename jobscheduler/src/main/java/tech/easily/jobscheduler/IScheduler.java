package tech.easily.jobscheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by lemon on 06/01/2018.
 */

public interface IScheduler {

    void enqueue(ComparableRunner runner, long delay, TimeUnit unit);

    void cancel(ComparableRunner runner);

    void finished(ComparableRunner runner);
}
