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
package org.opennms.netmgt.eventd;

import java.util.List;

// castor generated event class
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Log;

/**
 *
 * @author 	<A HREF="mailto:weave@opennms.org">Brian Weaver</A>
 * @author 	<A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj</A>
 * @author	<A HREF="http://www.opennms.org">OpenNMS.org</A>
 */
public interface EventIpcManager
{
	/**
	 * Called by a service to send an event to eventd
	 */
	public void sendNow(Event event);

	/**
	 * Called by a service to send a set of events to eventd
	 */
	public void sendNow(Log eventLog);

	/**
	 * Called by eventd to send an event to all interested listeners
	 */
	public void broadcastNow(Event event);

	/**
	 * Registers an event listener that is interested in all events
	 */
	public void addEventListener(EventListener listener);

	/**
	 * Registers an event listener interested in the UEIs in the passed list
	 */
	public void addEventListener(EventListener listener, List ueilist);

	/**
	 * Registers an event listener interested in the passed UEI
	 */
	public void addEventListener(EventListener listener, String uei);

	/**
	 * Removes a registered event listener
	 */
	public void removeEventListener(EventListener listener);

	/**
	 * Removes a registered event listener - the UEI list indicates the list
	 * of events the listener is no more interested in
	 */
	public void removeEventListener(EventListener listener, List ueiList);

	/**
	 * Removes a registered event listener - the UEI indicates an event
	 * the listener is no more interested in
	 */
	public void removeEventListener(EventListener listener, String uei);
}

