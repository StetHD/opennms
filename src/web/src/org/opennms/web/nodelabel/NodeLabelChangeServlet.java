package org.opennms.web.nodelabel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.opennms.core.resource.Vault;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.xml.event.*;
import org.opennms.netmgt.utils.EventProxy;
import org.opennms.netmgt.utils.TcpEventProxy;
import org.opennms.netmgt.utils.NodeLabel;
import org.opennms.web.MissingParameterException;


/**
 * Changes the label of a node, throws an event signalling that change,
 * and then redirects the user to a web page displaying that node's details.
 *
 * @author <A HREF="larry@opennms.org">Larry Karnowski</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 */
public class NodeLabelChangeServlet extends HttpServlet
{

    protected EventProxy proxy;

    public void init() throws ServletException {
        try {
            	this.proxy = new TcpEventProxy();
        }
        catch( Exception e ) {
            throw new ServletException( "JMS Exception", e );
        }
    }

    
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        String nodeIdString = request.getParameter( "node" );
        String labelType = request.getParameter( "labeltype" );
        String userLabel = request.getParameter( "userlabel" );
        
        if( nodeIdString == null ) {
            throw new MissingParameterException( "node", new String[] {"node", "labeltype", "userlabel"} );
        }
        if( labelType == null ) {
            throw new MissingParameterException( "labeltype", new String[] {"node", "labeltype", "userlabel"} );
        }
        if( userLabel == null ) {
            throw new MissingParameterException( "userlabel", new String[] {"node", "labeltype", "userlabel"} );
        }
        
        try {
            int nodeId = Integer.parseInt( nodeIdString );
            NodeLabel oldLabel = NodeLabel.retrieveLabel( nodeId );
            NodeLabel newLabel = null;
            
            if( labelType.equals( "auto" )) {
                newLabel = NodeLabel.computeLabel( nodeId );
            }
            else if( labelType.equals( "user" )) {
                newLabel = new NodeLabel( userLabel, NodeLabel.SOURCE_USERDEFINED );
            }    
            else {
                throw new ServletException( "Unexpected labeltype value: " + labelType );
            }
            
            NodeLabel.assignLabel( nodeId, newLabel );
            this.sendLabelChangeEvent( nodeId, oldLabel, newLabel );
            response.sendRedirect( request.getContextPath() + "/element/node.jsp?node=" + nodeIdString );
        }
        catch( SQLException e ) {
            throw new ServletException( "Database exception", e );
        }
        catch( Exception e ) {
            throw new ServletException( "Exception sending node label change event", e );
        }
    }
        

    protected void sendLabelChangeEvent( int nodeId, NodeLabel oldNodeLabel, NodeLabel newNodeLabel ) {
        Event outEvent = new Event();
        outEvent.setSource("NodeLabelChangeServlet");        
        outEvent.setUei(EventConstants.NODE_LABEL_CHANGED_EVENT_UEI);
        outEvent.setNodeid(nodeId);
        outEvent.setHost("host");
	outEvent.setTime(EventConstants.formatToString(new java.util.Date()));
        
        Parms parms = new Parms();
        
        if( oldNodeLabel != null ) {
            // old label
            Value value = new Value();
            value.setContent(oldNodeLabel.getLabel());
            Parm parm = new Parm();
            parm.setParmName(EventConstants.PARM_OLD_NODE_LABEL);
            parm.setValue(value);
            parms.addParm(parm);
            
            // old label source
            value = new Value();
            value.setContent(String.valueOf(oldNodeLabel.getSource()));
            parm = new Parm();
            parm.setParmName(EventConstants.PARM_OLD_NODE_LABEL_SOURCE);
            parm.setValue(value);
            parms.addParm(parm);
        }
        
        if( newNodeLabel != null ) {
            // new label
            Value value = new Value();
            value.setContent(newNodeLabel.getLabel());
            Parm parm = new Parm();
            parm.setParmName(EventConstants.PARM_NEW_NODE_LABEL);
            parm.setValue(value);
            parms.addParm(parm);
            
            // old label source
            value = new Value();
            value.setContent(String.valueOf(newNodeLabel.getSource()));
            parm = new Parm();
            parm.setParmName(EventConstants.PARM_NEW_NODE_LABEL_SOURCE);
            parm.setValue(value);
            parms.addParm(parm);            
        }
        
        outEvent.setParms(parms);
        
        this.proxy.send( outEvent );
    }
        
}

