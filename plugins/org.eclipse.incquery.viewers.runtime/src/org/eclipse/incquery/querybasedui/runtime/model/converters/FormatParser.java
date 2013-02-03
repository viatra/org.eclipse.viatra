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
package org.eclipse.incquery.querybasedui.runtime.model.converters;

import java.util.Map;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.querybasedui.runtime.model.FormatSpecification;
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
            .build();
    
    public static FormatSpecification parseFormatAnnotation(Annotation format) {
        FormatSpecification specification = new FormatSpecification();
        for (AnnotationParameter param : format.getParameters()) {
            parseParameter(param, specification);
        }
        return specification;
    }

    /**
     * @param param
     * @param specification
     */
    private static void parseParameter(AnnotationParameter param, FormatSpecification specification) {
        String name = parameterMapping.get(param.getName());
        if (param.getValue() instanceof StringValue) {
            specification.setProperty(name, ((StringValue) param.getValue()).getValue());
        } else if (param.getValue() instanceof IntValue) {
            IntValue intValue = (IntValue) param.getValue();
            specification.setProperty(name, Integer.toString(intValue.getValue()));
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
