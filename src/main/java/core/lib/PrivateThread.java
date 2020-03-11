package core.lib;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class PrivateThread {
    private static final Logger logger = Logger.getLogger(PrivateThread.class.getName());
    private final Object lock = new Object();
    private final String threadName;
    private ScheduledThreadPoolExecutor scheduler = null;
    private AtomicLong runnableId = new AtomicLong(0L);
    private BlockingQueue<RunnableWrapper> workQueue;
    private Thread workerThread = new Thread(new Runnable() {
        @Override
        public void run() {

            while (true) {
                RunnableWrapper workToExecute;
                Runnable runnableToExecute;
                try {
                    workToExecute = workQueue.take();
                } catch (InterruptedException e) {
                    return;
                }

                if (workToExecute == null || workToExecute.getRunnable() == null)
                    continue;

                runnableToExecute = workToExecute.getRunnable();
                synchronized (runnableToExecute) {
                    try {
                        runnableToExecute.run();
                    } catch (Throwable t) {
                        logger.error(t.getMessage());
                        t.printStackTrace();
                    } finally {
                        if (workToExecute.needToWait()) {
                            runnableToExecute.notifyAll();
                        }
                    }
                }
            }
        }
    });

    public PrivateThread(String threadName) {
        this.threadName = threadName;
        workQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
        workerThread.setName(this.threadName);
        workerThread.setDaemon(false);
        workerThread.start();
    }

    public void execute(Runnable runnable) {
        workQueue.add(new RunnableWrapper(runnable));
    }


    public void schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        if (scheduler == null) {
            synchronized (lock) {
                if (scheduler == null)
                    scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(
                            1, new ThreadFactoryBuilder().setNameFormat(threadName + "-scheduler-").build());
                scheduler.setRemoveOnCancelPolicy(true);
            }
        }

        scheduler.schedule(runnable, delay, timeUnit);
    }

    private class RunnableWrapper {

        private boolean needToWait;
        private Runnable runnable;
        private long runnableId;

        RunnableWrapper(Runnable runnable, boolean needToWait) {
            this.runnable = runnable;
            this.needToWait = needToWait;
            this.runnableId = PrivateThread.this.runnableId.incrementAndGet();
        }

        RunnableWrapper(Runnable runnable) {
            this(runnable, false);
        }

        boolean needToWait() {
            return needToWait;
        }

        Runnable getRunnable() {
            return runnable;
        }

    }
}