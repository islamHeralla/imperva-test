package core.lib;

import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PrivateScheduledExecutor extends ScheduledThreadPoolExecutor {
    private static final Logger logger = Logger.getLogger(PrivateScheduledExecutor.class.getName());

    public PrivateScheduledExecutor(int corePoolSize, String threadNamePrefix) {
        super(corePoolSize);
        this.setThreadFactory(new PrivateDefaultThreadFactory(threadNamePrefix));
    }

    public void execute(Runnable command) {
        super.execute(this.wrapRunnable(command));
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(this.wrapRunnable(command), delay, unit);
    }

    private Runnable wrapRunnable(Runnable command) {
        return new LogOnExceptionRunnable(command);
    }

    private static class LogOnExceptionRunnable implements Runnable {
        private Runnable theRunnable;


         LogOnExceptionRunnable(Runnable theRunnable) {
            this.theRunnable = theRunnable;
        }

        public void run() {
            try {
                this.theRunnable.run();
            } catch (Exception var2) {
                logger.error(var2.toString(), var2);
            }

        }
    }
}