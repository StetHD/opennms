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
//
package org.opennms.netmgt.config;

import java.lang.*;
import java.lang.reflect.UndeclaredThrowableException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.Enumeration;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import org.xml.sax.InputSource;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;

import org.opennms.netmgt.ConfigFileConstants;

// castor classes generated from the service-config.xsd
//
import org.opennms.netmgt.config.service.*;

/**
 * <p>This factory class is designed to be the main interface between the service
 * configuration information and the users of the information. When initialized
 * the factory loads the configuration from the file system, allowing access to
 * the information by others. The <code>init<code> method may be called by more
 * than one thread, but <em>MUST</em> be called by at least one thread before
 * the factory can be used.</p>
 *
 * <p>The factory supports the singleton design pattern, and thus the configuration
 * is loaded only once. All callers get the same reference unless a call to <code>
 * reload</code> is made. After than any saved instances of the factory can still
 * be referenced. Old references will not reflect any changes in the file if the
 * factory is reloaded.</p>
 *
 * @author <a href="mailto:weave@opennms.org">Weave</a>
 * @author <a href="http://www.opennms.org/">OpenNMS</a>
 *
 */
public final class ServiceConfigFactory
{
	/**
	 * The singleton instance of this factory
	 */
	private static ServiceConfigFactory	m_singleton = null;

	/**
	 * This member is set to true if the configuration file
	 * has been loaded.
	 */
	private static boolean			m_loaded = false;

	/**
	 * The loaded configuration after is has been unmarhsalled
	 * by castor.
	 */
	private ServiceConfiguration		m_config;

	/**
	 * Private constructor. This constructor used to load the 
	 * specified configuration file and initialized an instance
	 * of the class.
	 *
	 * @param configFile	The name of the configuration file.
	 *
	 * @exception java.io.IOException Thrown if the specified config
	 * 	file cannot be read
	 * @exception org.exolab.castor.xml.MarshalException Thrown if the 
	 * 	file does not conform to the schema.
	 * @exception org.exolab.castor.xml.ValidationException Thrown if 
	 *	the contents do not match the required schema.
	 */
	private ServiceConfigFactory(String configFile)
		throws 	IOException,
			MarshalException, 
			ValidationException
	{
		InputSource cfgIn = new InputSource(new FileInputStream(configFile));
		cfgIn.setSystemId(configFile);

		// load the config
		//
		m_config = (ServiceConfiguration) Unmarshaller.unmarshal(ServiceConfiguration.class, cfgIn);
	}

	/**
	 * Load the config from the default config file and create the 
	 * singleton instance of this factory.
	 *
	 * @exception java.io.IOException Thrown if the specified config
	 * 	file cannot be read
	 * @exception org.exolab.castor.xml.MarshalException Thrown if the 
	 * 	file does not conform to the schema.
	 * @exception org.exolab.castor.xml.ValidationException Thrown if 
	 *	the contents do not match the required schema.
	 */
	public static synchronized void init()
		throws 	IOException,
			MarshalException, 
			ValidationException
	{
		if (m_loaded)
		{
			// init already called - return
			// to reload, reload() will need to be called
			return;
		}

		File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.SERVICE_CONF_FILE_NAME);

		Category log = ThreadCategory.getInstance(ServiceConfigFactory.class);
		if(log.isDebugEnabled())
			log.debug("ServiceConfigFactory.init: config file path " + cfgFile.getPath());
		
		m_singleton = new ServiceConfigFactory(cfgFile.getPath());
		m_loaded    = true;
	}

	/**
	 * Reload the config from the default config file
	 *
	 * @exception java.io.IOException Thrown if the specified config
	 * 	file cannot be read/loaded
	 * @exception org.exolab.castor.xml.MarshalException Thrown if the 
	 * 	file does not conform to the schema.
	 * @exception org.exolab.castor.xml.ValidationException Thrown if 
	 *	the contents do not match the required schema.
	 */
	public static synchronized void reload()
		throws 	IOException,
			MarshalException, 
			ValidationException
	{
		m_singleton = null;
		m_loaded    = false;

		init();
	}

	/**
	 * Returns the currently defined singleton instance of the
	 * factory. There is only one instance of the configuration
	 * information, and it will not change unless the
	 * <code>reload</code> method is called.
	 *
	 * @return The singular instance of the factory class.
	 *
	 * @throws java.lang.IllegalStateException Thrown if the factory
	 * 	has not yet been initialized.
	 *
	 */
	public static synchronized ServiceConfigFactory getInstance()
	{
		if(!m_loaded)
			throw new IllegalStateException("Factory not initialized");

		return m_singleton;
	}

	/**
	 * Returns an array of all the defined configuration
	 * information for the <em>Services</em>. 
	 * If there are no defined services an array of 
	 * length zero is returned to the caller.
	 *
	 * @return An array holding a reference to all the Service
	 * 	configuration instances.
	 *
	 */
	public Service[] getServices()
	{
		int count = m_config.getServiceCount();
		Service[] slist = new Service[count];

		count = 0;
		Enumeration esvc = m_config.enumerateService();
		while(esvc.hasMoreElements())
			slist[count++] = (Service)esvc.nextElement();

		return slist;
	}
}
