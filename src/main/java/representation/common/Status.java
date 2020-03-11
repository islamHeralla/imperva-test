package representation.common;

import org.apache.log4j.Logger;

public enum Status {
    NEW_MESSAGE(" this means the message is new ") {
        @Override
        public void updateRetryAttempt() {
            numberOfRetries = 0;
        }
    }, SENT_MESSAGE("the Message has been sent successfully ") {
        @Override
        public void updateRetryAttempt() {

        }
    }, RETRY_SENDING_MESSAGE("attempting to send the message ") {
        @Override
        public void updateRetryAttempt() {
            numberOfRetries++;
            lastRetryTimeStamp = System.currentTimeMillis();
        }
    }, ERROR_SENDING_MESSAGE(" we are out of retires and cannot attempt more ") {
        @Override
        public void updateRetryAttempt() {
            numberOfRetries = 0;

        }
    };

    public static final int MAX_RETRIES = 6;
    private static final Logger logger = Logger.getLogger(Status.class.getName());
    public int numberOfRetries = 1;
    public long lastRetryTimeStamp = 0;
    public String info;

    Status(String s) {
        info = s;
    }

    public boolean isFinalState() {
        return (this == ERROR_SENDING_MESSAGE || this == SENT_MESSAGE);
    }

    /**
     * this method is only giving the restriction of either sending or not
     *
     * @param currentTimeStamp
     * @param receivedTime
     * @return
     */
    public boolean isValidForRetry(long currentTimeStamp, long receivedTime) {
        long delta = currentTimeStamp - receivedTime;
        if (currentTimeStamp == 0 || receivedTime == 0) {
            return false;
        }

        if (numberOfRetries >= MAX_RETRIES) {
            return false;
        }
        if (numberOfRetries == 1 && delta >= 500) {
            //retry number # 1 and valid for attempt
            logger.info("retry number # 1 and valid for attempt");
            return true;
        }
        if (numberOfRetries == 2 && delta >= 2000) {
            //retry number # 2 and valid for attempt
            logger.info("retry number # 2 and valid for attempt");
            return true;
        }

        if (numberOfRetries == 3 && delta >= 4000) {
            //retry number # 3 and valid for attempt
            logger.info("retry number # 3 and valid for attempt");
            return true;
        }

        if (numberOfRetries == 4 && delta >= 8000) {
            //retry number # 4 and valid for attempt
            logger.info("retry number # 4 and valid for attempt");
            return true;
        }

        if (numberOfRetries == 5 && delta >= 16000) {
            //retry number # 5 and valid for attempt
            logger.info("retry number # 15and valid for attempt");
            return false;
        }


        return false;
    }

    public boolean reachedMaxRetries() {
        return numberOfRetries == MAX_RETRIES;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public long getDelayBasedOnNumberOfRetries() {
        if (numberOfRetries == 1) {
            return 500;
        }
        if (numberOfRetries == 2) {
            return 2000;
        }

        if (numberOfRetries == 3) {
            return 4000;
        }
        if (numberOfRetries == 4) {
            return 8000;
        }

        if (numberOfRetries == 5) {
            return 16000;
        }
        return -1;
    }

    public abstract void updateRetryAttempt();
}
