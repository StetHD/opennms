//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
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
//
// Tab Size = 8
//
//
package org.opennms.netmgt.poller;

import java.lang.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.ConnectException;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;

/**
 * <P>This class is designed to be used by the service poller
 * framework to test the availability of the POP3 service on 
 * remote interfaces. The class implements the ServiceMonitor
 * interface that allows it to be used along with other
 * plug-ins by the service poller framework.</P>
 *
 * @author <A HREF="mailto:mike@opennms.org">Mike</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 *
 */
final class Pop3Monitor
	extends IPv4Monitor
{
	/** 
	 * Default POP3 port.
	 */
	private static final int DEFAULT_PORT = 110;

	/** 
	 * Default retries.
	 */
	private static final int DEFAULT_RETRY = 0;

	/** 
	 * Default timeout.  Specifies how long (in milliseconds) to block waiting
	 * for data from the monitored interface.
	 */
	private static final int DEFAULT_TIMEOUT = 3000;

	/**
	 * <P>Poll the specified address for POP3 service availability.</P>
	 *
	 * <P>During the poll an attempt is made to connect on the specified
	 * port (by default TCP port 110).  If the connection request is
	 * successful, the banner line generated by the interface is parsed
	 * and if the response indicates that we are talking to
	 * an POP3 server we continue.  Next, a POP3 'QUIT' command is sent
	 * to the interface.  Again the response is parsed and verified.  
	 * Provided that the interface's response is valid we set the
	 * service status to SERVICE_AVAILABLE and return.</P>
	 *
	 * @param iface		The network interface to test the service on.
	 * @param parameters	The package parameters (timeout, retry, etc...) to be 
	 *  used for this poll.
	 *
	 * @return The availibility of the interface and if a transition event
	 * 	should be supressed.
	 *
	 */
	public int poll(NetworkInterface iface, Map parameters) 
	{
		// Get interface address from NetworkInterface
		//
		if (iface.getType() != iface.TYPE_IPV4)
			throw new NetworkInterfaceNotSupportedException("Unsupported interface type, only TYPE_IPV4 currently supported");

		// Process parameters
		//
		Category log = ThreadCategory.getInstance(getClass());

		int retry   = getKeyedInteger(parameters, "retry", DEFAULT_RETRY);
		int port    = getKeyedInteger(parameters, "port", DEFAULT_PORT);
		int timeout = getKeyedInteger(parameters, "timeout", DEFAULT_TIMEOUT) + 1;

		InetAddress ipv4Addr = (InetAddress)iface.getAddress();

		if (log.isDebugEnabled())
			log.debug("poll: address = " + ipv4Addr + ", port = " + port + ", timeout = " + timeout + ", retry = " + retry);

		int serviceStatus = ServiceMonitor.SERVICE_UNAVAILABLE;
		for (int attempts=0; attempts <= retry && serviceStatus != ServiceMonitor.SERVICE_AVAILABLE; attempts++)
		{
			Socket portal = null;
			try
			{
				//
				// create a connected socket
				//
				portal = new Socket(ipv4Addr, port);
				// We're connected, so upgrade status to unresponsive
				serviceStatus = SERVICE_UNRESPONSIVE;
				portal.setSoTimeout(timeout);
				BufferedReader rdr = new BufferedReader(new InputStreamReader(portal.getInputStream()));
				
				//
				// Tokenize the Banner Line, and check the first 
				// line for a valid return.
				//
				// Server response should start with: "+OK"
				//
				String banner = rdr.readLine();
				if (banner == null)
					continue;
				StringTokenizer t = new StringTokenizer(banner);
				
				if (t.nextToken().equals("+OK"))
				{
					//
					// POP3 server should recoginize the QUIT command
					//
					String cmd = "QUIT\r\n";
					portal.getOutputStream().write(cmd.getBytes());
					
					//
					// Parse the response to the QUIT command
					//
					// Server response should start with: "+OK"
					//
					t = new StringTokenizer(rdr.readLine());
					if (t.nextToken().equals("+OK"))
					{
						serviceStatus = ServiceMonitor.SERVICE_AVAILABLE;
					}
				}
				
				// If we get this far and the status has not been set
				// to available, then something didn't verify during
				// the banner checking or QUIT command process.
				if (serviceStatus != ServiceMonitor.SERVICE_AVAILABLE)
				{
					serviceStatus = ServiceMonitor.SERVICE_UNAVAILABLE;
				}
			}
			catch(NoRouteToHostException e)
			{
				if(log.isEnabledFor(Priority.WARN))
					log.warn("poll: No route to host exception for address " + ipv4Addr.getHostAddress(), e);
				break; // Break out of for(;;)
			}
			catch(ConnectException e)
			{
				// Ignore
				if(log.isDebugEnabled())
					log.debug("poll: Connection exception for address " + ipv4Addr.getHostAddress(), e);
			}
			catch(IOException e)
			{
				// Ignore
				if(log.isDebugEnabled())
					log.debug("poll: IOException while polling address " + ipv4Addr.getHostAddress(), e);
			}
			finally
			{
				try
				{
					// Close the socket
					if(portal != null)
						portal.close();
				}
				catch(IOException e) 
				{ 
					if(log.isDebugEnabled())
						log.debug("poll: Error closing socket.", e);
				}
			}
		}
	
		//
		// return the status of the service
		//
		return serviceStatus;
	}

}

