//
// Copyright (C) 2001 Oculan Corp.
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
//      Brian Weaver   <weave@opennms.org>
//      http://www.opennms.org/
//
//

package org.opennms.web.availability.raw;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.opennms.core.resource.Vault;
import org.opennms.report.availability.*;
import org.opennms.web.MissingParameterException;
import org.opennms.web.ServletInitializer;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.apache.xerces.parsers.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Category;

/**
 * @author <A HREF="mailto:jacinta@opennms.org">Jacinta Remedios</A>
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 */
public class RawAvailabilityServlet extends HttpServlet
{
        static Category log = Category.getInstance(RawAvailabilityServlet.class.getName());

        public void init() throws ServletException 
	{
        }

        public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException 
	{
                String category = request.getParameter( "category" );
                String username = request.getRemoteUser();
                ServletConfig config = this.getServletConfig();

                if( category == null) {
                        throw new MissingParameterException( "category" );
                }

                if( username == null ) {
                        username = "";
                }

                try
                {
                        //String url = config.getServletContext().getRealPath(request.getRequestURI());
                        String url = config.getServletContext().getRealPath("/availability/availabilityRaw");
                        int index = url.indexOf("/availability/availabilityRaw");
                        String urlReplace = url.substring(0, index);
                        urlReplace += "/images/logo.gif";

                        AvailabilityReport report = new AvailabilityReport(username);
                        report.getReportData(urlReplace, category, "all");

                        if(log.isDebugEnabled())
                                log.info("Generated Report Data... ");

			Reader xml = new FileReader( Vault.getHomeDir() + "/share/reports/AvailReport.xml" );
			Writer out = response.getWriter();

			response.setContentType( "text/xml" );
			this.streamToStream( xml, out );
			out.close();
                }
                catch( Exception e ) {
                        throw new ServletException( "AvailabilityServlet: ", e );
                }
        }

        /**
         * @deprecated Should use {@link org.opennms.web.Util#streamToStream
         * Util.streamToStream} instead.
         */
        protected void streamToStream( Reader in, Writer out ) throws IOException
        {
                char[] b = new char[100];
                int length;

                while((length = in.read(b)) != -1)
                {
                        out.write(b, 0, length);
                }
        }
}
