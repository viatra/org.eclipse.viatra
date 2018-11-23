/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Interface for commands that read some representation of an instance of an Ecore metamodel. 
 * {@link IModelReadOperations} is provided as the default case where the instance model is simply an EMF model. 
 * 
 * <p> Note that not all representations may support / preserve ordered collections.
 * 
 * @param <RootContainer> the type of root containers in which model elements may reside (e.g. a {@link Resource})
 * @param <ModelObject> the type representing a model element; can be simply an {@link EObject} or a surrogate key 
 *  
 * @noextend This interface is not intended to be implemented by clients. 
 *  
 * @author Gabor Bergmann
 * @since 2.1
 */
public interface IEcoreReadOperations<RootContainer, ModelObject> {
    
    // ************************************* GET **************************************
    /**
     * Queries the exact type of the given object.
     */
    EClass eClass(ModelObject element) throws ModelManipulationException;
    /**
     * Retrieves the number of values in a given feature slot of a given container model element.
     */
    int count(ModelObject container, EStructuralFeature feature) throws ModelManipulationException;
    /**
     * Retrieves all values in a given feature slot of a given container model element.
     */
    Stream<? extends Object> stream(ModelObject container, EStructuralFeature feature) throws ModelManipulationException;
    /**
     * Returns whether the given value is listed among the values in a given feature slot of a given container model element.
     */
    boolean isSetTo(ModelObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;
}
