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
//      Brian Weaver    <weave@opennms.org>
//      http://www.opennms.org/
//

package org.opennms.web.element;


public class Node
{
        int     m_nodeId;
        int     m_nodeParent;
        String  m_label;
        String  m_dpname;
        String  m_nodeCreateTime;
        String  m_nodeSysId;
        String  m_nodeSysName;
        String  m_nodeSysDescr;
        String  m_nodeSysLocn;
        String  m_nodeSysContact;
        char    m_nodeType;
	String	m_operatingSystem;
        
        /* package-protected so only the NetworkElementFactory can instantiate */
        Node()
        {
        }

        /* package-protected so only the NetworkElementFactory can instantiate */
        Node(   int nodeId,
                int nodeParent,
                String label,
                String dpname,
                String nodeCreateTime,
                String nodeSysId,
                String nodeSysName,
                String nodeSysDescr,
                String nodeSysLocn,
                String nodeSysContact,
                char nodeType,
		String operatingSystem)
        {
                m_nodeId = nodeId;
                m_nodeParent = nodeParent;
                m_label = label;
                m_dpname = dpname;
                m_nodeCreateTime = nodeCreateTime; 
                m_nodeSysId = nodeSysId;
                m_nodeSysName = nodeSysName;
                m_nodeSysDescr = nodeSysDescr;
                m_nodeSysLocn = nodeSysLocn;
                m_nodeSysContact = nodeSysContact;
                m_nodeType = nodeType;
		m_operatingSystem = operatingSystem;
        }

        public int getNodeId()
        {
                return m_nodeId;
        }

        public int getNodeParent()
        {
                return m_nodeParent;
        }

        public String getLabel()
        {
                return m_label;
        }

        public String getDpName()
        {
                return m_dpname;
        }

        public String getNodeCreateTime()
        {
                return m_nodeCreateTime;
        }

        public String getNodeSysId()
        {
                return m_nodeSysId;
        }

        public String getNodeSysName()
        {
                return m_nodeSysName;
        }

        public String getNodeSysDescr()
        {
                return m_nodeSysDescr;
        }

        public String getNodeSysLocn()
        {
                return m_nodeSysLocn;
        }

        public String getNodeSysContact()
        {
                return m_nodeSysContact;
        }

        public char getNodeType()
        {
                return m_nodeType;
        }

        public String getOperatingSystem()
        {
                return m_operatingSystem;
        }

        public String toString()
        {
                StringBuffer str = new StringBuffer("Node Id = " + m_nodeId + "\n" );
                str.append("Node Parent = " + m_nodeParent + "\n" );
                str.append("Node Create Time = " + m_nodeCreateTime + "\n" );
                str.append("Dp name = " + m_dpname + "\n" );
                str.append("Node Sys Id = " + m_nodeSysId + "\n" );
                str.append("Node Sys Name = " + m_nodeSysName + "\n" );
                str.append("Node Sys Descr = " + m_nodeSysDescr + "\n" );
                str.append("Node Sys Locn = " + m_nodeSysLocn + "\n" );
                str.append("Node Sys Contact = " + m_nodeSysContact + "\n" );
                str.append("Node Sys Type = " + m_nodeType + "\n" );
                str.append("Operating System = " + m_operatingSystem + "\n" );
                return str.toString();
        }
}
