package demo.vmware.commands;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import demo.vmware.commands.schemaspecific.CommandCreateData;
import demo.vmware.model.ActivityLegal;
import demo.vmware.model.Attribute;
import demo.vmware.model.Relationship;
import demo.vmware.model.Transform;

public class CommandCreateDataTest {

    Logger LOG = Logger.getLogger(CommandCreateDataTest.class);

    /**
     * Verify we create the number we expect
     */
    @Test
    public void testCreateSingle() {
        int numCompanies = 5;
        int numRegions = 2;
        int numGroups = 3;
        int numBranches = 4;
        CommandCreateData jig = new CommandCreateData(numCompanies, numRegions, numGroups, numBranches, null, null,
                null, null, null);
        Map<String, Attribute> createdAttributeMap = new HashMap<String, Attribute>();
        Map<String, Relationship> createdRelationshipMap = new HashMap<String, Relationship>();
        Map<String, ActivityLegal> activityLegalMap = new HashMap<String, ActivityLegal>();
        Map<String, Transform> transformMap = new HashMap<String, Transform>();

        jig.createCompanyHierarchy(numRegions, numGroups, numBranches, createdAttributeMap, createdRelationshipMap,
                activityLegalMap, transformMap);
        assertEquals(1 + numRegions + (numRegions * (numGroups + (numGroups * numBranches))),
                createdAttributeMap.size());
        assertEquals(0 + numRegions + (numRegions * (numGroups + (numGroups * numBranches))),
                createdRelationshipMap.size());
        assertEquals(0 + numRegions + (numRegions * (numGroups + (numGroups * numBranches))), activityLegalMap.size());
        LOG.info("attributes are: " + createdAttributeMap);
    }
}
