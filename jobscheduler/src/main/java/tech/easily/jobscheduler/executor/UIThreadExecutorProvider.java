package tech.easily.jobscheduler.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * ui thread provided
 * <p>
 * Created by lemon on 06/01/2018.
 */
public final class UIThreadExecutorProvider implements ExecutorProvider {

    /**
     * post all the task on ui thread with the {@link Handler}
     */
    public final class UIThreadExecutor implements Executor {

        private final Handler handler;

        UIThreadExecutor() {
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }

        public void execute(Runnable command, long delay) {
            handler.postDelayed(command, delay);
        }

    }

    private static UIThreadExecutorProvider instance;
    private final UIThreadExecutor executor;

    private UIThreadExecutorProvider() {
        this.executor = new UIThreadExecutor();
    }

    public static UIThreadExecutorProvider getInstance() {
        if (instance == null) {
            synchronized (UIThreadExecutorProvider.class) {
                instance = new UIThreadExecutorProvider();
            }
        }
        return instance;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public int getConcurrency() {
        return 1;
    }
}
