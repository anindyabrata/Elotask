package com.anindyabrata.elotask;

import java.util.HashMap;

/**
 * Class used to represent data
 */
public class Task {
    private String id ;
    private String message ;
    private boolean done ;

    /**
     * Task object constructor
     * @param id String contains auto-generated id that points to document on firestore
     * @param message String containing Task message
     * @param done Boolean value true if Task is marked as done, false otherwise
     */
    public Task(String id, String message, boolean done) {
        this.id = id;
        this.message = message;
        this.done = done;
    }

    /**
     * Two Tasks are equal when their ids are equal
     * @param t Task being compared with
     * @return Boolean equality
     */
    public boolean equals(Task t){
        if(t != null && t.id == this.id) return true;
        return false;
    }

    /**
     * Represents a Task object as a HashMap ready to be stored as a document in firestore
     * @return HashMap object with message and done fields
     */
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
