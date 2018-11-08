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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableWriterBinary;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableWriterUnary;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.Direction;
import org.eclipse.viatra.query.runtime.tabular.EcoreIndexHost;

/**
 * Manipulates an {@link EcoreIndexHost}.
 * 
 * <p> The ModelObject type parameter may be specialized to any surrogate key type in use, such as {@link String} or {@link Long}.
 * 
 * <p> Implementors must provide their respective facility for creating and deleting objects (surrogate keys), 
 * therefore the methods {@link #create(Void, EClass)} and {@link #createChild(Object, EReference, EClass)} are not implemented.
 * To implement, use helpers {@link #registerInstance(EClassifier, Object, ITableWriterUnary.Table)} and {@link #addInternal(Object, EStructuralFeature, Object, ITableWriterBinary.Table, ITableWriterUnary.Table)},
 * as well as {@link #initializeNewlyCreatedObject(Object, EClass)}.
 * 
 *  
 * <p> Limitations: <ul>
 * <li>Currently, root containers are not represented (always null). 
 * <li>Ordered collections are not maintained or represented.
 * <li>No treatment of opposites (incl. container features) yet.
 * </ul> 
 * 
 * @noreference This class is experimental with an unstable API, not recommended for general usage yet. 
 * @author Gabor Bergmann
 * @since 2.1
 */
public abstract class IndexHostManipulations<ModelObject> implements IEcoreManipulations<Void, ModelObject> {
    
    EcoreIndexHost host;
    protected static final TupleMask BIND_SOURCE = TupleMask.selectSingle(0, 2);
    protected static final TupleMask BIND_TARGET = TupleMask.selectSingle(1, 2);

    public IndexHostManipulations(EcoreIndexHost host) {
        super();
        this.host = host;
    }

    
    /**
     * Removes a model element from the model. 
     * 
     * <p> An efficient implementation shall: <ul> 
     *  <li> discover the type of the deleted element 
     *  <li> remove the element from the unary table corresponding to the type (use {@link #unregisterInstance(EClassifier, Object, ITableWriterUnary.Table)}), 
     *  <li> as well as recursively remove any contained objects and assigned features.
     * </ul> 
     *  
     * @return the exact type
     * @param element the model element to delete from the model
     * @throws ModelManipulationException 
     */
    protected void deleteWithOutgoing(ModelObject element) throws ModelManipulationException {
        EClass eClass = findExactType(element);
        
        deleteWithOutgoingInternal(element, eClass);
    }


    protected void deleteWithOutgoingInternal(ModelObject element, EClass eClass) throws ModelManipulationException {
        for (EStructuralFeature candidate : getAllPossibleFeatures(eClass)) {
            remove(element, candidate);
        }
        
        unregisterInstance(eClass, element, null);
    }

    
    /**
     * Implementors may override this with a more efficient lookup.
     */
    protected EClass findExactType(ModelObject element) throws ModelManipulationException {
        Tuple boundElement = Tuples.staticArityFlatTupleOf(element);
        for (EClass eClass : getAllClassTypes()) {
            boolean found = host.getTableDirectInstances(eClass).containsTuple(boundElement);
            if (found) return eClass;
        }
        throw new ModelManipulationException("Model object not found as direct instance of any known type + " + element);
    }

    protected Iterable<EClass> getAllClassTypes() {
        if (allClassTypes == null) {
            allClassTypes = host.getAllCurrentTablesDirectInstances().stream()
                    .filter((entry) -> entry.getKey() instanceof EClass)
                    .map((entry) -> (EClass) entry.getKey())
                    .collect(Collectors.toList());
        }
        return allClassTypes;
    }
    private List<EClass> allClassTypes = null;
    

    /**
     * Removes a model element from the model. 
     * 
     * <p> The implementation must: <ul> 
     *  <li> perform everything that {@link #deleteWithOutgoing(Object)} does, as well as
     *  <li> remove any containment or cross-references pointing to this element.
     * </ul> 
     *  
     * @param element the model element to delete from the model
     * @throws ModelManipulationException 
     */
    protected void deleteWithAllDangling(ModelObject element) throws ModelManipulationException {
        EClass eClass = findExactType(element);
        
        
        // first delete incoming crossrefs
        for (Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>> candidate : getAllCrossrefsPossiblyIncoming(eClass)) {
            // remove previous value (if any)
            Tuple targetBinding = Tuples.staticArityFlatTupleOf(element);
            int oldSourceCount = candidate.getValue().countTuples(BIND_TARGET, targetBinding);
            if (oldSourceCount > 0) {
                List<ModelObject> oldSourcesCopy = new ArrayList<>(oldSourceCount);
                Iterable<? extends Object> oldSources = 
                        candidate.getValue().enumerateValues(BIND_TARGET, targetBinding);
                for (Object oldSource: oldSources) { // copy to avoid concurrent modification problems
                    oldSourcesCopy.add((ModelObject) oldSource);
                }
                for (ModelObject oldSource : oldSourcesCopy) {
                    removeInternal(oldSource, candidate.getKey(), element, candidate.getValue(), null);
                }
            }
        }
        // then incoming containment, and outgoing features
        removeFromCurrentContainer(element, eClass);
        deleteWithOutgoingInternal(element, eClass);
    }
    
    

    /**
     * <p> The implementation must: <ul> 
     *  <li> remove the containment reference pointing to this element, if it exists.
     * </ul> 
     * @throws ModelManipulationException 
     */
    protected void removeFromCurrentContainer(ModelObject value) throws ModelManipulationException {
        EClass eClass = findExactType(value);
        removeFromCurrentContainer(value, eClass);
        
    }


    protected void removeFromCurrentContainer(ModelObject value, EClass eClass) {
        for (Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>> candidate: getAllRefsPossiblyContaining(eClass)) {
            // remove previous value (if any)
            Tuple targetBinding = Tuples.staticArityFlatTupleOf(value);
            Iterable<? extends Object> oldContainers = 
                    candidate.getValue().enumerateValues(BIND_TARGET, targetBinding);
            // there is either 0 or 1 containing edges of this candidate type
            for (Object oldContainer : oldContainers) {
                candidate.getValue().write(Direction.DELETE, oldContainer, value);

                // we already removed the containment edge, no reason to proceed any further
                return; 
            }
        }
    }

    protected Iterable<Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>>> getAllRefsPossiblyContaining(EClass eClass) {
        return allPossibleContainers.computeIfAbsent(eClass, (type) -> host.getAllCurrentTablesFeatures().stream()
                .filter((entry) -> FeatureKind.CONTAINMENT_REF == FeatureKind.of(entry.getKey()) &&
                        ((EReference)entry.getKey()).getEReferenceType().isSuperTypeOf(eClass))
                .collect(Collectors.toList()));
    }
    private Map<EClass, List<Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>>>> allPossibleContainers = new HashMap<>();

    protected Iterable<Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>>> getAllCrossrefsPossiblyIncoming(EClass eClass) {
        return allPossibleCrossRefs.computeIfAbsent(eClass, (type) -> host.getAllCurrentTablesFeatures().stream()
                .filter((entry) -> FeatureKind.CROSS_REF == FeatureKind.of(entry.getKey()) &&
                        ((EReference)entry.getKey()).getEReferenceType().isSuperTypeOf(eClass))
                .collect(Collectors.toList()));
    }
    private Map<EClass, List<Entry<EStructuralFeature, ITableWriterBinary.Table<Object, Object>>>> allPossibleCrossRefs = new HashMap<>();
    
    protected Iterable<EStructuralFeature> getAllPossibleFeatures(EClass exactType) {
        return exactType.getEAllStructuralFeatures();
    }
    
    /**
     * Unregisters an instance of a classifier.  
     * 
     * 
     * @param feature
     * @param oldValue
     * @param instanceTableOptional can be null, in which case the table is looked up based on the type
     */
    protected void unregisterInstance(EClassifier type, Object oldValue,
            ITableWriterUnary.Table<Object> instanceTableOptional) {
        if (instanceTableOptional == null) instanceTableOptional = 
            host.getTableDirectInstances(type);
        instanceTableOptional.write(Direction.DELETE, oldValue);
    }

    /**
     * Registers an instance of a classifier. 
     * 
     * @param feature
     * @param newValue
     * @param instanceTableOptional can be null, in which case the table is looked up based on the type
     */
    protected void registerInstance(EClassifier type, Object newValue,
            ITableWriterUnary.Table<Object> instanceTableOptional) {
        if (instanceTableOptional == null) instanceTableOptional = 
                host.getTableDirectInstances(type);
        instanceTableOptional.write(Direction.INSERT, newValue);
    }

    
    
    /**
     * Initializes a newly created object to its default state.
     * 
     * While it is possible to customize this method directly, overriding 
     *  {@link #determineInitializationSequence(EClass)} or {@link #determineDefaultValues(EClass)}.
     * @throws ModelManipulationException 
     */
    protected void initializeNewlyCreatedObject(ModelObject instance, EClass clazz) throws ModelManipulationException {
        Initializer<ModelObject> initializer = 
                initializerActions.computeIfAbsent(clazz, this::determineInitializationSequence);
        if (initializer != null)
            initializer.initialize(instance);
    }
    protected Map<EClass, Initializer<ModelObject>> initializerActions = new HashMap<>();
    @FunctionalInterface
    protected static interface Initializer<ModelObject> {
        public void initialize(ModelObject object) throws ModelManipulationException;
    }
    
    /**
     * Called for each exact type at most once. 
     * Computes the initalization steps that will be performed for each instance of the class. 
     * 
     * <p> The default implementation derives the necessary steps from the default values of attributes.
     * Feel free to override with domain-specific initialization steps, 
     *  such as setting key attributes to uniquely generated values.
     */
    protected Initializer<ModelObject> determineInitializationSequence(EClass clazz) {
        Map<EAttribute, Object> defaultValues = determineDefaultValues(clazz);
        return makeInitializer(defaultValues);
    }
    
    /**
     * Called for each exact type at most once. 
     * Computes the default values of attributes, wherever non-derived and different from the datatype default.
     * 
     * <p> The default implementation derives the defaults from a template instance initialized by the {@link EFactory}..
     * Feel free to override with domain-specific initialization steps, 
     *  or override {@link #determineInitializationSequence(EClass)} for larger flexibility.
     */
    protected Map<EAttribute, Object> determineDefaultValues(EClass clazz) {
        Map<EAttribute, Object> defaultValues = new HashMap<>();
        Optional<EObject> templateInstance = createTemplateInstance(clazz);
        for (EAttribute attribute : clazz.getEAllAttributes()) {
            if (attribute.isDerived()) continue;
            Object defaultValue = templateInstance.isPresent() ?
                    templateInstance.get().eGet(attribute) :
                            attribute.getEType().getDefaultValue();
            if (defaultValue != null) {
                defaultValues.put(attribute, defaultValue);
            }
        }
        return defaultValues;
    }


    /**
     * Creates an (optional) vanila instance of the class to learn about the default values.
     * May fail, or may be overridden to omit the template creation.  
     * @return null if creation is unsuccessful or undesired
     */
    protected Optional<EObject> createTemplateInstance(EClass clazz) {
        try {
            return Optional.of(clazz.getEPackage().getEFactoryInstance().create(clazz));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
    

    /**
     * Constructs an initializer that sets attributes of the model object to the given default values.
     */
    protected Initializer<ModelObject> makeInitializer(Map<EAttribute, Object> defaultValues) {
        return (modelObject) -> {
            for (Entry<EAttribute, Object> entry : defaultValues.entrySet()) {
                set(modelObject, entry.getKey(), entry.getValue());
            }
        };
    }


    /**
     * @param featureTable optional, can be supplied if known; will be looked up based on feature if null is supplied
     * @param instanceTable optional, can be supplied if known; will be looked up based on feature if null is supplied
     * @throws ModelManipulationException 
     */
    protected void removeInternal(
            ModelObject container,
            EStructuralFeature feature, 
            Object oldValue, 
            ITableWriterBinary.Table<Object, Object> featureTable,
            ITableWriterUnary.Table<Object> instanceTable) throws ModelManipulationException 
    {
        if (featureTable == null) featureTable = host.getTableFeatureSlots(feature);
        
        featureTable.write(Direction.DELETE, container, oldValue);
        
        switch (FeatureKind.of(feature)) {
            case CONTAINMENT_REF: 
                deleteWithOutgoing((ModelObject) oldValue);
                break;
            case ATTRIBUTE:
                unregisterInstance(feature.getEType(), oldValue, instanceTable);
                break;
        }
    }

    protected enum FeatureKind {
        CROSS_REF,
        CONTAINMENT_REF,
        ATTRIBUTE;
        
        static FeatureKind of(EStructuralFeature feature) {
            if (feature instanceof EReference) 
                return ((EReference) feature).isContainment() ? CONTAINMENT_REF : CROSS_REF; 
            else 
                return ATTRIBUTE;
        }
    }
    /**
     * PRE: Previous containments are erased at this point, if adding to a containment feature. 
     * This is required to avoid double containment.
     * 
     * @param featureTable optional, can be supplied if known; will be looked up based on feature if null is supplied
     * @param instanceTable optional, can be supplied if known; will be looked up based on feature if null is supplied
     */
    protected void addInternal(
            ModelObject container,
            EStructuralFeature feature, 
            Object newValue, 
            ITableWriterBinary.Table<Object, Object> featureTable,
            ITableWriterUnary.Table<Object> instanceTable) 
    {
        if (featureTable == null) featureTable = host.getTableFeatureSlots(feature);
        
        featureTable.write(Direction.INSERT, container, newValue);
        
        if (feature instanceof EAttribute) {
            registerInstance(feature.getEType(), newValue, instanceTable);
        }
    }
    
        
    @Override
    public void addTo(ModelObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException {
        addInternal(container, feature, element, null, null);
    }

    @Override
    public void addTo(ModelObject container, EStructuralFeature feature, Object element, int index)
            throws ModelManipulationException {
        // position ignored
        addTo(container, feature, element);
    }

    @Override
    public void addAllTo(ModelObject container, EStructuralFeature reference, Collection<? extends Object> elements)
            throws ModelManipulationException {
        ITableWriterBinary.Table<Object, Object> table = host.getTableFeatureSlots(reference);
        for (Object element : elements) {
            addInternal(container, reference, element, table, null);
        }
    }

    @Override
    public void set(ModelObject container, EStructuralFeature feature, Object value) throws ModelManipulationException {
        ITableWriterBinary.Table<Object, Object> table = host.getTableFeatureSlots(feature);
        
        // remove previous value (if any)
        Tuple sourceBinding = Tuples.staticArityFlatTupleOf(container);
        Iterable<? extends Object> oldValues = 
                table.enumerateValues(BIND_SOURCE, sourceBinding);
        Object removed = null;
        for (Object oldValue : oldValues) {
            if (removed == null) { // this is the first (hopefully only) old value to remove
                removeInternal(container, feature, oldValue, table, null);
                removed = oldValue;
            } else { // error
                throw new ModelManipulationException(String.format(
                        "Found multiple values (%s, %s) of feature (%s) on object (%s) when trying to SET to new value (%s)",
                        removed, oldValue, feature.getName(), container, value));
            }
        }
        
        // remove from old containment, if this is a containment feature
        if (feature instanceof EReference) {
            if (((EReference) feature).isContainer())
                removeFromCurrentContainer((ModelObject)value);
        }
        
        addInternal(container, feature, value, table, null);
    }


    @Override
    public void remove(ModelObject container, EStructuralFeature feature, Object element)
            throws ModelManipulationException {
        removeInternal(container, feature, element, null, null);
    }

    @Override
    public void remove(ModelObject container, EStructuralFeature feature, int index) throws ModelManipulationException {
        throw new UnsupportedOperationException("Position-based removal unsupported.");
    }

    @Override
    public void remove(ModelObject container, EStructuralFeature feature) throws ModelManipulationException {
        ITableWriterBinary.Table<Object, Object> table = host.getTableFeatureSlots(feature);
        ITableWriterUnary.Table<Object> tableDirectInstances = 
                (feature instanceof EAttribute) ? 
                        host.getTableDirectInstances(feature.getEType()) // lookup only once
                        : null; // not needed for references

        // remove previous value (if any)
        Tuple sourceBinding = Tuples.staticArityFlatTupleOf(container);
        int oldValueCount = table.countTuples(BIND_SOURCE, sourceBinding);
        if (oldValueCount > 0) {
            List<Object> oldValuesCopy = new ArrayList<>(oldValueCount);
            Iterable<? extends Object> oldValues = 
                    table.enumerateValues(BIND_SOURCE, sourceBinding);
            for (Object oldValue : oldValues) { // copy to avoid concurrent modification problems
                oldValuesCopy.add(oldValue);
            }
            for (Object oldValue : oldValuesCopy) {
                removeInternal(container, feature, oldValue, table, tableDirectInstances);
            }
        }
    }
    
    @Override
    public void remove(ModelObject object) throws ModelManipulationException {
        deleteWithAllDangling(object);
    }

    @Override
    public void moveTo(ModelObject what, Void newContainer) throws ModelManipulationException {
        removeFromCurrentContainer(what);
    }

    @Override
    public void moveTo(ModelObject what, Void newContainer, int index) throws ModelManipulationException {
        // position ignored
        removeFromCurrentContainer(what);
    }

    @Override
    public void moveTo(ModelObject what, ModelObject newContainer, EReference reference)
            throws ModelManipulationException {
        removeFromCurrentContainer(what);
        addInternal(newContainer, reference, what, null, null);
    }

    @Override
    public void moveTo(ModelObject what, ModelObject newContainer, EReference reference, int index)
            throws ModelManipulationException {
        // position ignored
        removeFromCurrentContainer(what);
        addInternal(newContainer, reference, what, null, null);
    }

    @Override
    public void moveAllTo(Collection<ModelObject> what, ModelObject newContainer, EReference reference)
            throws ModelManipulationException 
    {
        ITableWriterBinary.Table<Object, Object> table = host.getTableFeatureSlots(reference);
        for (Object element : what) {
            removeFromCurrentContainer((ModelObject) element);
            addInternal(newContainer, reference, element, table, null);
        }
    }

    @Override
    public void changeIndex(ModelObject container, EStructuralFeature feature, int oldIndex, int newIndex)
            throws ModelManipulationException {
        // position ignored, NOP
    }


}
