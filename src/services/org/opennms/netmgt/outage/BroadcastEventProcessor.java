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
package org.opennms.netmgt.outage;

import java.lang.*;

import java.io.StringReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;

import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;

import org.opennms.netmgt.xml.event.Event;

import org.opennms.core.queue.FifoQueue;
import org.opennms.core.queue.FifoQueueException;

import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.eventd.EventListener;
import org.opennms.netmgt.eventd.EventIpcManagerFactory;

/**
 * <P>BroadcastEventProcessor is responsible for receiving events from eventd
 * and queing them to the outage writer pool</P>
 *
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj</a>
 * @author <a href="http://www.opennms.org/">OpenNMS</a>
 */
final class BroadcastEventProcessor
	implements EventListener
{
	/**
	 * The location where incoming events of interest
	 * are enqueued
	 */
	private FifoQueue	m_writerQ;

	/**
	 * Constructor 
	 *
	 * @param writerQ	The queue where events of interest are added.
	 */
	BroadcastEventProcessor(FifoQueue writerQ)
	{
		m_writerQ = writerQ;
	}

	/**
	 * Create a list of UEIs of interest to the OutageManager and
	 * subscribe to eventd
	 */
	public void start()
	{
		// Create the selector for the ueis this service is interested in
		//
		List ueiList = new ArrayList();

		// nodeLostService
		ueiList.add(EventConstants.NODE_LOST_SERVICE_EVENT_UEI);

		// interfaceDown
		ueiList.add(EventConstants.INTERFACE_DOWN_EVENT_UEI);

		// nodeDown
		ueiList.add(EventConstants.NODE_DOWN_EVENT_UEI);

		// nodeUp
		ueiList.add(EventConstants.NODE_UP_EVENT_UEI);

		// interfaceUp
		ueiList.add(EventConstants.INTERFACE_UP_EVENT_UEI);

		// nodeRegainedService
		ueiList.add(EventConstants.NODE_REGAINED_SERVICE_EVENT_UEI);

		// deleteService
		ueiList.add(EventConstants.DELETE_SERVICE_EVENT_UEI);

		// interfaceReparented
		ueiList.add(EventConstants.INTERFACE_REPARENTED_EVENT_UEI);
		
		EventIpcManagerFactory.init();
		EventIpcManagerFactory.getInstance().getManager().addEventListener(this, ueiList);
	}

	/**
	 * Unsubscribe from eventd
	 */
	public void close()
	{
		EventIpcManagerFactory.getInstance().getManager().removeEventListener(this);
	}

	/**
	 * This method is invoked by the EventIpcManager
	 * when a new event is available for processing.
	 * Each message is examined for its Universal Event Identifier
	 * and the appropriate action is taking based on each UEI.
	 *
	 * @param event	The event 
	 */
	public void onEvent(Event event)
	{
		if (event == null)
			return;

		Category log = ThreadCategory.getInstance(getClass());
		if (log.isDebugEnabled())
			log.debug("About to start processing recd. event");

		try
		{
			String uei = event.getUei();
			if (uei == null)
				return;

			m_writerQ.add(new OutageWriter(event));

			if (log.isDebugEnabled())
				log.debug("Event " + uei + " added to writer queue");
		}
		catch(InterruptedException ex)
		{
			log.error("Failed to process event", ex);
			return;
		}
		catch(FifoQueueException ex)
		{
			log.error("Failed to process event", ex);
			return;
		}
		catch(Throwable t)
		{
			log.error("Failed to process event", t);
			return;
		}

	}

	/**
	 * Return an id for this event listener
	 */
	public String getName()
	{
		return "OutageManager:BroadcastEventProcessor";
	}
} // end class
