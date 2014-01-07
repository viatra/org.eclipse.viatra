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
package org.eclipse.incquery.viewers.runtime.model.converters;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.viewers.runtime.model.FormatSpecification;
import org.eclipse.swt.graphics.RGB;

import com.google.common.collect.ImmutableMap;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class FormatParser {

    private static final Map<String, String> parameterMapping = ImmutableMap.<String, String> builder()
            .put("color", FormatSpecification.COLOR)
            .put("lineColor", FormatSpecification.LINE_COLOR)
            .put("textColor", FormatSpecification.TEXT_COLOR)
            .put("lineWidth", FormatSpecification.LINE_WIDTH)
            .put("lineStyle", FormatSpecification.LINE_STYLE)
            .put("arrowSourceEnd", FormatSpecification.ARROW_SOURCE_END)
            .put("arrowTargetEnd", FormatSpecification.ARROW_TARGET_END)
            .build();

    public static FormatSpecification parseFormatAnnotation(PAnnotation format) {
        FormatSpecification specification = new FormatSpecification();
        for (Entry<String, Object> param : format.getAllValues()) {
            parseParameter(param.getKey(), param.getValue(), specification);
        }
        return specification;
    }

    private static void parseParameter(String name, Object value, FormatSpecification specification) {
        String key = parameterMapping.get(name);
        if (value instanceof String) {
            specification.setProperty(key, (String)value);
        } else if (value instanceof Integer) {
            specification.setProperty(key, Integer.toString((Integer)value));
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
}
