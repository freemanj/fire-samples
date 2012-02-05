/*
 * Copyright 2011 VMWare.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.vmware.commands.schemaspecific;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

import demo.vmware.commands.CommandResult;
import demo.vmware.commands.CommandTimer;
import demo.vmware.commands.ICommand;
import demo.vmware.model.ActivityLegal;
import demo.vmware.model.Attribute;
import demo.vmware.model.Contact;
import demo.vmware.model.Relationship;
import demo.vmware.model.Transform;

/**
 * Fills the data with dummy tables builds companies with a relationship hierarchy
 * company(parent)-->company(region)-->company(group)-->company(branch) then stores all the companies (Attribute) and
 * relationships (Relationship) in the cache
 * 
 * @author freemanj
 * 
 */
public class CommandCreateData implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandCreateData.class);

    /** cheap running company id generator */
    AtomicInteger companyCounter = new AtomicInteger();
    int numCompanies;
    int numRegions;
    int numGroups;
    int numBranches;
    GemfireTemplate relationshipTemplate;
    GemfireTemplate attributeTemplate;
    GemfireTemplate contactTemplate;

    GemfireTemplate activityLegalTemplate;

    GemfireTemplate transformTemplate;

    /**
     * configure through constructor injections
     * 
     * @param numCompanies
     * @param numRegions
     * @param numGroups
     * @param numBranches
     */
    public CommandCreateData(int numCompanies, int numRegions, int numGroups, int numBranches,
            GemfireTemplate attributeTemplate, GemfireTemplate relationshipTemplate, GemfireTemplate contactTemplate,
            GemfireTemplate activityLegalTemplate, GemfireTemplate transformTemplate) {
        this.numCompanies = numCompanies;
        this.numRegions = numRegions;
        this.numGroups = numGroups;
        this.numBranches = numBranches;
        this.attributeTemplate = attributeTemplate;
        this.relationshipTemplate = relationshipTemplate;
        this.contactTemplate = contactTemplate;
        this.activityLegalTemplate = activityLegalTemplate;
        this.transformTemplate = transformTemplate;
    }

    @Override
    public String commandDescription() {
        return "Create random data and stuff into Gemfire Cluster";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    /**
     * Simple method that returns a new activity legal object to join to entitites
     * 
     * @param pk
     * @param id1
     * @param id2
     * @param type
     * @return
     */
    ActivityLegal createActivityLegal(String pk, String id1, String id2, String type) {
        return new ActivityLegal(pk, id1, id2, type);
    }

    /**
     * Creates the child company and attaches to parents and puts in maps
     * 
     * @param parentCompany
     * @param companyName
     * @param createdAttributeMap
     * @param createdRelationshipMap
     * @return
     */
    Attribute createAndAttachChildCompany(Attribute parentCompany, String thisBranchNumberPrefix,
            Map<String, Attribute> createdAttributeMap, Map<String, Relationship> createdRelationshipMap,
            Map<String, ActivityLegal> createdActivityMap, Map<String, Transform> createdTransformMap) {
        int thisCompanyNumber = companyCounter.incrementAndGet();
        String hierarchicalNamePrefix;
        if (parentCompany != null) {
            hierarchicalNamePrefix = parentCompany.getName();
        } else {
            // could just use "" but use this so we can recognize top level nodes
            hierarchicalNamePrefix = "Root";
        }
        String companyName = hierarchicalNamePrefix + "_" + thisBranchNumberPrefix + "-" + thisCompanyNumber;
        Attribute oneCompany = createCompany(thisCompanyNumber, companyName);
        createdAttributeMap.put(oneCompany.getPk(), oneCompany);
        // now create the relationship to the parent.
        if (parentCompany != null) {
            Relationship oneRelationship = createRelationship("" + companyCounter.incrementAndGet(),
                    parentCompany.getPk(), oneCompany.getPk(), "child");
            createdRelationshipMap.put(oneRelationship.getPk(), oneRelationship);

            // should be more creative but I'm lazy.
            Transform oneTransform = createTransform("" + companyCounter.incrementAndGet(), parentCompany.getPk(),
                    oneCompany.getPk(), "created relationship");
            createdTransformMap.put(oneTransform.getPk(), oneTransform);

            ActivityLegal oneActivity = createActivityLegal("" + companyCounter.incrementAndGet(),
                    parentCompany.getPk(), oneCompany.getPk(), "child");
            createdActivityMap.put(oneActivity.getPk(), oneActivity);
        }

        return oneCompany;
    }

    void createBranchCompany(Attribute parentCompany, Map<String, Attribute> createdAttributeMap,
            Map<String, Relationship> createdRelationshipMap, Map<String, ActivityLegal> createdActivityMap,
            Map<String, Transform> createdTransformMap) {
        Attribute oneCompany = createAndAttachChildCompany(parentCompany, "Branch", createdAttributeMap,
                createdRelationshipMap, createdActivityMap, createdTransformMap);
    }

    /**
     * Simple method that creates an Attribute object
     * 
     * @param id
     * @param companyName
     * @return
     */
    Attribute createCompany(int id, String companyName) {
        return new Attribute("" + id, companyName);
    }

    public void createCompanyHierarchy(int numRegions2, int numGroups2, int numBranches2,
            Map<String, Attribute> createdAttributeMap, Map<String, Relationship> createdRelationshipMap,
            Map<String, ActivityLegal> createdActivityMap, Map<String, Transform> createdTransformMap) {
        // top level company has no parent relationship -- they only exist with peers or children
        Attribute oneCompany = createAndAttachChildCompany(null, "Company", createdAttributeMap,
                createdRelationshipMap, createdActivityMap, createdTransformMap);

        // create all the regional companies
        for (int i = 0; i < numRegions2; i++) {
            createRegionCompanies(oneCompany, numGroups2, numBranches2, createdAttributeMap, createdRelationshipMap,
                    createdActivityMap, createdTransformMap);
        }

    }

    String firstNames[] = { "Robert", "Mandy", "Sajid", "Morgan", "Melvin", "Ellen", "Rupi", "Bill", "Ann", "Barak" };
    String lastNames[] = { "Smith", "Jones", "Wang", "Ambore", "Madison" };

    /**
     * Creates a single contact (primary?)
     * 
     * @param createdAttributeMap
     */
    private Map<String, Contact> createContactsFor(Map<String, Attribute> createdAttributeMap) {
        Map<String, Contact> createdContacts = new HashMap<String, Contact>();
        for (Attribute oneAttribute : createdAttributeMap.values()) {
            Contact oneContact = new Contact("" + companyCounter.incrementAndGet(), oneAttribute.getPk(),
                    firstNames[companyCounter.intValue() % firstNames.length], lastNames[companyCounter.intValue()
                            % lastNames.length], "800-555-" + (companyCounter.intValue() % 1000), true);
            createdContacts.put(oneContact.getPk(), oneContact);
        }
        return createdContacts;
    }

    void createGroupCompanies(Attribute parentCompany, int numBranches2, Map<String, Attribute> createdAttributeMap,
            Map<String, Relationship> createdRelationshipMap, Map<String, ActivityLegal> createdActivityMap,
            Map<String, Transform> createdTransformMap) {
        Attribute oneCompany = createAndAttachChildCompany(parentCompany, "Group", createdAttributeMap,
                createdRelationshipMap, createdActivityMap, createdTransformMap);
        // create all the group companies
        for (int i = 0; i < numBranches2; i++) {
            createBranchCompany(oneCompany, createdAttributeMap, createdRelationshipMap, createdActivityMap,
                    createdTransformMap);
        }
    }

    void createRegionCompanies(Attribute parentCompany, int numGroups2, int numBranches2,
            Map<String, Attribute> createdAttributeMap, Map<String, Relationship> createdRelationshipMap,
            Map<String, ActivityLegal> createdActivityMap, Map<String, Transform> createdTransformMap) {
        Attribute oneCompany = createAndAttachChildCompany(parentCompany, "Region", createdAttributeMap,
                createdRelationshipMap, createdActivityMap, createdTransformMap);
        // create all the group companies
        for (int i = 0; i < numGroups2; i++) {
            createGroupCompanies(oneCompany, numBranches2, createdAttributeMap, createdRelationshipMap,
                    createdActivityMap, createdTransformMap);
        }
    }

    /**
     * Simple method that returns a new relationship object
     * 
     * @param pk
     * @param id1
     * @param id2
     * @param type
     * @return
     */
    Relationship createRelationship(String pk, String id1, String id2, String type) {
        return new Relationship(pk, id1, id2, type);
    }

    /* ---------------------------------------- */
    /* forgive me for this bit of ugly code! */
    /* ---------------------------------------- */

    /**
     * Simple method that returns a new relationship object
     * 
     * @param pk
     * @param id1
     * @param id2
     * @param type
     * @return
     */
    Transform createTransform(String pk, String id1, String id2, String type) {
        return new Transform(pk, id1, id2, type);
    }

    @Override
    public int numberOfParameters() {
        return 0;
    }

    private void populateCacheWithCompanyHierarchy(Map<String, Attribute> createdAttributeMap,
            Map<String, Relationship> createdRelationshipMap, Map<String, ActivityLegal> createdActivityMap,
            Map<String, Transform> createdTransformMap) {
        // stuff them all in the cache
        attributeTemplate.putAll(createdAttributeMap);
        relationshipTemplate.putAll(createdRelationshipMap);
        activityLegalTemplate.putAll(createdActivityMap);
        transformTemplate.putAll(createdTransformMap);
        // Create a contact for each company/attribute -- this is in the wrong method / place :-(
        Map<String, Contact> createdContacts = createContactsFor(createdAttributeMap);
        contactTemplate.putAll(createdContacts);
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        List<String> messages = new ArrayList<String>();

        // lets us track cache growth
        int attributeCounter = 0;

        CommandTimer timer = new CommandTimer();
        LOG.info("creating test data for " + numCompanies + "," + numRegions + "," + numGroups + "," + numBranches);
        // use the Java executor service because of it's awesome invokeAll method.
        // we have 4 cores but gemfire needs some. 2 is probably a more realistic number
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        Collection tasks = new ArrayList<CompanyHierarchyPopulator>();

        for (int i = 0; i < numCompanies; i++) {
            // add a task for each company we are creating
            CompanyHierarchyPopulator poper = new CompanyHierarchyPopulator();
            tasks.add(poper);
        }

        try {
            // run all the tasks in execution pool
            List<Future<?>> futures = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();

            // aggregate the results from the call() method
            for (int i = 0; i < numCompanies; i++) {
                // should get resulting messages also -- sometime in the future
                attributeCounter += ((Integer) futures.get(i).get()).intValue();
            }

            timer.stop();
            messages.add("Created " + attributeCounter + " attributes and " + " took " + timer.getTimeDiffInSeconds()
                    + " sec");
        } catch (ExecutionException e) {
            // this should never happen
            LOG.warn("something bad happend", e);
            messages.add("Something bad happend " + e.getMessage());
        } catch (InterruptedException e) {
            // this should never happen
            LOG.warn("something bad happend", e);
            messages.add("Something bad happend " + e.getMessage());
        }
        return new CommandResult(null, messages);
    }

    /**
     * Simple thread executor that lets us create a whole company from a thread pool.
     * <p>
     * ASSUMPTION: all instance methods are thread safe
     * 
     */
    private class CompanyHierarchyPopulator implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            Map<String, Attribute> createdAttributeMap = new HashMap<String, Attribute>();
            Map<String, Relationship> createdRelationshipMap = new HashMap<String, Relationship>();
            Map<String, ActivityLegal> createdActivityMap = new HashMap<String, ActivityLegal>();
            Map<String, Transform> createdTransformMap = new HashMap<String, Transform>();
            createCompanyHierarchy(numRegions, numGroups, numBranches, createdAttributeMap, createdRelationshipMap,
                    createdActivityMap, createdTransformMap);
            LOG.debug("Putting attributes:" + createdAttributeMap.keySet().size() + ", relationships:"
                    + createdRelationshipMap.keySet().size());
            populateCacheWithCompanyHierarchy(createdAttributeMap, createdRelationshipMap, createdActivityMap,
                    createdTransformMap);
            LOG.info("Total Size after put Attribute:" + attributeTemplate.getRegion().size() + " Relationship:"
                    + relationshipTemplate.getRegion().size() + " Contact:" + contactTemplate.getRegion().size());

            return createdAttributeMap.keySet().size();
        }
    }

}
