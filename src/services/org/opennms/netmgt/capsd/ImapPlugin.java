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
package org.opennms.netmgt.capsd;

import java.lang.*;
import java.lang.reflect.UndeclaredThrowableException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.ConnectException;

import java.util.Map;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;

/**
 * <P>This class is designed to be used by the capabilities
 * daemon to test for the existance of an IMAP server on 
 * remote interfaces. The class implements the CapsdPlugin
 * interface that allows it to be used along with other
 * plugins by the daemon.</P>
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj</A>
 * @author <a href="mailto:weave@opennms.org">Weave</a>
 * @author <A HREF="http://www.opennsm.org">OpenNMS</A>
 *
 */
public final class ImapPlugin
	extends AbstractPlugin
{
	/**
	 * The name of the protocol supported by this plugin.
	 */
	private static final String 	PROTOCOL_NAME = "IMAP";
	
	/**
	 * <P>The default port on which the host is checked to see if it supports IMAP.</P>
	 */
	private static final int	DEFAULT_PORT	= 143;

	/**
	 * Default number of retries for IMAP requests
	 */
	private final static int	DEFAULT_RETRY	= 0;
	
	/**
	 * Default timeout (in milliseconds) for IMAP requests
	 */
	private final static int	DEFAULT_TIMEOUT	= 5000; // in milliseconds
	
	/**
	 * The start of the initial banner received from the server
	 */
	private static String		IMAP_START_RESPONSE_PREFIX="* OK ";

	/**
	 * The LOGOUT request sent to the server to close the connection
	 */
	private static String		IMAP_LOGOUT_REQUEST="ONMSCAPSD LOGOUT\r\n";
	
	/**
	 * The BYE response received from the server in response to the
	 * logout
	 */
	private static String		IMAP_BYE_RESPONSE_PREFIX="* BYE ";
	
	/**
	 * The LOGOUT response received from the server in response to the
	 * logout
	 */
	private static String		IMAP_LOGOUT_RESPONSE_PREFIX="ONMSCAPSD OK ";

	/**
	 * <P>Test to see if the passed host-port pair is the 
	 * endpoint for an IMAP server. If there is an IMAP server
	 * at that destination then a value of true is returned
	 * from the method. Otherwise a false value is returned 
	 * to the caller.</P>
	 *
	 * @param host	The remote host to connect to.
	 * @param port 	The remote port on the host.
	 *
	 * @return True if server supports IMAP on the specified 
	 *	port, false otherwise
	 */
	private boolean isServer(InetAddress host, int port, int retries, int timeout)
	{
		Category log = ThreadCategory.getInstance(getClass());

		boolean isAServer = false;
		for (int attempts=0; attempts <= retries && !isAServer; attempts++)
		{
			Socket  portal    = null;
			try
			{
				//
				// create a connected socket
				//
				portal = new Socket(host, port);
				portal.setSoTimeout(timeout); // blocking time!

				BufferedReader lineRdr = new BufferedReader(new InputStreamReader(portal.getInputStream()));
			
				//
				// Check the banner line for a valid return.
				//
				String banner = lineRdr.readLine();
				if(banner != null && banner.startsWith(IMAP_START_RESPONSE_PREFIX))
				{
					//
					// Send the LOGOUT
					//
					portal.getOutputStream().write(IMAP_LOGOUT_REQUEST.getBytes());
								
					//
					// get the returned string, tokenize, and 
					// verify the correct output.
					//
					String response = lineRdr.readLine();
					if(response != null && response.startsWith(IMAP_BYE_RESPONSE_PREFIX))
					{
						response = lineRdr.readLine();
						if(response != null && response.startsWith(IMAP_LOGOUT_RESPONSE_PREFIX))
						{
							isAServer = true;
						}
					}
				}
			}
			catch(ConnectException cE)
			{
				// Connection refused!!  No need to perform retries.
				//
				cE.fillInStackTrace();
				log.debug("ImapPlugin: host " + host.getHostAddress() + " refused connection", cE);
				isAServer = false;
				break;
			}
			catch(NoRouteToHostException e)
			{
				// No Route to host!!! Skip retries.
				//
				e.fillInStackTrace();
				log.info("ImapPlugin: unable to connect to remote imap server, no route to host " + host.getAddress(), e);
				isAServer = false;
				throw new UndeclaredThrowableException(e);
			}
			catch(InterruptedIOException e)
			{
				// this is expected
				isAServer = false;
			}
			catch(IOException e)
			{
				log.info("ImapPlugin: unexpected I/O exception caught during imap test to host " + host.getHostAddress(), e);
				isAServer = false;
			}
			catch(Throwable t)
			{
				log.warn("ImapPlugin: undeclared throwable exception caught testing host " + host.getHostAddress(), t);
				isAServer = false;
			}
			finally
			{
				try
				{
					if(portal != null)
						portal.close();
				}
				catch(IOException e) { }
			}
		}

		//
		// return the success/failure of this
		// attempt to contact an ftp server.
		//
		return isAServer;
	}

	/**
	 * Returns the name of the protocol that this plugin
	 * checks on the target system for support.
	 *
	 * @return The protocol name for this plugin.
	 */
	public String getProtocolName()
	{
		return PROTOCOL_NAME;
	}

	/**
	 * Returns true if the protocol defined by this
	 * plugin is supported. If the protocol is not 
	 * supported then a false value is returned to the 
	 * caller.
	 *
	 * @param address	The address to check for support.
	 *
	 * @return True if the protocol is supported by the address.
	 */
	public boolean isProtocolSupported(InetAddress address)
	{
		return isServer(address, DEFAULT_PORT, DEFAULT_RETRY, DEFAULT_TIMEOUT);
	}

	/**
	 * Returns true if the protocol defined by this
	 * plugin is supported. If the protocol is not 
	 * supported then a false value is returned to the 
	 * caller. The qualifier map passed to the method is
	 * used by the plugin to return additional information
	 * by key-name. These key-value pairs can be added to 
	 * service events if needed.
	 *
	 * @param address	The address to check for support.
	 * @param qualiier	The map where qualification are set
	 *			by the plugin.
	 *
	 * @return True if the protocol is supported by the address.
	 */
	public boolean isProtocolSupported(InetAddress address, Map qualifiers)
	{
		int retries = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;
		int port    = DEFAULT_PORT;

		if(qualifiers != null)
		{
			retries = getKeyedInteger(qualifiers, "retry", DEFAULT_RETRY);
			timeout = getKeyedInteger(qualifiers, "timeout", DEFAULT_TIMEOUT);
			port    = getKeyedInteger(qualifiers, "port", DEFAULT_PORT);
		}

		boolean result = isServer(address, port, retries, timeout);
		if(result && qualifiers != null && !qualifiers.containsKey("port"))
			qualifiers.put("port", new Integer(port));

		return result;
	}
}
