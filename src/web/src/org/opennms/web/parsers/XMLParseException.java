//
// Copyright (C) 2000 N*Manage Company, Inc.
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

/** This exception is used to indicate that an exception has occured during
  * the parsing of an xml file. It extends from RuntimeException to allow
  * the existing parsers to remain unchanged (runtime exceptions don't have
  * to be caught) but to allow new parsers to gain a debug advantage. This
  * should be changed to extend from Exception when all existing parsers can
  * be changed.
  * 
  * @author <A HREF="mailto:jason@opennms.org">Jason Johns</A>
  * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
  * @version 1.1.1.1  
*/
public class XMLParseException extends RuntimeException
{
	private Throwable rootCause;
	
	public XMLParseException() 
	{
		super();
	}
	
	public XMLParseException(String message) 
	{
		super(message);
	}
	
	public XMLParseException(String message, Throwable rootCause) 
	{
		super(message);
		this.rootCause = rootCause;
	}
	
	public XMLParseException(Throwable rootCause)
	{
		super(rootCause.getLocalizedMessage());
		this.rootCause = rootCause;
	}
	
	public Throwable getRootCause() 
	{
		return rootCause;
	}
}	
