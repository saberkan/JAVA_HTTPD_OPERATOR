package io.saberkan.httpd.model;

import java.util.Date;

public class HttpdStatus {
    public enum State {
        CREATED,
        PROCESSING,
        ERROR
    }


    private State state;
    private boolean error;
    private String message;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
