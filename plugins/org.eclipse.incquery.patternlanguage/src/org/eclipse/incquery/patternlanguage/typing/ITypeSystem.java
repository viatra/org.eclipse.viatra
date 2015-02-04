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
     * 
     * @param expectedType
     * @param actualType
     */
    boolean isConformant(Object expectedType, Object actualType);

    /**
     * Decides whether the reference can have a selected type as source
     * 
     * @param relationType
     * @param sourceType
     */
    boolean isConformToRelationSource(Object relationType, Object sourceType);

    /**
     * Decides whether the reference can have a selected type as target.
     * 
     * @param relationType
     * @param targetType
     */
    boolean isConformToRelationTarget(Object relationType, Object targetType);

    /**
     * Extracts the model-specific types from a Type declaration.
     * 
     * @param type
     *            either the {@link Type} or the {@link RelationType} instance
     * @return model-specific type representation
     */
    Object extractTypeDescriptor(Type type);

    /**
     * Extracts the model-specific source type from a RelationType declaration
     * 
     * @param type
     */
    Object extractSourceTypeDescriptor(RelationType type);

    /**
     * Extracts the model-specific target type from a {@link RelationType} declaration.
     * 
     * @param type
     */
    Object extractTargetTypeDescriptor(RelationType type);

    /**
     * Creates a tye reference for model inference from a selected type.
     * 
     * @param type
     * @param context
     */
    JvmTypeReference toJvmTypeReference(Object type, EObject context);

    /**
     * Converts a type object to a user-visible description string.
     * 
     * @param type
     *            either a {@link Type}, or a model-specific type instance
     * @return the string representation of the selected type
     */
    String typeString(Object type);

    /**
     * An empty implementation of {@link ITypeSystem} that can be used by the abstract pattern language module.
     */
    public static final class NullTypeSystem implements ITypeSystem {

        @Override
        public boolean isConformant(Object expectedType, Object actualType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isConformToRelationSource(Object relationType, Object sourceType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isConformToRelationTarget(Object relationType, Object targetType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JvmTypeReference toJvmTypeReference(Object type, EObject context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object extractTypeDescriptor(Type type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object extractSourceTypeDescriptor(RelationType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object extractTargetTypeDescriptor(RelationType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String typeString(Object type) {
            throw new UnsupportedOperationException();
        }

    }
}