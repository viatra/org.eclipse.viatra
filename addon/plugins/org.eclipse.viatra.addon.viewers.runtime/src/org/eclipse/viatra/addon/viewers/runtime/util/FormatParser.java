/*******************************************************************************
 * Copyright (c) 2010-2013, Csaba Debreceni, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.util;

import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormatSpecification;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormattableElement;
import org.eclipse.viatra.addon.viewers.runtime.notation.NotationFactory;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;

import com.google.common.collect.ImmutableMap;

/**
 * @author Zoltan Ujhelyi and Csaba Debreceni
 *
 */
public final class FormatParser {

    public static final String ANNOTATION_ID = "Format";
    private static final String PREFIX = "org.eclipse.viatra.viewers.format.";
    public static final String COLOR = PREFIX + "color";
    public static final String LINE_COLOR = PREFIX + "linecolor";
    public static final String TEXT_COLOR = PREFIX + "textcolor";
    public static final String LINE_WIDTH = PREFIX + "linewidth";
    public static final String LINE_STYLE = PREFIX + "linestyle";
    public static final String ARROW_SOURCE_END = PREFIX + "arrowsourceend";
    public static final String ARROW_TARGET_END = PREFIX + "arrowtargetend";
    
    private static final Map<String, String> parameterMapping = ImmutableMap.<String, String> builder()
            .put("color", COLOR)
            .put("lineColor", LINE_COLOR)
            .put("textColor", TEXT_COLOR)
            .put("lineWidth", LINE_WIDTH)
            .put("lineStyle", LINE_STYLE)
            .put("arrowSourceEnd", ARROW_SOURCE_END)
            .put("arrowTargetEnd", ARROW_TARGET_END)
            .build();

    private FormatParser() {}
    
    public static boolean isFormatted(FormattableElement element) {
        return element.getFormat() != null;
    }
    
    public static FormatSpecification parseFormatAnnotation(PAnnotation format) {
        FormatSpecification specification = NotationFactory.eINSTANCE.createFormatSpecification();
        format.forEachValue((key, value) -> parseParameter(key, value, specification));
        return specification;
    }

    private static void parseParameter(String name, Object value, FormatSpecification specification) {
        String key = parameterMapping.get(name);
        if (value instanceof String) {
            specification.getProperties().put(key, value);
        } else if (value instanceof Integer) {
            specification.getProperties().put(key, Integer.toString((Integer)value));
        }
        // Ignoring unsupported input
    }

    public static RGB parseColor(String colorString) {
        String colorRegexp = "#[A-Fa-f0-9]{6}";
        if (colorString == null || !colorString.matches(colorRegexp)) {
            return null;
        }

        int red = Integer.parseInt(colorString.substring(1, 3), 16);
        int green = Integer.parseInt(colorString.substring(3, 5), 16);
        int blue = Integer.parseInt(colorString.substring(5, 7), 16);

        return new RGB(red, green, blue);
    }
    
    public static RGB getColorFormatProperty(FormattableElement element, String index) {
        return FormatParser.parseColor((String) element.getFormat().getProperties().get(index));
    }

    /**
     * Returns a numeric format property. If the input is erroneous (e.g. non-existent or non-numeric properties), -1 is
     * returned.
     */
    public static int getNumberProperty(FormattableElement element, String index) {
        try {
            return Integer.parseInt((String) element.getFormat().getProperties().get(index));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Returns a string format property. If the input is erroneous (e.g. non-existent properties),
     * null is returned.
     */
    public static String getStringProperty(FormattableElement element, String index) {
        return (String) element.getFormat().getProperties().get(index);
    }
}
