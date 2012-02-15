package demo.vmware.datasync;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Operation;
import com.gemstone.gemfire.cache.util.GatewayEvent;
import com.gemstone.gemfire.cache.util.GatewayEventListener;

/**
 * This is a logging GatewayEventListener that shows the timing of operations. A lot of folks do write-behind using
 * cache gateway event listeners.
 * 
 * @author freemanj
 * 
 */
public class DummyGatewayEventListener implements GatewayEventListener, Declarable {

    Logger LOG = Logger.getLogger(DummyGatewayEventListener.class);

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean processEvents(List<GatewayEvent> eventList) {
        for (GatewayEvent ge : eventList) {
            LOG.info("Operation = " + ge.getOperation() + " for object (" + ge.getDeserializedValue().getClass()
                    + ") with key '" + ge.getKey() + "'");
            if (ge.getOperation().equals(Operation.UPDATE)) {
            } else if (ge.getOperation().equals(Operation.CREATE)) {
            } else if (ge.getOperation().equals(Operation.DESTROY)) {
            }

        }
        return true;
    }

    @Override
    public void init(Properties arg0) {
        // this is not the method you are looking for...
        LOG.info("initialized GatewayEventListener with " + arg0);
    }
}
