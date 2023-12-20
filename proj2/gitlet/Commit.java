package gitlet;

// TODO: any imports you need here
import java.io.Serializable;
import java.util.Date;

import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit  implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private Map<String, String> fileMap; // key: filename, value: fileID
    private String parent;
    // private Commit next;

    /* make the initial commit, which has zero argument */
    public Commit() {  // no "static", this is a class constructor
        this.message = "initial commit";
        this.timestamp = new Date(0);  // 0 millisecond from 00:00:00 UTC, Thursday, 1 January 1970
    }

    public Commit(String msg, Map<String, String> fileMap, String parent){
        this.message = msg;
        this.timestamp = new Date();
        this.fileMap = fileMap;
        this.parent = parent;
    }

    public String getParent() {
        return this.parent;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.timestamp;
    }

    public Map<String, String> getFile() {
        return this.fileMap;
    }

    /* TODO: Something to track the file
    /* TODO: fill in the rest of this class. */
}
