package demo.vmware.model;


/**
 * Every class in the demonstration uses the same primary key definition , a String called pk. Real business models
 * probably don't have a unified key this works well for an example.
 * <p>
 * This also gives us a simple way to implement IAutoCacheKey so that we can have one place to return the cache key
 * value for all our sample model objects. Real business models might use compound objects or synthetic attributes for
 * cache keys.
 * 
 * @author freemanj
 * 
 */
public class CoreModel {

    /** simple string primary key */
    private String pk;

    public void setPk(String id) {
        this.pk = id;
    }

    public String getPk() {
        return pk;
    }

}
