// 
//    JoeSNMP - SNMPv1 & v2 Compliant Libraries for Java
//    Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//   
// For more information contact: 
//	Brian Weaver	<weave@opennms.org>
//	http://www.opennms.org/
//
//
// Tab Size = 8
//
package org.opennms.protocols.snmp;

import java.lang.*;
import java.io.Serializable;

import org.opennms.protocols.snmp.SnmpSMI;
import org.opennms.protocols.snmp.SnmpSyntax;

import org.opennms.protocols.snmp.asn1.AsnEncoder;
import org.opennms.protocols.snmp.asn1.AsnEncodingException;
import org.opennms.protocols.snmp.asn1.AsnDecodingException;

/**
 * This class defines the 32-bit unsigned SNMP object used
 * to transmit 32-bit unsigned number. The unsigned value
 * is represented by a 64-bit quantity, but the upper 32-bits
 * are always truncated from the value. 
 *
 * If a caller passes in a value with any the upper 32-bits 
 * set the value will be silently truncated to a 32-bit value.
 * If negative quantities or values with more than 32-bits
 * are passed then data corruption will occur.
 *
 * @author	<a href="http://www.opennms.org">OpenNMS</a>
 * @author	<a href="mailto:weave@opennms.org">Brian Weaver</a>
 */
public class SnmpUInt32 extends Object
	implements SnmpSyntax, Cloneable, Serializable
{
	/**
	 * defines the serialization version
	 */
	static final long serialVersionUID = 6360915905545058942L;

	/**
	 * The internal 32-bit unsigned quantity
	 * implemented as a 64-bit signed quantity
	 *
	 */
	private long	m_value;

	/**
	 * The 32-bit mask to be BITWISE AND with all values
	 * to ensure that only the lower 32-bits are set.
	 *
	 */
	private static final long MASK = 0xffffffffL;

	/**
	 * The ASN.1 value for an unsigned integer value. BEWARE
	 * this value will conflict with the SnmpSMI.SMI_COUNTER32
	 * value. This object should not be dynamically registered
	 * with the SNMP library
	 *
	 */
	public static final byte ASNTYPE = SnmpSMI.SMI_UNSIGNED32;

	/**
	 * Default class constructor. Constructs the object with
	 * a value of zero(0).
	 *
	 */
	public SnmpUInt32( )
	{
		m_value = 0;
	}
	
	/**
	 * Constructs a SnmpUInt32 object with the specified value.
	 *
	 * @param value The new 32-bit value.
	 *
	 */
	public SnmpUInt32(long value)
	{
		//
		// silently truncate off all but 32-bits!
		//
		m_value = (value & MASK);
	}

	/**
	 * Constructs a SnmpUInt32 object with the specified value.
	 *
	 * @param value The new 32-bit value.
	 *
	 */
	public SnmpUInt32(Long value)
	{
		this(value.longValue());
	}

	/**
	 * Class copy constructor. Constructs a new object with
	 * the same value as the passed object.
	 *
	 * @param second The object to copy the value from.
	 *
	 */
	public SnmpUInt32(SnmpUInt32 second)
	{
		m_value = second.m_value;
	}

	/**
	 * Simple class constructor that recovers the unsigned value from
	 * the passed string. If the decoded value evaluates to a negative
	 * number or is malformed then an exception is generated. Likewise,
	 * the argument must not be a null reference or a null pointer exception
	 * is generated by the constructor.
	 *
	 * @param value		The unsigned value encoded as a string.
	 * 
	 * @throws java.lang.NullPointerException Thrown if the passed value
	 *	is a null pointer.
	 * @throws java.lang.IllegalArgumentException Thrown if the decoded value
	 *	evaluates to a negative value.
	 * @throws java.lang.NumberFormatException Throws in the passed value cannot
	 * 	be decoded by the constructor.
	 */
	public SnmpUInt32(String value)
	{
		if(value == null)
			throw new NullPointerException("The construction argument cannot be null");

		m_value = Long.parseLong(value);
		if(m_value < 0)
			throw new IllegalArgumentException("Illegal Negative Integer Value");

		m_value = m_value & MASK;
	}

	/**
	 * Used to retreive the 32-bit unsigned value.
	 *
	 * @return The internal 32-bit value.
	 *
	 */
	public long getValue()
	{
		//
		// just being consistant!
		//
		return (m_value & MASK);
	}

	/**
	 * Used to set the 32-bit unsigned quantity.
	 * If the value exceeds 32-bit then the upper
	 * 32-bits will be silently truncated from
	 * the value.
	 *
	 * @param value The new value for the object
	 */
	public void setValue(long value)
	{
		//
		// silently truncate all but 32-bits!
		//
		m_value = (value & MASK);
	}

	/**
	 * Used to set the 32-bit unsigned quantity.
	 * If the value exceeds 32-bit then the upper
	 * 32-bits will be silently truncated from
	 * the value.
	 *
	 * @param value The new value for the object
	 */
	public void setValue(Long value)
	{
		//
		// Again, truncate to 32-bits
		//
		m_value = (value.longValue() & MASK);
	}

	/**
	 * Used to retreive the ASN.1 type for this object.
	 *
	 * @return The ASN.1 value for the SnmpUInt32
	 *
	 */
	public byte typeId()
	{
		return ASNTYPE;
	}

	/**
	 * Used to encode the integer value into an ASN.1 buffer.
	 * The passed encoder defines the method for encoding the
	 * data.
	 *
	 * @param buf		The location to write the encoded data
	 * @param offset	The start of the encoded buffer.
	 * @param encoder	The ASN.1 encoder object
	 *
	 * @return The byte immediantly after the last encoded byte.
	 *
	 */
	public int encodeASN(byte[]	buf,
			     int	offset,
			     AsnEncoder encoder) throws AsnEncodingException
	{
		return encoder.buildUInteger32(buf, offset, typeId(), getValue());
	}

	/**
	 * Used to decode the integer value from the ASN.1 buffer.
	 * The passed encoder is used to decode the ASN.1 information
	 * and the integer value is stored in the internal object.
	 *
	 * @param buf		The encoded ASN.1 data
	 * @param offset	The offset of the first byte of data
	 * @param encoder	The ASN.1 decoder object.
	 *
	 * @return The byte immediantly after the last decoded byte of
	 *	information.
	 *
	 */
	public int decodeASN(byte[]	buf,
			     int	offset,
			     AsnEncoder encoder) throws AsnDecodingException
	{
		Object[] rVals = encoder.parseUInteger32(buf, offset);

		if(((Byte)rVals[1]).byteValue() != typeId())
			throw new AsnDecodingException("Invalid ASN.1 type");

		setValue((Long)rVals[2]);

		return ((Integer)rVals[0]).intValue();
	}

	/**
	 * Returns a duplicte of the current object
	 *
	 * @return A duplciate copy of the current object
	 */
	public SnmpSyntax duplicate() 
	{
		return new SnmpUInt32(this);
	}

	/**
	 * Returns a duplicte of the current object
	 *
	 * @return A duplciate copy of the current object
	 */
	public Object clone()
	{
		return new SnmpUInt32(this);
	}

	/**
	 * Returns the string representation of the object.
	 *
	 */
	public String toString()
	{
		return Long.toString(getValue());
	}
}
