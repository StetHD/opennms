//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
// Copyright (C) 2001 Oculan Corp.  All rights reserved.
//  
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software 
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
// 
// For more information contact: 
//	Brian Weaver	<weave@opennms.org>
//	http://www.opennms.org/
//
// Tab Size = 8
//

package org.opennms.netmgt.rtc;

/**
 * <P>This class is a repository for constant, static information
 * concerning the RTC. 
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Kumaraswamy</A>
 * @author <A HREF="http://www.opennms.org">OpenNMS</A>
 */
public final class RTCConstants
{
	/**
	 * The value returned by RTC if a nodeid/ip/svc tuple does not belong to 
	 * a category
	 */
	public static double	NODE_NOT_IN_CATEGORY =-1.0;

	/**
	 * <P>The base name of the eventd configuration file. This does
	 * not include any path information about the location of the 
	 * file, just the filename itself.</P>
	 */
	public final static String	RTC_CONF_FNAME		= "rtc-configuration.xml";

	/**
	 * <P>The SQL statement necessary to read service id
	 * and service name into map </P>
	 */
	public final static String 	SQL_DB_SVC_TABLE_READ	= "SELECT serviceID, serviceName FROM service";

	/**
	 * <P>The SQL statement necessary to convert the service name
	 * into a service id using the 'service' tbale </P>
	 */
	public final static String 	SQL_DB_SVCNAME_TO_SVCID	= "SELECT serviceID FROM service WHERE serviceName = ?";

	/**
	 * <P>The sql statement that is used to get node info. for an IP address
	 */
	public final static String	DB_GET_INFO_FOR_IP	= "SELECT  node.nodeid FROM "
								+"node, ipInterface WHERE ((ipInterface.ipaddr = ?) AND "
								+"(ipInterface.nodeid = node.nodeid) AND (node.nodeType = 'A') AND (ipinterface.ismanaged = 'M') )";

	/**
	 * <P>The sql statement that is used to get services info. for a nodeid/IP address
	 */
	public final static String	DB_GET_SVC_ENTRIES	= "SELECT service.servicename FROM ifServices, " 
								+ "service WHERE ((ifServices.nodeid = ? ) AND (ifServices.ipaddr = ?) AND "
								+ "(ifServices.serviceid = service.serviceid) AND (ifservices.status = 'A'))"; 

	/**
	 * <P>The sql statement that is used to get 'status' for a nodeid/ip/svc
	 */
	public final static String	DB_GET_SERVICE_STATUS	= "SELECT status from ifservices where ((nodeid = ?) AND (ipaddr = ?) AND (serviceid = ?))";

	/**
	 * The sql statement for getting outage entries for a nodeid/ip/serviceid
	 */
	public final static String	DB_GET_OUTAGE_ENTRIES	= "SELECT ifLostService, ifRegainedService from outages "
								+"where ( (outages.nodeid = ?) AND (outages.ipaddr = ?) AND (outages.serviceid = ?) AND "
								+"((ifLostService >= ?) OR (ifRegainedService >= ?) OR (ifRegainedService IS NULL)) )";
	
}
