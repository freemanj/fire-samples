package demo.vmware.model;

import java.io.Serializable;

/**
 * parent class for model objects that have a primary key and join to two other classes via foreign keys
 * 
 * @author freemanj
 * 
 */
public class CrossAttribute extends CoreModel implements Serializable {

    private String id1;
    private String id2;

    public CrossAttribute() {
        super();
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getId1() {
        return id1;
    }

    @Override
    public String toString() {
        return "pk:" + getPk() + ",id1:" + id1 + ",id2:" + id2;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

}