package org.opennms.web.event;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.opennms.web.MissingParameterException;
import org.opennms.web.Util;
import org.opennms.web.event.filter.*;


/**
 * This servlet takes a large and specific request parameter set and maps it 
 * to the more robust "filter" parameter set of the 
 * {@link EventFilterServlet EventFilterServlet} via a redirect.  
 *
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 */
public class EventQueryServlet extends HttpServlet
{
    /** 
     * The list of parameters that are extracted by this servlet and not passed
     * on to the {@link EventFilterServlet EventFilterServlet}.
     */
    protected static String[] IGNORE_LIST = new String[] {
        "msgsub", 
        "msgmatchany",
        "nodenamelike", 
        "service", 
        "iplike", 
        "severity", 
        "relativetime",
        "usebeforetime",
        "beforehour",
        "beforeminute",
        "beforeampm",
        "beforedate",
        "beforemonth",
        "beforeyear",
        "useaftertime",
        "afterhour",
        "afterminute",
        "afterampm",
        "afterdate",
        "aftermonth",
        "afteryear"            
    };    

    /** The URL for the {@link EventFilterServlet EventFilterServlet}.  The default
     * is "list."  This URL is a sibling URL, so it is relative to the URL 
     * directory that was used to call this servlet (usually "event/").
     */
    protected String redirectUrl = "filter";
    
    
    public void init() throws ServletException {
        ServletConfig config = this.getServletConfig();
        
        if(config.getInitParameter("redirect.url") != null ) {
            this.redirectUrl = config.getInitParameter("redirect.url");
        }
    }
    
    
    /**
     * Extracts the key parameters from the parameter set, translates them into
     * filter-based parameters, and then passes the modified parameter set to 
     * the {@link EventFilterServlet EventFilterServlet}.
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        ArrayList filterArray = new ArrayList();
                
        //convenient syntax for LogMessageSubstringFilter
        String msgSubstring = request.getParameter("msgsub");
        if(msgSubstring != null && msgSubstring.length() > 0) {
            filterArray.add(new LogMessageSubstringFilter(msgSubstring));
        }

        //convenient syntax for LogMessageMatchesAnyFilter
        String msgMatchAny = request.getParameter("msgmatchany");
        if(msgMatchAny != null && msgMatchAny.length() > 0) {
            filterArray.add(new LogMessageMatchesAnyFilter(msgMatchAny));
        }                
        
        //convenient syntax for NodeNameContainingFilter
        String nodeNameLike = request.getParameter("nodenamelike");
        if(nodeNameLike != null && nodeNameLike.length() > 0) {
            filterArray.add(new NodeNameLikeFilter(nodeNameLike));
        }

        //convenient syntax for ServiceFilter
        String service = request.getParameter("service");
        if(service != null && !service.equals(EventUtil.ANY_SERVICES_OPTION)) {
            filterArray.add(new ServiceFilter(Integer.parseInt(service)));
        }
                
        //convenient syntax for IPLikeFilter
        String ipLikePattern = request.getParameter("iplike");
        if(ipLikePattern != null && !ipLikePattern.equals("")) {
            filterArray.add(new IPLikeFilter(ipLikePattern));
        }

        //convenient syntax for SeverityFilter
        String severity = request.getParameter("severity");
        if(severity != null && !severity.equals(EventUtil.ANY_SEVERITIES_OPTION)) {
            filterArray.add(new SeverityFilter(Integer.parseInt(severity)));
        }

        //convenient syntax for AfterDateFilter as relative to current time
        String relativeTime = request.getParameter("relativetime");
        if(relativeTime != null && !relativeTime.equals(EventUtil.ANY_RELATIVE_TIMES_OPTION)) {
            try {
                filterArray.add(EventUtil.getRelativeTimeFilter(Integer.parseInt(relativeTime)));
            }
            catch( IllegalArgumentException e ) {
                //ignore the relative time if it is an illegal value
                this.log( "Illegal relativetime value", e );
            }
        }

        String useBeforeTime = request.getParameter("usebeforetime");
        if(useBeforeTime != null && useBeforeTime.equals("1")) {
            try {
                filterArray.add(this.getBeforeDateFilter(request));
            }
            catch( IllegalArgumentException e ) {
                //ignore the before time if it is an illegal value
                this.log( "Illegal before time value", e );
            }
            catch( MissingParameterException e ) {
                throw new ServletException(e);
            }
        }

        String useAfterTime = request.getParameter("useaftertime");
        if(useAfterTime != null && useAfterTime.equals("1")) {
            try {
                filterArray.add(this.getAfterDateFilter(request));
            }
            catch( IllegalArgumentException e ) {
                //ignore the after time if it is an illegal value
                this.log( "Illegal after time value", e );
            }
            catch( MissingParameterException e ) {
                throw new ServletException(e);
            }
        }


        String queryString = "";

        if(filterArray.size() > 0) {
            String[] filterStrings = new String[filterArray.size()];
            
            for(int i=0; i < filterStrings.length; i++) {
                Filter filter = (Filter)filterArray.get(i);
                filterStrings[i] = EventUtil.getFilterString(filter);
            }
            
            HashMap paramAdditions = new HashMap();
            paramAdditions.put("filter", filterStrings);
            
            queryString = Util.makeQueryString(request, paramAdditions, IGNORE_LIST);
        }
        else {
            queryString = Util.makeQueryString(request, IGNORE_LIST);            
        }

        response.sendRedirect(this.redirectUrl + "?" + queryString);             
    }


    protected BeforeDateFilter getBeforeDateFilter(HttpServletRequest request) {
        Date beforeDate = this.getDateFromRequest(request, "before");
        return(new BeforeDateFilter(beforeDate));
    }
    
    
    protected AfterDateFilter getAfterDateFilter(HttpServletRequest request) {
        Date afterDate = this.getDateFromRequest(request, "after");
        return(new AfterDateFilter(afterDate));
    }
    
    
    protected Date getDateFromRequest(HttpServletRequest request, String prefix) throws MissingParameterException {
        if( request == null || prefix == null ) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        Calendar cal = Calendar.getInstance();        
        
        //be lenient to handle the inputs easier
        //read the java.util.Calendar javadoc for more info
        cal.setLenient(true);  
        
        //hour, from 1-12
        String hourString = request.getParameter(prefix + "hour");
        if( hourString == null ) {
            throw new MissingParameterException(prefix + "hour", this.getRequiredDateFields(prefix));
        }
        
        cal.set(Calendar.HOUR, Integer.parseInt(hourString)); 
        
        //minute, from 0-59
        String minuteString = request.getParameter(prefix + "minute");
        if( minuteString == null ) {
            throw new MissingParameterException(prefix + "minute", this.getRequiredDateFields(prefix));
        }
        
        cal.set(Calendar.MINUTE, Integer.parseInt(minuteString)); 

        //AM/PM, either AM or PM
        String amPmString = request.getParameter(prefix + "ampm");
        if( amPmString == null ) {
            throw new MissingParameterException(prefix + "ampm", this.getRequiredDateFields(prefix));
        }
        
        if(amPmString.equalsIgnoreCase("am")) {
            cal.set(Calendar.AM_PM, Calendar.AM);
        }
        else if(amPmString.equalsIgnoreCase("pm")) {
            cal.set(Calendar.AM_PM, Calendar.PM);
        }
        else {
            throw new IllegalArgumentException("Illegal AM/PM value: " + amPmString);
        }

        
        //month, 0-11 (Jan-Dec)
        String monthString = request.getParameter(prefix + "month");
        if( monthString == null ) {
            throw new MissingParameterException(prefix + "month", this.getRequiredDateFields(prefix));
        }
        
        cal.set(Calendar.MONTH, Integer.parseInt(monthString)); 
        
        //date, 1-31
        String dateString = request.getParameter(prefix + "date");
        if( dateString == null ) {
            throw new MissingParameterException(prefix + "date", this.getRequiredDateFields(prefix));
        }
        
        cal.set(Calendar.DATE, Integer.parseInt(dateString)); 
        
        //year
        String yearString = request.getParameter(prefix + "year");
        if( yearString == null ) {
            throw new MissingParameterException(prefix + "year", this.getRequiredDateFields(prefix));
        }
        
        cal.set(Calendar.YEAR, Integer.parseInt(yearString));

        return cal.getTime();                     
    }
    
    protected String[] getRequiredDateFields(String prefix) {
        return new String[] {
            prefix + "hour",
            prefix + "minute",
            prefix + "ampm",
            prefix + "date",
            prefix + "month",
            prefix + "year"
        };
    }
    
}

