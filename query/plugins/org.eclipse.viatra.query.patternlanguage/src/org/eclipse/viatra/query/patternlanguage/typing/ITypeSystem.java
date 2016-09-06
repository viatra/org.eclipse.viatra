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
package org.eclipse.viatra.query.patternlanguage.typing;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.RelationType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * A type system represents the types (classes, or references) provided by a modeling backend.
 * 
 * @author Zoltan Ujhelyi
 * @noimplement Do not implement directly, rely on {@link AbstractTypeSystem} instead.
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
     * Reduces the type descriptor set by providing the most specific set of type descriptors inferrable for a selected
     * types.
     * 
     * @param types
     *            a collection of type definitions
     * @param mergeWithSupertypes
     *            if true, the collection is also minimized by calculating common supertypes
     * @return the minimized set of type information
     * @since 1.3
     */
    Set<IInputKey> minimizeTypeInformation(Set<IInputKey> types, boolean mergeWithSupertypes);

    /**
     * Adds a new type descriptor to a collection of type descriptors, and minimizes it. Equivalent of calling
     * {@link #minimizeTypeInformation(Set)} with <code>types.add(newType)</code>, but might have a more
     * efficient impementation.
     * 
     * @param types
     * @param newType
     * @return the minimized set of type information including the new type descriptor key
     * @since 1.3
     */
    Set<IInputKey> addTypeInformation(Set<IInputKey> types, IInputKey newType);

    /**
     * @param types
     * @param newTypes
     * @return
     * @since 1.3
     */
    Set<IInputKey> addTypeInformation(Set<IInputKey> types, Set<IInputKey> newTypes);
    
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
     * Returns a set of types that is a common supertype of a set of type parameters
     * @param types
     * @return
     * @since 1.3
     */
    Set<IInputKey> getCompatibleSupertypes(Set<IInputKey> types);

    /**
     * Returns whether the type declaration represents a valid, resolvable type for the selected type system. A null type is invalid.
     * @param type
     * @return
     * @since 1.4
     */
    boolean isValidType(Type type);
    
    /**
     * An empty implementation of {@link ITypeSystem} that can be used by the abstract pattern language module.
     * @since 1.3
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

        /**
         * @since 1.3
         */
        @Override
        public Set<IInputKey> minimizeTypeInformation(Set<IInputKey> types, boolean mergeWithSupertypes) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public Set<IInputKey> addTypeInformation(Set<IInputKey> types, IInputKey newType) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public Set<IInputKey> addTypeInformation(Set<IInputKey> types, Set<IInputKey> newTypes) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public Set<IInputKey> getCompatibleSupertypes(Set<IInputKey> types) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.4
         */
        @Override
        public boolean isValidType(Type type) {
            return false;
        }


    }
}