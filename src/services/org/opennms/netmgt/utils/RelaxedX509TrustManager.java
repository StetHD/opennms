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
package org.opennms.netmgt.utils;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

/** 
 * This class is used to change the behaviour of the X509TrustManager
 * that is used to validate certificates from an HTTPS server. With this
 * class all certificates will be approved
 *
 * @author <A HREF="mailto:jason@opennms.org">Jason</A>
 * @author <A HREF="http://www.opennsm.org">OpenNMS</A>
 */
public class RelaxedX509TrustManager implements X509TrustManager
{
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
        { }
        
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
        { }
        
        public java.security.cert.X509Certificate[] getAcceptedIssuers() 
        { return null; }
}
