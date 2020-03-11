package execution;

import core.lib.PrivateScheduledExecutor;
import representation.message.Message;

public abstract class MessagesManagement {
    protected PrivateScheduledExecutor privateScheduledExecutor;
    private static int THREAD_POOL_SIZE = 10;
    private static final String LOG_FILTER = " MessageHandler";
    public MessagesManagement() {
        privateScheduledExecutor = new PrivateScheduledExecutor(THREAD_POOL_SIZE, LOG_FILTER);
    }

    abstract void newMessage(Message message);
}
