package demo.vmware.model;

import java.io.Serializable;

/**
 * Company model object using customer's naming convention
 * 
 * @author freemanj
 * 
 */
public class Attribute extends CoreModel implements Serializable {

    private String name;

    /** exists only to be used by Spring wiring */
    public Attribute() {
        super();
    }

    public Attribute(String pk, String name) {
        setPk(pk);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    };

    public String toString() {
        return "pk:" + getPk() + ",name:" + name;
    }
}
