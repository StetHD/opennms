<%@page language="java" contentType = "text/html" session = "true" import="java.util.*,java.net.*,org.opennms.netmgt.config.EventconfFactory,org.opennms.netmgt.xml.eventconf.Event" %>

<%
    //there should be a list of autoActions in the session that we need to 
    //display and edit
    List autoActions = null;
    
    HttpSession user = request.getSession(false);
    
    if (user != null)
    {
        autoActions = (List)user.getAttribute("autoActions.editAutoActions.jsp");
        
        if (autoActions == null)
        {
            autoActions = new ArrayList();
        }
    }
%>

<html>
<head>
  <title>Edit Auto Actions | Event Config | OpenNMS Web Console</title>
  <base HREF="<%=org.opennms.web.Util.calculateUrlBase( request )%>" />
  <link rel="stylesheet" type="text/css" href="includes/styles.css" />
</head>

<script language="Javascript" type="text/javascript" >
    function validateValues()
    {
        for( i = 0; i < document.autoActions.elements.length; i++ ) 
        {
           if (document.autoActions.elements[i].type == "text")
           {
               if (document.autoActions.elements[i].value == "")
               {
                   alert("Please assign a value to each auto action or delete all blank actions.");
                   return false;
               }
           }
        }
        
        return true;
    }
    
    function validateValuesButIgnoreComponent(component)
    {
        for( i = 0; i < document.autoActions.elements.length; i++ ) 
        {
           if (document.autoActions.elements[i].name != component && document.autoActions.elements[i].type == "text")
           {
                if (document.autoActions.elements[i].value == "")
                {
                    alert("Please assign a value to each auto action or delete all blank actions.");
                    return false;
                }
           }
        }
        
        return true;
    }
    
    function updateActions(page) 
    {
        var valuesOK = validateValues();
        if (valuesOK)
        {
          document.autoActions.redirect.value = page;
          document.autoActions.action = "admin/eventconf/autoActions/updateAutoActions";
          document.autoActions.submit();
        }
    }
    
    function saveActions(page) 
    {
        var valuesOK = validateValues();
        if (valuesOK)
        {
          document.autoActions.redirect.value = page;
          document.autoActions.action = "admin/eventconf/autoActions/saveAutoActions";
          document.autoActions.submit();
        }
    }
    
    function deleteAction(component, index)
    {
        var confirmed = confirm("Are you sure you want to delete this action?");
        if (confirmed)
        {
            if (validateValuesButIgnoreComponent(component))
            {
              document.autoActions.deleteIndex.value = index;
              document.autoActions.redirect.value = "/admin/eventconf/autoActions/deleteAutoAction";
              document.autoActions.action = "admin/eventconf/autoActions/updateAutoActions";
              document.autoActions.submit();
            }
        }
    }
    
    function cancelAction()
    { 
      document.autoActions.action = "admin/eventconf/modify.jsp";
      document.autoActions.submit();
    }
</script>

<body marginwidth="0" marginheight="0" LEFTMARGIN="0" RIGHTMARGIN="0" TOPMARGIN="0">

<% String breadcrumb1 = java.net.URLEncoder.encode("<a href='admin/index.jsp'> Admin </a>"); %>
<% String breadcrumb2 = java.net.URLEncoder.encode("<a href='admin/eventconf/list.jsp'> Event Configuration </a>"); %>
<% String breadcrumb3 = java.net.URLEncoder.encode("Edit Event Auto Actions"); %>
<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Edit Event Auto Actions" />
  <jsp:param name="location" value="admin" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb1%>" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb2%>" />
  <jsp:param name="breadcrumb" value="<%=breadcrumb3%>" />
</jsp:include>

<br>

<table width="100%" border="0" cellspacing="0" cellpadding="2" >
  <tr>
    <td>&nbsp;</td>
    
      <td width="100%" valign="top" >
          
          <!-- mask information -->
          <FORM METHOD="POST" NAME="autoActions" >
          <input type="hidden" name="redirect" />
          <input type="hidden" name="deleteIndex" />
          <input type="hidden" name="rows" value="<%=autoActions.size()%>">
          <input type="button" value="Add New Action" onclick="updateActions('/admin/eventconf/autoActions/newAutoAction')"/>
            
            <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
              <tr bgcolor="#999999">
                <td WITDH="15%"><b>Delete</b> </td>
                <td WIDTH="5%"><b>State</b> </td>
                <td WIDTH="95%"><b>Command</b> </td>
              </tr>
              
              <!-- auto action information -->
              <%
                for (int i = 0; i < autoActions.size(); i++) 
                {
                    AutoAction curAction = (AutoAction)autoActions.get(i);
               %>
                  <tr bgcolor=<%=i%2==0 ? "#ffffff" : "#cccccc"%>>
                    <td width="5%" align="center"> 
                            <a href="javascript:deleteAction('<%="action"+i%>', '<%=i%>')"> <img  src="images/trash.gif" ></a>
                    </td>
                    <td>
                        <select name="<%="state"+i%>" size="1"> <%=buildSelectOptions(AutoAction.AUTO_ACTION_STATES, curAction.getState())%>
                        </select>
                    </td>
                    
                    <td><input type="text" size="100" name="<%="action"+i%>" value='<%=curAction.getAutoAction()%>'></td>
                  </tr>
             <% } /*end for loop*/ 
              %>
             
            </table>
            
            <input type="button" value="    OK   " onclick="saveActions('/admin/eventconf/modify.jsp')"> &nbsp;
            <input type="button" value="Cancel" onclick="cancelAction()">
            </FORM>
         
      </td>
    
    <td> &nbsp; </td>
  </tr>
</table>

<br>

<jsp:include page="/includes/footer.jsp" flush="true" >
  <jsp:param name="location" value="admin" />
</jsp:include>
</body>
</html>

<%!
   public String buildSelectOptions(String values[], String selected)
   {
      StringBuffer buffer = new StringBuffer();
      
      for (int i = 0; i < values.length; i++)
      {
          if (selected.equals(values[i])) 
          {
              buffer.append("<option value=\"").append(values[i]+"\"").append(" selected>").append(values[i]).append("</options>");
          }
          else 
          {
             buffer.append("<option value=\"").append(values[i]+"\">").append(values[i]).append("</options>");
          }
      }
      
      return buffer.toString();
   }
%>
