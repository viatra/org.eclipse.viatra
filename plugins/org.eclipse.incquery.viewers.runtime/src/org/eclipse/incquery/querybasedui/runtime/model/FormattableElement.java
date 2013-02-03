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
package org.eclipse.incquery.querybasedui.runtime.model;

import org.eclipse.incquery.querybasedui.runtime.model.converters.FormatParser;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class FormattableElement {

    protected FormatSpecification specification;

    /**
     * @param specification
     *            the specification to set
     */
    public void setSpecification(FormatSpecification specification) {
        this.specification = specification;
    }

    public boolean isFormatted() {
        return specification != null;
    }

    public RGB getColorFormatProperty(String index) {
        return FormatParser.parseColor(specification.getProperty(index));
    }

    /**
     * Returns a numeric format property. If the input is erroneous (e.g. non-existent or non-numeric properties), -1 is
     * returned.
     * 
     * @param index
     * @return
     */
    public int getNumberPropery(String index) {
        try {
            return Integer.parseInt(specification.getProperty(index));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
