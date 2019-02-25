package com.anindyabrata.elotask;

import java.util.HashMap;

public class Task {
    private String id ;
    private String message ;
    private boolean done ;

    public Task(String id, String message, boolean done) {
        this.id = id;
        this.message = message;
        this.done = done;
    }

    public boolean equals(Task t){
        if(t != null && t.id == this.id) return true;
        return false;
    }

    public HashMap<String,Object> toMap(){
        HashMap<String,Object> ret = new HashMap<String, Object>();
        ret.put("message", message);
        ret.put("done", done);
        return ret;
    }

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }

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
