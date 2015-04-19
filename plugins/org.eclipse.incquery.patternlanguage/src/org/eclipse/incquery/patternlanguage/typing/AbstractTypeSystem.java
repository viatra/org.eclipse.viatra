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
package org.eclipse.incquery.patternlanguage.typing;

import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;

/**
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractTypeSystem implements ITypeSystem {

    final IQueryMetaContext context;

    public AbstractTypeSystem(IQueryMetaContext context) {
        this.context = context;
    }

    @Override
    public boolean isConformToRelationSource(Object relationType, Object sourceType) {
        Object expectedType = null;
        switch (context.edgeInterpretation()) {
        case BINARY:
            expectedType = context.binaryEdgeSourceType(relationType);
            break;
        case TERNARY:
            expectedType = context.ternaryEdgeSourceType(relationType);
            break;
        }
        return isConformant(expectedType, sourceType);
    }

    @Override
    public boolean isConformToRelationTarget(Object relationType, Object targetType) {
        Object expectedType = null;
        switch (context.edgeInterpretation()) {
        case BINARY:
            expectedType = context.binaryEdgeTargetType(relationType);
            break;
        case TERNARY:
            expectedType = context.ternaryEdgeTargetType(relationType);
            break;
        }
        return isConformant(expectedType, targetType);
    }

    @Override
    public String typeString(Object type) {
        if (type instanceof Type) {
            return ((Type) type).getTypename();
        } else if (type instanceof IInputKey) {
        	return ((IInputKey) type).getPrettyPrintableName();
        } else throw new IllegalArgumentException("Invalid type: " + type);
    }

    
}
