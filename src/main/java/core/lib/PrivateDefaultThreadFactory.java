package core.lib;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PrivateDefaultThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final int priority;

    public PrivateDefaultThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, Thread.NORM_PRIORITY);
    }

    PrivateDefaultThreadFactory(String threadNamePrefix, int setPriority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = threadNamePrefix + "-";
        priority = setPriority;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        t.setPriority(priority);
        return t;
    }
}