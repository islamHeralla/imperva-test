package execution;

import org.apache.log4j.Logger;
import representation.common.Status;
import representation.message.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * this class shows how we can solve this without implementing the periodic wakeup method
 * here im using Schedulers in order to schedule the next Runnable delegator and base on the delay between retries
 * i'm able to schedule the next flop easily
 */
public class HandleMessageQueueV2WithoutWakeup extends MessagesManagement implements LifeCycleManagementComponent {


    private static final Logger logger = Logger.getLogger(HandleMessageQueueV2WithoutWakeup.class.getName());

    //the number of threads should be resized as we need
    private ConcurrentHashMap<String, Message> inQueueMessages = new ConcurrentHashMap();


    public HandleMessageQueueV2WithoutWakeup() {
        super();
    }

    //this wakeup is not needed in this case
    public void wakeup() {


    }

    @SuppressWarnings("newMessage")
    public void newMessage(final Message message) {

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
        message.setReceivedTimeStamp(System.currentTimeMillis());
        message.setStatus(Status.RETRY_SENDING_MESSAGE);

        privateScheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                validateAndReSchedule(message);

            }
        }, message.getStatus().getDelayBasedOnNumberOfRetries(), TimeUnit.MILLISECONDS);

    }

    private void validateAndReSchedule(final Message message) {
        if (message.isValidForRetry(System.currentTimeMillis())) {
            if (sendMessage(message)) {
                logger.info("Message Was Sent : " + message);
                message.setStatus(Status.SENT_MESSAGE);
                return;
            }
        } else if (message.reachedMaxRetries()) {
            logger.info("Max retries has been Reached under Message :" + message);
            message.setStatus(Status.ERROR_SENDING_MESSAGE);
            return;
        }

        privateScheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                validateAndReSchedule(message);
            }
        }, message.getStatus().getDelayBasedOnNumberOfRetries(), TimeUnit.MILLISECONDS);

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
