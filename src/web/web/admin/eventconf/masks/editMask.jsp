<%@page language="java" contentType = "text/html" session = "true" import="java.util.*,java.net.*,org.opennms.bb.common.bobjects.eventconf.*,org.opennms.bb.common.admin.eventconf.*" %>

<%
    //there should be a list of MaskElements in the session that we need to 
    //display and edit
    List maskElements = null;
    
    HttpSession user = request.getSession(false);
    
    
    if (user != null)
    {
        maskElements = (List)user.getAttribute("maskElements.editMask.jsp");
        
        if (maskElements == null)
        {
            maskElements = new ArrayList();
        }
    }
%>

<html>
<head>
  <title>Edit Event Mask | Event Config | OpenNMS Web Console</title>
  <base HREF="<%=org.opennms.web.Util.calculateUrlBase( request )%>" />
  <link rel="stylesheet" type="text/css" href="includes/styles.css" />
</head>

<script language="Javascript" type="text/javascript" >
    function validateValues()
    {
        for( i = 0; i < document.mask_elements.elements.length; i++ ) 
        {
           if (document.mask_elements.elements[i].type == "textarea")
           {
              if (document.mask_elements.elements[i-1].type == "select-one")
              {
                var index = document.mask_elements.elements[i-1].selectedIndex;
                var name = document.mask_elements.elements[i-1].options[index].value;
                
                if (document.mask_elements.elements[i].value == "")
                {
                    alert("The mask element '"+name+"' must have at least one value assigned.");
                    return false;
                }
              }
           }
        }
        
        return true;
    }
    
    function validateValuesButIgnoreComponent(component)
    {
        for( i = 0; i < document.mask_elements.elements.length; i++ ) 
        {
           if (document.mask_elements.elements[i].name != component && document.mask_elements.elements[i].type == "textarea")
           {
              if (document.mask_elements.elements[i-1].type == "select-one")
              {
                var index = document.mask_elements.elements[i-1].selectedIndex;
                var name = document.mask_elements.elements[i-1].options[index].value;
                
                if (document.mask_elements.elements[i].value == "")
                {
                    alert("The mask element '"+name+"' must have at least one value assigned.");
                    return false;
                }
              }
           }
        }
        
        return true;
    }
    
    function updateElements(page) 
    {
        var valuesOK = validateValues();
        if (valuesOK)
        {
          document.mask_elements.redirect.value = page;
          document.mask_elements.action = "admin/eventconf/masks/updateMaskElements";
          document.mask_elements.submit();
        }
    }
    
    function saveElements(page) 
    {
        var valuesOK = validateValues();
        if (valuesOK)
        {
          document.mask_elements.redirect.value = page;
          document.mask_elements.action = "admin/eventconf/masks/saveMaskElements";
          document.mask_elements.submit();
        }
    }
    
    function deleteElement(component, index)
    {
        var confirmed = confirm("Are you sure you want to delete this element?");
        if (confirmed)
        {
            if (validateValuesButIgnoreComponent(component))
            {
              document.mask_elements.deleteIndex.value = index;
              document.mask_elements.redirect.value = "/admin/eventconf/masks/deleteElement";
              document.mask_elements.action = "admin/eventconf/masks/updateMaskElements";
              document.mask_elements.submit();
            }
        }
    }
    
    function cancelElements()
    {
        document.mask_elements.action="admin/eventconf/modify.jsp"
        document.mask_elements.submit();
    }
</script>

<body marginwidth="0" marginheight="0" LEFTMARGIN="0" RIGHTMARGIN="0" TOPMARGIN="0">

<% String breadcrumb1 = java.net.URLEncoder.encode("<a href='admin/index.jsp'> Admin </a>"); %>
<% String breadcrumb2 = java.net.URLEncoder.encode("<a href='admin/eventconf/list.jsp'> Event Configuration </a>"); %>
<% String breadcrumb3 = java.net.URLEncoder.encode("Edit Event Mask"); %>
<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Edit Event Mask" />
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
          <FORM METHOD="POST" NAME="mask_elements" >
          <input type="hidden" name="redirect" />
          <input type="hidden" name="deleteIndex" />
          <input type="button" value="Add New Element" onclick="updateElements('/admin/eventconf/masks/newMaskElement')"/>
            
            <table width="100%" border="1" cellspacing="0" cellpadding="2" bordercolor="black">
              <tr bgcolor="#999999">
                <td width="15%"><b>Delete</b></td>
                <td width="10%"><b>Name</b></td>
                <td><b>Values (one per line)</b></td>
              </tr>
              
                 <% for (int i = 0; i < maskElements.size(); i++)
                    {
                       MaskElement curElement = (MaskElement)maskElements.get(i);
                       String name = curElement.getElementName();
                       List maskValues = curElement.getElementValues();
                       StringBuffer valuesBuffer = new StringBuffer();
                  %>
                    
                      
                        <tr>
                        
                          <td width="5%" align="center"> 
                            <a href="javascript:deleteElement('<%="mask"+i+"Values"%>', '<%=i%>')"> <img  src="images/trash.gif" ></a>
                          </td>
                          
                         <%  String names[] = {"uei","source","host","snmphost","nodeid","interface","service","eid"};
                         %>
                         <td width="10%"  valign="top">
                              <select name="mask" size="1"> <%=buildSelectOptions(names,name)%>
                              </select>
                         </td>
                         
                         <%
                           for (int j = 0; j < maskValues.size(); j++) 
                           {
                              valuesBuffer.append((String)maskValues.get(j)+"\n");
                           } %>
                           
                           <td><textarea name=<%="mask"+i+"Values"%> cols="100" rows="5" wrap="hard"><%=valuesBuffer.toString()%></textarea>
                           </td>
                        </tr>
                
                  <%} /* end for loop*/ %>
                
            </table>
            <input type="button" value="    OK   " onclick="saveElements('/admin/eventconf/modify.jsp')">
            <input type="button" value="Cancel" onclick="cancelElements()">
            </FORM>
                
         <br>
      
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
