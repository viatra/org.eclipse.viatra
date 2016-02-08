/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

/**
 * A descriptor for PQuery parameters. A parameter has a name and a type
 *
 * @author Zoltan Ujhelyi
 *
 */
public class PParameter {

    private String name;
    private String typeName;

    public PParameter(String name) {
        this(name, null);
    }

    public PParameter(String name, String typeName) {
        super();
        this.name = name;
        this.typeName = typeName;
    }

    /**
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a textual representation of the type of the parameter
     * @return the type description, or null if not available
     */
    public String getTypeName() {
        return typeName;
    }


}
