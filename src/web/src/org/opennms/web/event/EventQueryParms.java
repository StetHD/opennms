
package org.opennms.web.event;

import java.util.ArrayList;
import org.opennms.web.event.filter.*;


/**
 * Convenience data structure for holding the arguments to an event query. 
 *
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 */
public class EventQueryParms extends Object 
{
    public EventFactory.SortStyle sortStyle;
    public EventFactory.AcknowledgeType ackType;
    public ArrayList filters;
    public int limit;
    public int multiple;


    /**
     * Convert the internal (and useful) ArrayList filters object
     * into an array of Filter instances.
     */ 
    public Filter[] getFilters() {
        Filter[] filters = new Filter[this.filters.size()];                        
        filters = (Filter[])this.filters.toArray( filters );
        return( filters );
    }
}
