/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 *
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.vaadin.dashboard.dashlets;

import java.util.Map;

import org.opennms.netmgt.bsm.service.BusinessServiceSearchCriteria;
import org.opennms.netmgt.bsm.service.BusinessServiceSearchCriteriaBuilder;
import org.opennms.netmgt.model.OnmsSeverity;

/**
 * Small helper class for loading search criteria from the dashlet's parameter map.
 *
 * @author Christian Pape <christian@opennms.org>
 */
public class BSMConfigHelper {
    /**
     * Returns a boolean value for a given key from the map where "1", "true" and "yes"
     * are interpeted as boolean true, otherwise false.
     *
     * @param map the map to be used
     * @param key the key
     * @return the boolean value
     */
    public static boolean getBooleanForKey(Map<String, String> map, String key) {
        String value = map.get(key);
        return ("true".equals(value) || "yes".equals(value) || "1".equals(value));
    }

    /**
     * Returns the string value for a given key. Null values will be returned as empty
     * strings.
     * @param map the map to use
     * @param key the key
     * @return the string value
     */
    public static String getStringForKey(Map<String, String> map, String key) {
        String value = map.get(key);
        return (value == null ? "" : value);
    }

    /**
     * Returns the int value for a given key. Unparsable values are returned as zero.
     *
     * @param map the map to be used
     * @param key the key
     * @return the int value, 0 if not parsable
     */
    public static int getIntForKey(Map<String, String> map, String key) {
        String value = map.get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Created as business service search criteria for a given map.
     *
     * @param map the map to inverstigate
     * @return the criteria
     */
    public static BusinessServiceSearchCriteria fromMap(Map<String, String> map) {
        boolean filterByName = getBooleanForKey(map, "filterByName");
        String nameValue = getStringForKey(map, "nameValue");

        boolean filterByAttribute = getBooleanForKey(map, "filterByAttribute");
        String attributeKey = getStringForKey(map, "attributeKey");
        String attributeValue = getStringForKey(map, "attributeValue");

        boolean filterBySeverity = getBooleanForKey(map, "filterBySeverity");

        String severityValue = getStringForKey(map, "severityValue");

        if (severityValue == null || "".equals(severityValue)) {
            severityValue = OnmsSeverity.WARNING.getLabel();
        }

        int resultsLimit = getIntForKey(map, "resultsLimit");

        BusinessServiceSearchCriteriaBuilder b = new BusinessServiceSearchCriteriaBuilder();

        if (filterByName) {
            b.name(nameValue);
        }
        if (filterByAttribute) {
            b.attribute(attributeKey, attributeValue);
        }
        if (filterBySeverity) {
            b.greaterOrEqualSeverity(severityValue);
        }

        return b.limit(resultsLimit)
                .order(BusinessServiceSearchCriteriaBuilder.Order.Name);
    }
}
