<%-- 
  This page is included by other JSPs to create a box containing an
  abbreviated list of outages.
  
  It expects that a <base> tag has been set in the including page
  that directs all URLs to be relative to the servlet context.
--%>

<%@page language="java" contentType="text/html" session="true" import="org.opennms.web.outage.*,java.util.*" %>

<%! 
    OutageModel model = new OutageModel();
%>

<%
    //required parameter node
    String nodeIdString = request.getParameter("node");
    
    if( nodeIdString == null ) {
        throw new org.opennms.web.MissingParameterException("node", new String[] {"node", "intf"}); 
    }

    //required parameter intf
    String ipAddr = request.getParameter("intf");

    if( ipAddr == null ) {
        throw new org.opennms.web.MissingParameterException("intf", new String[] {"node", "intf"}); 
    }
    
    int nodeId = Integer.parseInt(nodeIdString);

    //determine yesterday's respresentation
    Calendar cal = new GregorianCalendar();
    cal.add( Calendar.DATE, -1 );
    Date yesterday = cal.getTime();

    //gets all current outages and outages that have been resolved within the
    //the last 24 hours
    Outage[] outages = this.model.getOutagesForInterface(nodeId, ipAddr, yesterday);    
%>

<table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black" BGCOLOR="#cccccc">
  

<% if(outages.length == 0) { %>
  <tr>
    <td BGCOLOR="#999999" colspan="3">There have been no outages on this interface in the last 24 hours.</b></td>
  </tr>
<% } else { %>
  <tr> 
    <td BGCOLOR="#999999" colspan="3"><b>Recent Outages</b></td>
  </tr>
  <tr bgcolor="#999999">
    <td><b>Service</b></td>
    <td><b>Lost</b></td>
    <td><b>Regained</b></td>
  </tr>
  <% for( int i=0; i < outages.length; i++ ) { %>
     <tr>
      <td><a href="element/service.jsp?node=<%=nodeId%>&intf=<%=outages[i].getIpAddress()%>&service=<%=outages[i].getServiceId()%>"><%=outages[i].getServiceName()%></a></td>
      <td><%=org.opennms.netmgt.EventConstants.formatToUIString(outages[i].getLostServiceTime())%></td>
      <% if( outages[i].getRegainedServiceTime() == null ) { %>
        <td bgcolor="red"><b>DOWN</b></td>
      <% } else { %>        
        <td><%=org.opennms.netmgt.EventConstants.formatToUIString(outages[i].getRegainedServiceTime())%></td>
      <% } %>        
     </tr>
  <% } %>
<% } %>

</table>
