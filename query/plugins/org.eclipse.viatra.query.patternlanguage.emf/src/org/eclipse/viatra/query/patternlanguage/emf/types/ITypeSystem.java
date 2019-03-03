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
package org.eclipse.viatra.query.patternlanguage.emf.types;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.vql.RelationType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * A type system represents the types (classes, or references) provided by a modeling backend.
 * 
 * @author Zoltan Ujhelyi
 * @noimplement Do not implement directly, rely on {@link AbstractTypeSystem} instead.
 * @since 2.0
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
     * Returns a new Type instance that represents the same type as the parameter input key. Each call of this method
     * will return a new instance, and the returned type is not contained in any other EMF object, making the return
     * type safe to include in VQL models.
     *
     * Not all concrete IInputKey instances are supported by this method; in case of unsupported type an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @since 2.2
     */
    default Type convertToVQLType(EObject context, IInputKey key) {
        return convertToVQLType(context, key, false);
    }
    
    /**
     * Returns a new Type instance that represents the same type as the parameter input key. Each call of this method
     * will return a new instance, and the returned type is not contained in any other EMF object, making the return
     * type safe to include in VQL models.
     *
     * Not all concrete IInputKey instances are supported by this method; in case of unsupported type an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param avoidDataType If set to true, data type are returned as Java types
     *
     * @since 2.2
     */
    Type convertToVQLType(EObject context, IInputKey key, boolean avoidDataType);
    
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
     * @since 1.3
     */
    Set<IInputKey> addTypeInformation(Set<IInputKey> types, Set<IInputKey> newTypes);
    
    /**
     * Creates a type reference for model inference from a selected type.
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
     *
     * @since 1.3
     */
    Set<IInputKey> getCompatibleSupertypes(Set<IInputKey> types);

    /**
     * Returns whether the type declaration represents a valid, resolvable type for the selected type system. A null type is invalid.
     * 
     * @since 1.4
     */
    boolean isValidType(Type type);
    
}