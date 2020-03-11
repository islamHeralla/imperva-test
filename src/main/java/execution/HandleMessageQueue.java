package execution;

import core.lib.PrivateScheduledExecutor;
import org.apache.log4j.Logger;
import representation.common.Status;
import representation.message.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * this class is owning the whole aspects of treating the incoming messages including the mechanism of the retries
 */
public class HandleMessageQueue extends MessagesManagement implements LifeCycleManagementComponent  {

    private static final Logger logger = Logger.getLogger(HandleMessageQueue.class.getName());
    //the number of threads should be resized as we need
    private ConcurrentHashMap<String, Message> inQueueMessages = new ConcurrentHashMap();


    public HandleMessageQueue() {
        super();
    }


    //this wakeup is been controlled by the Coordinator Class and triggered each 500 Milli Seconds
    public void wakeup() {

        final long currentTimeStamp = System.currentTimeMillis();
        if (inQueueMessages.isEmpty()) {
            logger.info("message Queue is empty ");
            return;
        }

        for (final Message message : inQueueMessages.values()) {
            if (message.getStatus().isFinalState()) {
                inQueueMessages.remove(message.getId());
                continue;
            }

            privateScheduledExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (message.isValidForRetry(currentTimeStamp)) {
                        if (sendMessage(message)) {
                            logger.info("Message Was Sent : " + message);
                            message.setStatus(Status.SENT_MESSAGE);
                        }
                    } else if (message.reachedMaxRetries()) {
                        logger.info("Max retries has been Reached under Message :" + message);
                        message.setStatus(Status.ERROR_SENDING_MESSAGE);
                    }

                }
            });


        }


    }

    @SuppressWarnings("newMessage")
    public void newMessage(Message message) {

        if (message == null) {
            logger.error("message is null");
            return;
        }
        if (message.getId() == null) {
            logger.error("message id is null ");
            return;
        }

        if (sendMessage(message)) {
            logger.error("Message : " + message + " was sent successfully ");
            return;
        }


        if (inQueueMessages.get(message.getId()) != null) {
            logger.error("Message : " + message + " duplicate Message , already exist  ");
            return;
        }

        logger.info("message :" + message + "  been added to the queue");
        message.setStatus(Status.NEW_MESSAGE);
        if (sendMessage(message)) {
            logger.info("Message : " + message + " was sent");
            return;
        }
        addNewMessageToQueue(message);


    }

    private void addNewMessageToQueue(Message message) {
        message.setReceivedTimeStamp(System.currentTimeMillis());
        message.setStatus(Status.RETRY_SENDING_MESSAGE);
        inQueueMessages.put(message.getId(), message);
    }


    /**
     * this method supposed to send the Message and Return true if it success
     * the Message Obj including the destination list for the customers that have to get the message
     *
     * @param message obj
     * @return boolean
     */
    private boolean sendMessage(Message message) {
        logger.info("assuming this function is sending the Message :" + message);
        return true;
    }

}
