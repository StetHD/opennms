package org.opennms.web.event.filter;


/** Convenience class to determine what sort of events to include in a query. */
public interface Filter 
{
    
    /** 
     * Returns an expresions for a SQL where clause.
     * Remember to include a trailing space, but no leading AND or OR. 
     */
    public String getSql();
    
    
    /**
     * Returns a terse string (including a "=") that describes this filter
     * in such a way to easily be included in an HTTP GET parameter.
     *
     * <p>Some examples: 
     *   <ul>
     *     <li>"node=1"</li>
     *     <li>"interface=192.168.0.1"</li>
     *     <li>"severity=3"</li>     
     *     <li>"nodenamelike=opennms"</li>     
     *   </ul>
     * </p>
     */
    public String getDescription();
    

    /** 
     * Returns a terse but human-readable string describing this filter in
     * such a way to easily be included in a search results list.
     *
     * <p>Some examples (corresponding to the examples in 
     * {@link #getDescription getDescription}): 
     *   <ul>
     *     <li>"node=nodelabel_of_node_1"</li>
     *     <li>"interface=192.168.0.1"</li>
     *     <li>"severity=Normal"</li>     
     *     <li>"node name containing \"opennms\""</li>     
     *   </ul>
     * </p>     
     */     
    public String getTextDescription();
}




