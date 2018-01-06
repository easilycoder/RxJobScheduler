package tech.easily.jobscheduler;

import android.support.annotation.NonNull;


import io.reactivex.Scheduler;
import tech.easily.jobscheduler.executor.ExecutorProvider;
import tech.easily.jobscheduler.executor.JobExecutorProvider;
import tech.easily.jobscheduler.executor.UIThreadExecutorProvider;

import static tech.easily.jobscheduler.Priority.PRIORITY_NORMAL;

/**
 * just like {@link io.reactivex.schedulers.Schedulers},
 * the creator of the {@link PriorityScheduler.InnerPriorityScheduler}
 * note that :all the instance  created by the same method (or the overload method) share the same executor and job queue
 * <p>
 * Created by lemon on 06/01/2018.
 */

public final class JobSchedulers {

    private static final PriorityScheduler JOB_SCHEDULER;
    private static PriorityScheduler MAIN_SCHEDULER;

    static {
        JOB_SCHEDULER = new PriorityScheduler(JobExecutorProvider.getInstance());
        MAIN_SCHEDULER = new PriorityScheduler(UIThreadExecutorProvider.getInstance());
    }

    public static Scheduler job() {
        return job(PRIORITY_NORMAL, null);
    }

    public static Scheduler job(@Priority int priority) {
        return job(priority, null);
    }

    public static Scheduler job(String groupId) {
        return job(PRIORITY_NORMAL, groupId);
    }

    /**
     * background job scheduler
     * using {@link JobExecutorProvider} to provide the needed thread
     *
     * @param priority
     * @param groupId
     * @return instance of {@link PriorityScheduler.InnerPriorityScheduler}
     */
    public static Scheduler job(@Priority int priority, String groupId) {
        return JOB_SCHEDULER.groupWithPriority(priority, groupId);
    }

    public static Scheduler main() {
        return main(PRIORITY_NORMAL, null);
    }

    public static Scheduler main(@Priority int priority) {
        return main(priority, null);
    }

    public static Scheduler main(String groupId) {
        return main(PRIORITY_NORMAL, groupId);
    }

    /**
     * schedule on ui thread
     *
     * @param priority
     * @param groupId
     * @return instance of {@link PriorityScheduler.InnerPriorityScheduler}
     */
    public static Scheduler main(@Priority int priority, String groupId) {
        return MAIN_SCHEDULER.groupWithPriority(priority, groupId);
    }

    /**
     * schedule on custom thread strategy
     *
     * @param executorProvider
     * @return
     */
    public static Scheduler from(@NonNull ExecutorProvider executorProvider) {
        return new PriorityScheduler(executorProvider).groupWithPriority(PRIORITY_NORMAL, null);
    }

}
