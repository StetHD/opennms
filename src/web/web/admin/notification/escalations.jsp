<%@page language="java" contentType="text/html" session="true" import="java.util.*,org.opennms.web.Util,org.opennms.netmgt.config.*,org.opennms.netmgt.config.destinationPaths.*" %>

<%!
    public void init() throws ServletException {
        try {
            UserFactory.init();
            GroupFactory.init();
            DestinationPathFactory.init();
            NotificationCommandFactory.init();
        }
        catch( Exception e ) {
            throw new ServletException( "Cannot load configuration file", e );
        }
    }
    
    String pathName = null;
    Path path = null;
    String times[] = {"0m", "15m", "30m", "1h", "2h", "3h", "6h", "12h", "1d"};
%>

<%
    pathName = request.getParameter("path");
    path = DestinationPathFactory.getInstance().getPath(pathName);
    if (path == null)
      path = new Path();
%>

<html>
<head>
  <title>Group Intervals | Admin | OpenNMS Web Console</title>
  <base HREF="<%=org.opennms.web.Util.calculateUrlBase( request )%>" />
  <link rel="stylesheet" type="text/css" href="includes/styles.css" />
</head>

<script language="Javascript" type="text/javascript" >

    function next() 
    {
        document.targets.submit();
    }

</script>

<body marginwidth="0" marginheight="0" LEFTMARGIN="0" RIGHTMARGIN="0" TOPMARGIN="0">

<% String breadcrumb1 = java.net.URLEncoder.encode("<a href='admin/index.jsp'>Admin</a>"); %>
<% String breadcrumb2 = java.net.URLEncoder.encode("Commands"); %>
<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Commands" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb1%>" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb2%>" />
</jsp:include>

<br>
<!-- Body -->

<table width="100%" cellspacing="0" cellpadding="0" border="0">
  <tr>
    <td> &nbsp; </td>

    <td>
    <h3><%=(pathName!=null ? "Editing path " + pathName + "<br>" : "")%></h3>
    <h3>Step 4: Add escalations to the path.</h3>
    <form METHOD="POST" NAME="escalations" ACTION="" >
      <%=Util.makeHiddenTags(request)%>
      <table width="50%" cellspacing="2" cellpadding="2" border="0">
        <tr>
          <td valign="top" align="left">
            <%=displayTargets(request.getParameterValues("users"), request.getParameterValues("groups"))%>
          </td>
          <td valign="top" align="left">
            <%=displayEscalations()%>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <input type="button" value="Add Escalation"/>
          </td>
        </tr>
        <tr>
          <td colspan="2">
           <a HREF="javascript:document.commands.submit()">Next &#155;&#155;&#155;</a>
          </td>
        </tr>
      </table>
    </form>
    </td>

    <td> &nbsp; </td>
  </tr>
</table>

<br>

<jsp:include page="/includes/footer.jsp" flush="false" />

</body>
</html>

<%!
    public String displayTargets(String users[], String groups[])
    {
        if (users==null)
            return "";
            
        StringBuffer buffer = new StringBuffer("<h2>Initial Targets</h2>");
        buffer.append("<table width=\"100%\" cellspacing=\"2\" cellpadding=\"2\" border=\"0\">");
        
        for (int i = 0; i < users.length; i++)
        {
            buffer.append("<tr><td>").append(users[i]).append("</td>").append("</tr>");
        }
        
        for (int i = 0; i < groups.length; i++)
        {
            buffer.append("<tr><td>").append(groups[i]).append("</td>").append("</tr>");
        }
        
        buffer.append("</table>");
        return buffer.toString();
    }
    
    public String displayEscalations()
    {
        
        StringBuffer buffer = new StringBuffer("<h2>Escalation Targets</h2>");
        buffer.append("<table width=\"100%\" cellspacing=\"2\" cellpadding=\"2\" border=\"0\">");
        
        buffer.append("</table>");
        return buffer.toString();
    }
%>
