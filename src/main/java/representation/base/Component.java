package representation.base;

import representation.common.Status;

public class Component {
    protected String id;
    protected Status status;


    /**
     * the id should be unique
     *
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
