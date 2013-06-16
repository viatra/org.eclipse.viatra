/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class FormatSpecification {

    public static final String FORMAT_ANNOTATION = "Format";

    private final static String PREFIX = "org.eclipse.incquery.viewers.format.";
    public final static String COLOR = PREFIX + "color";
    public final static String LINE_COLOR = PREFIX + "linecolor";
    public final static String TEXT_COLOR = PREFIX + "textcolor";
    public final static String LINE_WIDTH = PREFIX + "linewidth";
    public final static String LINE_STYLE = PREFIX + "linestyle";
    public final static String ARROW_SOURCE_END = PREFIX + "arrowsourceend";
    public final static String ARROW_TARGET_END = PREFIX + "arrowtargetend";
        

    private Map<String, String> formatProperties = new HashMap<String, String>();

    public void setProperty(String index, String value) {
        formatProperties.put(index, value);
    }

    public String getProperty(String index) {
        return formatProperties.get(index);
    }

}
