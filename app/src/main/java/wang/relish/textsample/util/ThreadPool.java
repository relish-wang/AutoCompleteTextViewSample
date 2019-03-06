package wang.relish.textsample.util;


import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

/**
 * 线程池
 *
 * @author Relish Wang
 * @since 2018/05/30
 */
public enum ThreadPool {

    NET_IMAGE(10, "Net Image Task", true),
    SINGLE(1, "Single Thread", false);


    private final ThreadPoolExecutor mExecutor;

    private static final String TAG = "ThreadPool";

    ThreadPool(final int threadCount, final String name, final boolean isStack) {
        BlockingQueue<Runnable> queue;
        if (isStack) {
            queue = new ThreadPool.LinkedBlockingStack<>();
        } else {
            queue = new LinkedBlockingQueue<>();
        }

        mExecutor = new ThreadPoolExecutor(
                threadCount,
                threadCount,
                30,
                TimeUnit.SECONDS,
                queue,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, name);
                    }
                });

        mExecutor.allowCoreThreadTimeOut(true);
    }


    public final Future<?> execute(Runnable runnable) {
        return mExecutor.submit(new ThreadPool.RunnableTask(runnable));
    }


    private static final class RunnableTask implements Runnable {
        private final Runnable mRunnable;

        private RunnableTask(Runnable runnable) {
            if (runnable == null) throw new IllegalArgumentException("runnable is null!");
            mRunnable = runnable;
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } catch (Exception e) {
                Log.e(TAG, "a exception occurred while doing a runnable task: " + e);
            }
        }
    }

    /**
     * 栈实现
     *
     * @param <T> 任务
     */
    private static final class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {
        @Override
        public boolean add(T t) {
            super.addFirst(t);
            return true;
        }

        @Override
        public boolean offer(T t) {
            return super.offerFirst(t);
        }

        @Override
        public void put(T t) throws InterruptedException {
            super.putFirst(t);
        }

        @Override
        public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
            return super.offerFirst(t, timeout, unit);
        }
    }
}