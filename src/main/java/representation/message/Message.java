package representation.message;

import representation.base.Component;
import representation.common.Destination;
import representation.common.MessageType;

import java.beans.Transient;
import java.util.List;

public class Message extends Component {

    private String content;
    private String source;
    private List<Destination> destination;
    private MessageType messageType = MessageType.TEXT;
    private long receivedTimeStamp;

    public long getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(long receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Destination> getDestination() {
        return destination;
    }

    public void setDestination(List<Destination> destination) {
        this.destination = destination;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", MessageType=" + messageType +
                ", receivedTimeStamp=" + receivedTimeStamp +
                ", status=" + status +
                '}';
    }

    @Transient
    public boolean isValidForRetry(long currentTimeStamp) {
        if (getStatus().isValidForRetry(currentTimeStamp, receivedTimeStamp)) {
            getStatus().updateRetryAttempt();
            return true;
        }
        return false;
    }

    @Transient
    public boolean reachedMaxRetries() {
        return status.reachedMaxRetries();
    }
}
