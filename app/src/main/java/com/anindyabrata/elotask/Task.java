package com.anindyabrata.elotask;

public class Task {
    private String message ;
    private boolean done ;

    public Task(String message, boolean done) {
        this.message = message;
        this.done = done;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
