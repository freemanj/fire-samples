package demo.vmware.commands.notes;

import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;

import demo.vmware.commands.CommandResult;
import demo.vmware.commands.ICommand;
import demo.vmware.model.SampleNote;

public class CommandAddNote implements ICommand {

    @Override
    public String commandDescription() {
        return "Add a quoted string (note) to the notes table";
    }

    @Override
    public String usageDescription() {
        return "<cmd> \"<note>\"";
    }

    @Override
    public int numberOfParameters() {
        return 1;
    }

    /**
     * @Return CommandResultSet containing the key to the stored note
     */
    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        Cache cache = (Cache) mainContext.getBean("cache");
        // We put the key in the model object and use as key to the region
        Region<String, SampleNote> region = cache.getRegion("NOTES-EXT-CONFIG");
        // pick some random key that is unlikely to be duplicated -- in really world we'd use GUID generator
        String key = new java.util.Date().toString();
        SampleNote note = new SampleNote(key, parameters.get(0));
        region.put(key, note);
        return new CommandResult(key, "Note saved");
    }
}
