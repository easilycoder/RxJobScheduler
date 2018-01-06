package tech.easily.jobscheduler;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import tech.easily.jobscheduler.executor.ExecutorProvider;

/**
 * a {@link Scheduler} which can schedule task by group and priority
 * instance of {@link PriorityScheduler} is singleton which manager the executor and queue global
 * {@link InnerPriorityScheduler} provide the real task schedule operation
 * <p>
 * Created by lemon on 06/01/2018.
 */

public final class PriorityScheduler {


    private Executor executor;

    private final int concurrency;

    private PriorityBlockingQueue<ComparableRunner> queue;

    private List<ComparableRunner> runningTask = new ArrayList<>();

    private static final int MAX_TASK_PER_GROUP = 1;

    private final Object LOCK = new Object();


    PriorityScheduler(@NonNull ExecutorProvider provider) {
        this.concurrency = provider.getConcurrency();
        this.executor = provider.getExecutor();
        this.queue = new PriorityBlockingQueue<>();
    }


    Scheduler groupWithPriority(int priority, String groupId) {
        return new InnerPriorityScheduler(priority, groupId);
    }


    public synchronized void cancelGroup(@NonNull String groupId) {
        for (Iterator<ComparableRunner> i = queue.iterator(); i.hasNext(); ) {
            ComparableRunner runner = i.next();
            if (runner != null && groupId.equals(runner.groupId)) {
                runner.dispose();
                i.remove();
            }
        }
        for (ComparableRunner runner : runningTask) {
            if (runner != null && groupId.equals(runner.groupId)) {
                runner.dispose();
            }
        }
    }


    /**
     * the real {@link Scheduler} implementation ,response to create a {@link io.reactivex.Scheduler.Worker}
     */
    public final class InnerPriorityScheduler extends Scheduler {

        private int priority;
        private String groupId;

        private InnerPriorityScheduler(int priority, String groupId) {
            this.priority = priority;
            this.groupId = groupId;

        }

        @Override
        public Worker createWorker() {
            return new PriorityWorker(executor, queue, priority, groupId);
        }

        public int getPriority() {
            return priority;
        }

        public String getGroupId() {
            return groupId;
        }


        public synchronized void cancelGroup(@NonNull String groupId) {
            PriorityScheduler.this.cancelGroup(groupId);
        }
    }

    /**
     * Schedule logic lay on
     * in RxJava 1.x，{@link Scheduler} only response to create {@link io.reactivex.Scheduler.Worker} instance，{@link io.reactivex.Scheduler.Worker} for the real schedule job
     * in RxJava 2.x，{@link Scheduler} can deal the real job directly
     * here ,all the jobs are passed to {@link io.reactivex.Scheduler.Worker}
     */
    final class PriorityWorker extends Scheduler.Worker implements IScheduler {
        final PriorityBlockingQueue<ComparableRunner> queue;
        final Executor executor;
        final int priority;
        final String groupId;
        final CompositeDisposable tasks = new CompositeDisposable();

        PriorityWorker(Executor executor, PriorityBlockingQueue<ComparableRunner> queue, int priority, String groupId) {
            this.executor = executor;
            this.queue = queue;
            this.priority = priority;
            this.groupId = groupId;
        }

        @Override
        public Disposable schedule(Runnable run) {
            return schedule(run, 0, TimeUnit.SECONDS);
        }

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (isDisposed()) {
                return EmptyDisposable.INSTANCE;
            }
            // wrap the former task
            ComparableRunner runner = new ComparableRunner(this, run, priority, groupId);
            // enqueue the task and schedule it
            enqueue(runner, delay, unit);
            tasks.add(runner);
            return runner;
        }

        @Override
        public void dispose() {
            if (!tasks.isDisposed()) {
                tasks.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return tasks.isDisposed();
        }

        @Override
        public void enqueue(ComparableRunner runner, long delay, TimeUnit unit) {
            synchronized (LOCK) {
                queue.offer(runner, delay, unit);
                promoteRunner();
            }
        }

        @Override
        public void cancel(ComparableRunner runner) {
            synchronized (LOCK) {
                if (runner != null) {
                    queue.remove(runner);
                }
            }
        }

        @Override
        public void finished(ComparableRunner runner) {
            synchronized (LOCK) {
                if (!runningTask.remove(runner)) {
                    throw new AssertionError("task wasn't in-flight!");
                }
                promoteRunner();
            }
        }

        /**
         * take job to run
         */
        private void promoteRunner() {
            if (runningTask.size() >= concurrency) {
                return;
            }
            for (Iterator<ComparableRunner> i = queue.iterator(); i.hasNext(); ) {
                ComparableRunner call = i.next();
                if (runningTaskForGroup(call) < MAX_TASK_PER_GROUP) {
                    i.remove();
                    runningTask.add(call);
                    executor.execute(call);
                }
                if (runningTask.size() >= concurrency) return;
            }
        }

        private int runningTaskForGroup(ComparableRunner runner) {
            int result = 0;
            for (ComparableRunner temp : runningTask) {
                if (!TextUtils.isEmpty(runner.groupId) && runner.groupId.equals(temp.groupId)) {
                    result++;
                }
            }
            return result;
        }
    }

}
