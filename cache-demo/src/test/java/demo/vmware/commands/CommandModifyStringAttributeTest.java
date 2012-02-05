package demo.vmware.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import demo.vmware.model.Contact;

public class CommandModifyStringAttributeTest {

    @Test
    public void testSetString() {
        Contact contact = new Contact("a", "fk", "first", "last", "800-555-1212", false);
        CommandModifyStringAttribute jig = new CommandModifyStringAttribute();
        jig.setValue(contact, "pk", "newKey");
        assertEquals("newKey", contact.getPk());
    }
}
