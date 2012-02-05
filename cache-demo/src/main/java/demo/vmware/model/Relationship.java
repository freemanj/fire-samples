package demo.vmware.model;

/**
 * Describes the relationship between companies
 * 
 */
public class Relationship extends CrossAttribute {

    private String relationshipType;

    /** exists only to be used by Spring wiring */
    public Relationship() {
        super();
    }

    public Relationship(String pk, String id1, String id2, String type) {
        this.setPk(pk);
        this.setId1(id1);
        this.setId2(id2);
        this.setRelationshipType(type);
    }

    /* boring getters and setters */

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String type) {
        this.relationshipType = type;
    }

    public String toString() {
        return super.toString() + ",type:" + relationshipType;
    }

}
