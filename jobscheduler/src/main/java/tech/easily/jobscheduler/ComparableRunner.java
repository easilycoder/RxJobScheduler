package tech.easily.jobscheduler;

import android.support.annotation.NonNull;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.disposables.Disposable;


/**
 * RxJava Task wrapper,adding groupId and priority properties
 * manage all the task in {@link PriorityBlockingQueue}
 * all the task will mark as disposed automatically after the job finished
 * <p>
 * Created by lemon on 06/01/2018.
 */
final class ComparableRunner extends AtomicBoolean implements Runnable, Comparable<ComparableRunner>, Disposable {

    /**
     * inner scheduler
     */
    private IScheduler scheduler;
    /**
     * RxJava real schedule task
     */
    private Runnable realRunner;

    private int priority;

    String groupId;

    /**
     * generate a unique code for the instance ,so we can keep the fifo feature for the {@link PriorityBlockingQueue}
     */
    private static final AtomicLong seq = new AtomicLong(0);

    private final long seqNum;

    ComparableRunner(IScheduler scheduler, @NonNull Runnable realRunner, int priority, String groupId) {
        this.scheduler = scheduler;
        this.realRunner = realRunner;
        this.priority = priority;
        this.groupId = groupId;
        this.seqNum = seq.getAndIncrement();
    }


    @Override
    public void run() {
        if (get()) {
            scheduler.finished(this);
            return;
        }
        try {
            realRunner.run();
        } finally {
            scheduler.finished(this);
            lazySet(true);
        }
    }

    @Override
    public int compareTo(@NonNull ComparableRunner runner) {
        int diff = runner.priority - priority;
        if (diff == 0 && runner != this) {
            diff = (seqNum < runner.seqNum ? -1 : 1);
        }
        return diff;
    }

    @Override
    public void dispose() {
        // cancel the task if it had been disposed before
        if (!get()) {
            scheduler.cancel(this);
        }
        lazySet(true);
    }

    @Override
    public boolean isDisposed() {
        return get();
    }
}
