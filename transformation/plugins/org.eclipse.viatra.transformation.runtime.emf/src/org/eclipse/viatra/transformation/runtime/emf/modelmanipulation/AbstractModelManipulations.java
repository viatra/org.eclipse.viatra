/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * Abstract base class for model manipulation implementation. It checks for the preconditions of the operations, and the
 * subclasses should override the do* methods.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.1
 * @noextend API may be extended in the future.
 */
public abstract class AbstractModelManipulations extends AbstractEcoreManipulations<Resource, EObject> 
    implements IModelManipulations, IModelReadOperations
{

    private static final String INVALID_MULTIPLICITY_FOR_POSITIONING_MESSAGE = "Positioning only works on features with 'many' multiplicity.";
    private static final String NOT_IN_CONTAINMENT_HIERARCHY_MESSAGE = "Elements must be moved into the containment hierarchy.";
    private static final String UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE = 
            "The container of EClass %s does neither define or inherit an EStructuralFeature %s.";
    private static final String FEATURE_TYPE_MISMATCH = 
            "The type of EStructuralFeature %s is incompatible with %s.";
    protected final ViatraQueryEngine engine;
    private NavigationHelper baseEMFIndex;

    public AbstractModelManipulations(ViatraQueryEngine engine) {
        super();
        this.engine = engine;
    }

    /**
     * @throws ViatraQueryRuntimeException
     */
    protected NavigationHelper getBaseEMFIndex() {
        if (baseEMFIndex == null) {
            baseEMFIndex = EMFScope.extractUnderlyingEMFIndex(engine);
        }
        return baseEMFIndex;
    }

    protected void doMoveTo(Collection<EObject> what, EObject newContainer, EReference reference)
            throws ModelManipulationException {
        for (EObject obj : what) {
            doMoveTo(obj, newContainer, reference);
        }
    }

    protected abstract void doMoveTo(EObject what, Resource newContainer) throws ModelManipulationException;

    protected abstract void doMoveTo(EObject what, Resource newContainer, int index) throws ModelManipulationException;

    protected abstract void doMoveTo(EObject what, EObject newContainer, EReference reference)
            throws ModelManipulationException;

    protected abstract void doMoveTo(EObject what, EObject newContainer, EReference reference, int index)
            throws ModelManipulationException;
    
    /**
     * Remove a non-containment reference value
     */
    protected abstract void doRemove(EObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException;

    protected abstract void doRemove(EObject container, EStructuralFeature feature, int index)
            throws ModelManipulationException;

    /**
     * Removes an element from the containment hierarchy
     */
    protected abstract void doRemove(EObject object) throws ModelManipulationException;

    protected abstract void doRemove(EObject container, EStructuralFeature reference) throws ModelManipulationException;

    protected abstract void doAdd(EObject container, EStructuralFeature reference, Collection<? extends Object> element)
            throws ModelManipulationException;

    protected abstract void doAdd(EObject container, EStructuralFeature reference, Object object, int index)
            throws ModelManipulationException;

    protected abstract void doSet(EObject container, EStructuralFeature feature, Object value)
            throws ModelManipulationException;

    protected abstract EObject doCreate(EObject container, EReference reference, EClass clazz)
            throws ModelManipulationException;

    protected abstract EObject doCreate(Resource res, EClass clazz) throws ModelManipulationException;

    protected abstract void doChangeIndex(EObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException;
    
    /**
     * @since 2.1
     */
    protected abstract int doCount(EObject container, EStructuralFeature feature) throws ModelManipulationException;
    /**
     * @since 2.1
     */
    protected abstract Stream<? extends Object> doStream(EObject container, EStructuralFeature feature) throws ModelManipulationException;
    /**
     * @since 2.1
     */
    protected abstract boolean doIsSetTo(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException;

    
    /**
     * @since 2.1
     */
    @Override
    public EClass eClass(EObject element) throws ModelManipulationException {
        return element.eClass();
    }
    
    /**
     * @since 2.1
     */
    @Override
    public int count(EObject container, EStructuralFeature feature) throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        return doCount(container, feature);
    }
    
    /**
     * @since 2.1
     */
    @Override
    public Stream<? extends Object> stream(EObject container, EStructuralFeature feature) throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        return doStream(container, feature);
    }
    
    /**
     * @since 2.1
     */
    @Override
    public boolean isSetTo(EObject container, EStructuralFeature feature, Object value)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(feature.getEType().isInstance(value),
                FEATURE_TYPE_MISMATCH,
                feature.getName(), value);
       return doIsSetTo(container, feature, value);
    }



    @Override
    public EObject create(Resource res, EClass clazz) throws ModelManipulationException {
        return doCreate(res, clazz);
    }

    @Override
    public EObject createChild(EObject container, EReference reference, EClass clazz)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(reference.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), reference.getName());
        Preconditions.checkArgument(reference.getEReferenceType().isSuperTypeOf(clazz) 
                || isEObjectClass(reference.getEReferenceType()),
                FEATURE_TYPE_MISMATCH,
                reference.getName(), clazz.getName());
        Preconditions.checkArgument(reference.isContainment(),
                "Created elements must be inserted directly into the containment hierarchy.");
        Preconditions.checkArgument(!clazz.isAbstract(), "Cannot instantiate abstract EClass %s.", clazz.getName());

        return doCreate(container, reference, clazz);
    }

    @Override
    public void addTo(EObject container, EStructuralFeature feature, Object element) throws ModelManipulationException {
        addTo(container, feature, Collections.singleton(element));
    }
    
    @Override
    public void addTo(EObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(feature.getEType().isInstance(element),
                FEATURE_TYPE_MISMATCH,
                feature.getName(), element);
        Preconditions.checkArgument(feature.isMany(),
                "The EStructuralFeature %s must have an upper bound larger than 1.", feature.getName());
        Preconditions.checkArgument(!(feature instanceof EReference && ((EReference) feature).isContainment()),
                "Adding existing elements into the containment reference %s is not supported.", feature.getName());
        doAdd(container, feature, element, index);
    }
    
    @Override
    public void addTo(EObject container, EStructuralFeature feature, Collection<? extends Object> elements)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        for (Object element: elements) {
            Preconditions.checkArgument(feature.getEType().isInstance(element),
                    FEATURE_TYPE_MISMATCH,
                    feature.getName(), element);            
        }
        Preconditions.checkArgument(feature.isMany(),
                "The EStructuralFeature %s must have an upper bound larger than 1.", feature.getName());
        Preconditions.checkArgument(!(feature instanceof EReference && ((EReference) feature).isContainment()),
                "Adding existing elements into the containment reference %s is not supported.", feature.getName());
        doAdd(container, feature, elements);
    }

    @Override
    public void set(EObject container, EStructuralFeature feature, Object value) throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(null == value || feature.getEType().isInstance(value),
                FEATURE_TYPE_MISMATCH,
                feature.getName(), value);
        Preconditions.checkArgument(!feature.isMany(), "The EStructuralFeature %s must have an upper bound of 1.",
                feature.getName());
        doSet(container, feature, value);
    }

    @Override
    public void remove(EObject object) throws ModelManipulationException {
        doRemove(object);
    }

    @Override
    public void remove(EObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(feature.getEType().isInstance(element),
                FEATURE_TYPE_MISMATCH,
                feature.getName(), element);
        Preconditions.checkArgument(feature.isMany(),
                "Remove only works on EStructuralFeatures with 'many' multiplicity.");
        doRemove(container, feature, element);
    }

    @Override
    public void remove(EObject container, EStructuralFeature feature, int index) throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(feature.isMany(), "Remove only works on features with 'many' multiplicity.");
        doRemove(container, feature, index);
    }
    
    @Override
    public void remove(EObject container, EStructuralFeature feature) throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass.getName(), feature.getName());
        Preconditions.checkArgument(feature.isMany(), "Remove only works on references with 'many' multiplicity.");
        doRemove(container, feature);
    }

    @Override
    public void moveTo(EObject what, Resource newContainer) throws ModelManipulationException {
        doMoveTo(what, newContainer);
    }

    @Override
    public void moveTo(EObject what, Resource newContainer, int index) throws ModelManipulationException {
        doMoveTo(what, newContainer, index);
    }

    @Override
    public void moveTo(EObject what, EObject newContainer, EReference reference) throws ModelManipulationException {
        EClass newContainerClass = newContainer.eClass();
        Preconditions.checkArgument(reference.getEContainingClass().isSuperTypeOf(newContainerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                newContainerClass, reference.getName());
        Preconditions.checkArgument(reference.getEReferenceType().isInstance(what),
                FEATURE_TYPE_MISMATCH,
                reference.getName(), what);
        Preconditions.checkArgument(reference.isContainment(),
                NOT_IN_CONTAINMENT_HIERARCHY_MESSAGE);
        doMoveTo(what, newContainer, reference);
    }

    @Override
    public void moveTo(EObject what, EObject newContainer, EReference reference, int index)
            throws ModelManipulationException {
        EClass newContainerClass = newContainer.eClass();
        Preconditions.checkArgument(reference.getEContainingClass().isSuperTypeOf(newContainerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                newContainerClass, reference.getName());
        Preconditions.checkArgument(reference.getEReferenceType().isInstance(what),
                FEATURE_TYPE_MISMATCH,
                reference.getName(), what);
        Preconditions.checkArgument(reference.isMany(), INVALID_MULTIPLICITY_FOR_POSITIONING_MESSAGE);
        Preconditions.checkArgument(reference.isContainment(),
                NOT_IN_CONTAINMENT_HIERARCHY_MESSAGE);
        
        doMoveTo(what, newContainer, reference, index);
    }

    @Override
    public void moveTo(Collection<EObject> what, EObject newContainer, EReference reference)
            throws ModelManipulationException {
        EClass newContainerClass = newContainer.eClass();
        Preconditions.checkArgument(reference.getEContainingClass().isSuperTypeOf(newContainerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                newContainerClass, reference.getName());
        for (EObject element: what) {
            Preconditions.checkArgument(reference.getEReferenceType().isInstance(element),
                    FEATURE_TYPE_MISMATCH,
                    reference.getName(), element);            
        }
        Preconditions.checkArgument(reference.isContainment(),
                NOT_IN_CONTAINMENT_HIERARCHY_MESSAGE);
        doMoveTo(what, newContainer, reference);
    }

    @Override
    public void changeIndex(EObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException {
        EClass containerClass = container.eClass();
        Preconditions.checkArgument(feature.getEContainingClass().isSuperTypeOf(containerClass),
                UNDEFINED_ESTRUCTURAL_FEATURE_FOR_CONTAINER_MESSAGE,
                containerClass, feature.getName());
        Preconditions.checkArgument(feature.isMany(), INVALID_MULTIPLICITY_FOR_POSITIONING_MESSAGE);
        doChangeIndex(container, feature, oldIndex, newIndex);
    }
    
    
    /**
     * @since 2.1
     */
    protected static Collection<Object> getSlotValuesInternal(EObject container, EStructuralFeature feature) {
        Object slot = container.eGet(feature);
        if (feature.isMany()) {
            return (Collection<Object>) slot;
        } else {
            return (slot == null)? Collections.emptySet() : Collections.singleton(slot);
        }
    }

}
