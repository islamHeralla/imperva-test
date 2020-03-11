package core;

import execution.HandleMessageQueue;
import execution.LifeCycleManagementComponent;
import core.lib.PrivateThread;

import java.util.concurrent.TimeUnit;

/**
 * all starts here
 * this class includes the main method and here we start the core.execution .
 * the wakeup method triggered by the private thread runnable.
 * the loop is one thread (the private thread) the handles the retries and insuring the continuity of the process
 *
 */
public class Coordinator {
    private PrivateThread privateThread;
    private LifeCycleManagementComponent handleMessage;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handleMessage.wakeup();
            scheduler();
        }
    };

    public Coordinator() {
        handleMessage = new HandleMessageQueue();
        privateThread = new PrivateThread("private-thread-coordinator");
        privateThread.execute(runnable);

    }

    public static void main(String[] args) {

        // exec
    }


    /**
     * this method establish the 500 Milliseconds loop
     */
    private void scheduler() {
        privateThread.schedule(runnable, 500, TimeUnit.MILLISECONDS);
    }
}
