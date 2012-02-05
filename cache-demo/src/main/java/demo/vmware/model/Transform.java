package demo.vmware.model;

/**
 * Describes relationship changes made between Companies
 * 
 */
public class Transform extends CrossAttribute {

    private String transformType;

    /** exists only to be used by Spring wiring */
    public Transform() {
        super();
    }

    public Transform(String pk, String id1, String id2, String type) {
        this.setPk(pk);
        this.setId1(id1);
        this.setId2(id2);
        this.setTransformType(type);
    }

    /* boring getters and setters */

    public String getTransformType() {
        return transformType;
    }

    public void setTransformType(String type) {
        this.transformType = type;
    }

    public String toString() {
        return super.toString() + ",type:" + transformType;
    }
}
