package org.lulzm.waft.ChatModel;

public class Message {
    private String message, type;
    private long time;
    private boolean seen;
    private String from;
    private String send_time;

    // default constructor
    public Message() {
    }

    // constructor
    public Message(String message, String type, long time, boolean seen, String from, String send_time) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
        this.send_time = send_time;
    }

    // getter & setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }
}
