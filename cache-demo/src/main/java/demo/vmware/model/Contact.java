package demo.vmware.model;

import java.io.Serializable;

/**
 * A contact at a company
 */
public class Contact extends CoreModel implements Serializable {

    /** foreign key to the Attribute (Company ) */
    private String id1;

    private String firstName;
    private String lastName;

    private String phone;

    private boolean active;;

    /** exists only to be used by Spring wiring */
    public Contact() {
        super();
    }

    public Contact(String pk, String id1, String firstName, String lastName, String phone, boolean active) {
        this.setPk(pk);
        this.setId1(id1);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPhone(phone);
        this.setActive(active);
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String fk1) {
        this.id1 = fk1;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return "pk:" + getPk() + ",id1:" + id1 + ",firstName:" + firstName + ",lastName:" + lastName + ",phone:"
                + phone + ",active:" + active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
