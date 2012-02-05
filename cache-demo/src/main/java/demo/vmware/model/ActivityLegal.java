package demo.vmware.model;

/**
 * Some type of legal event
 */
public class ActivityLegal extends CrossAttribute {

    private String activityType;

    /** exists only to be used by Spring wiring */
    public ActivityLegal() {
        super();
    }

    public ActivityLegal(String pk, String id1, String id2, String type) {
        this.setPk(pk);
        this.setId1(id1);
        this.setId2(id2);
        this.setActivityType(type);
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String type) {
        this.activityType = type;
    }

    public String toString() {
        return super.toString() + ",type:" + activityType;
    }

}
