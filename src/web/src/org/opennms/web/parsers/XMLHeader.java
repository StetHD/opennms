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

package org.opennms.web.parsers;

import java.util.*;
import java.net.*;
import java.io.*;

import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;
import org.apache.xerces.parsers.*;
import javax.xml.parsers.*;

/**This class holds the information parsed from a header of
 * an OpenNMS xml file.
 * @author <A HREF="mailto:jason@opennms.org">Jason Johns</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 *
 * @version 1.1.1.1
 * 
 */
public class XMLHeader
{
	/*XML Tag names
	*/
	private static final String HEADER            = "header";
	private static final String REV               = "rev";
	private static final String CREATED           = "created";
	private static final String CREATED_ATT_YEAR  = "year";
	private static final String CREATED_ATT_MONTH = "month";
	private static final String CREATED_ATT_DAY   = "day";
	private static final String CREATED_ATT_HOUR  = "hour";
	private static final String CREATED_ATT_MIN   = "min";
	private static final String CREATED_ATT_SEC   = "sec";
	private static final String MSTATION          = "mstation";
	
	/**
	*/
	protected Document m_document;
	
	/**
	*/
	protected Element m_root;
	
	/**
	*/
	protected Element m_headerElement;
	
	/**
	*/
	protected String m_version;
	
	/**Default constructor, does nothing
	*/
	public XMLHeader()
	{
	}
	
	/**
	*/
	public XMLHeader(String version, Document document)
	{
		m_document = document;
		m_version = version;
		
		buildDocument();
	}
	
	/**This method sets the version
	   @param String aVersion, the version
	*/
	public void setVersion(String aVersion)
	{
		m_version = aVersion;
	}
	
	/**Returns the version
	   @return String, the version
	*/
	public String getVersion()
	{
		return m_version;
	}
	
	/**Sets the master station
	   @param String aStation, the master station
	*/
	public void setMasterStation(String aStation)
	{
		//m_masterStation = aStation;
	}
	
	/**Returns the master station
	   @return String, the master station
	*/
	public String getMasterStation()
	{
		return getHost();
	}
	
	/**
	*/
	private String getHost()
	{
		String hostName = "localhost";
		
		try
		{
			InetAddress address = InetAddress.getLocalHost();
			hostName = address.getHostName();
		}
		catch (Exception e)
		{
			//do nothing, the hostname will default to localhost
		}
		
		return hostName;
	}
	
	/**
	*/
	public void buildDocument()
	{
		m_headerElement = m_document.createElement(HEADER);
		
		Element revElement = m_document.createElement(REV);
		m_headerElement.appendChild(revElement);
		revElement.appendChild(m_document.createTextNode(getVersion()));
		
		Element createdElement = m_document.createElement(CREATED);
		m_headerElement.appendChild(createdElement);
		
		//add the created date attributes
		Calendar time = new GregorianCalendar();
		
		createdElement.setAttribute(CREATED_ATT_YEAR,  ""+time.get(Calendar.YEAR));
		createdElement.setAttribute(CREATED_ATT_MONTH, ""+(1+time.get(Calendar.MONTH)));
		createdElement.setAttribute(CREATED_ATT_DAY,   ""+time.get(Calendar.DAY_OF_MONTH));
		createdElement.setAttribute(CREATED_ATT_HOUR,  ""+time.get(Calendar.HOUR));
		createdElement.setAttribute(CREATED_ATT_MIN,   ""+time.get(Calendar.MINUTE));
		createdElement.setAttribute(CREATED_ATT_SEC,   ""+time.get(Calendar.SECOND));
		
		createdElement.appendChild(m_document.createTextNode(""+((Date)time.getTime()).getTime()));
		
		Element stationElement = m_document.createElement(MSTATION);
		m_headerElement.appendChild(stationElement);
		stationElement.appendChild(m_document.createTextNode(getHost()));
	}
	
	/**
	*/
	public Element getHeaderElement()
	{
		return m_headerElement;
	}
	
	/**
	*/
	public String toString()
	{
		return "";
	}
}
