package demo.vmware.model;

import java.io.Serializable;

/**
 * Demo note class for the notes tables.
 * <p>
 * Used Serializable to simplify demo. All objects must be serialized using one of several methods. PDX serialization
 * removes the need for this interface and does other nice stuff
 * 
 * @author freemanj
 * 
 */
public class SampleNote implements Serializable {

    private String key;
    private String noteString;

    public SampleNote(String key, String noteString) {
        this.key = key;
        this.noteString = noteString;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNoteString() {
        return noteString;
    }

    public void setNoteString(String noteString) {
        this.noteString = noteString;
    }

}
