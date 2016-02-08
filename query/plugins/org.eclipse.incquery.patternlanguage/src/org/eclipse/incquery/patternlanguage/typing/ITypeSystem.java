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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.patternLanguage.RelationType;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * A type system represents the types (classes, or references) provided by a modeling backend.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public interface ITypeSystem {

    /**
     * Decides whether the second type is compatible with the first one
     */
    boolean isConformant(IInputKey expectedType, IInputKey actualType);

    /**
     * Decides whether the reference can have a selected type at its given column.
     */
    boolean isConformToRelationColumn(IInputKey relationType, int columnIndex, IInputKey columnType);

    /**
     * Extracts the model-specific types from a Type declaration.
     * 
     * @param type
     *            either the {@link Type} or the {@link RelationType} instance
     * @return model-specific type representation
     */
    IInputKey extractTypeDescriptor(Type type);

    /**
     * Extracts the model-specific column type of the given index from a RelationType declaration
     * 
     * @param type
     */
    IInputKey extractColumnDescriptor(RelationType type, int columnIndex);

    /**
     * Creates a type reference for model inference from a selected type.
     * 
     * @param type
     * @param context
     */
    JvmTypeReference toJvmTypeReference(IInputKey type, EObject context);

    /**
     * Converts a type object to a user-visible description string.
     * 
     * @param type
     *            either a {@link Type}, or a model-specific type instance
     * @return the string representation of the selected type
     */
    String typeString(IInputKey type);

    /**
     * An empty implementation of {@link ITypeSystem} that can be used by the abstract pattern language module.
     */
    public static final class NullTypeSystem implements ITypeSystem {

		@Override
		public boolean isConformant(IInputKey expectedType, IInputKey actualType) {
            throw new UnsupportedOperationException();
		}

		@Override
		public boolean isConformToRelationColumn(IInputKey relationType,
				int columnIndex, IInputKey columnType) {
            throw new UnsupportedOperationException();
		}

		@Override
		public IInputKey extractTypeDescriptor(Type type) {
            throw new UnsupportedOperationException();
		}

		@Override
		public IInputKey extractColumnDescriptor(RelationType type,
				int columnIndex) {
            throw new UnsupportedOperationException();
		}

		@Override
		public JvmTypeReference toJvmTypeReference(IInputKey type,
				EObject context) {
            throw new UnsupportedOperationException();
		}

		@Override
		public String typeString(IInputKey type) {
            throw new UnsupportedOperationException();
		}


    }
}