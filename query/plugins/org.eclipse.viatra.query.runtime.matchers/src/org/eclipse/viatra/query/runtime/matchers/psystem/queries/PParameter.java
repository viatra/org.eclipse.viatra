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

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * A descriptor for declared PQuery parameters. A parameter has a name and a declared type
 *
 * @author Zoltan Ujhelyi
 *
 */
public class PParameter {

    private String name;
    private String typeName;
    private IInputKey declaredUnaryType;

    public PParameter(String name) {
        this(name, null);
    }

    public PParameter(String name, String typeName) {
        this(name, typeName, null);
    }

    public PParameter(String name, String typeName, IInputKey declaredUnaryType) {
        super();
        this.name = name;
        this.typeName = typeName;
		this.declaredUnaryType = declaredUnaryType;
		
		if (declaredUnaryType != null && declaredUnaryType.getArity()!=1) {
			throw new IllegalArgumentException("PParameter declared type must be unary instead of " + declaredUnaryType.getPrettyPrintableName());
		}
    }

    /**
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a textual representation of the declared type of the parameter
     * @return the type description, or null if not available
     */
    public String getTypeName() {
        return typeName;
    }

	/**
	 * Yield an {@link IInputKey} representation of the type declared for this parameter.
	 * @return the unary type that was declared on this parameter in the query header, or null if not available
	 */
	public IInputKey getDeclaredUnaryType() {
		return declaredUnaryType;
	}
    
    


}
