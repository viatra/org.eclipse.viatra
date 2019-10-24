/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;
import org.eclipse.viatra.query.runtime.matchers.util.IProvider;

/**
 * The constraint extension registry is singleton utility for 
 * accessing the constraint specifications registered through extensions
 * (see VIATRA @Constraint annotation)
 * 
 * @author Abel Hegedus
 *
 */
public class ConstraintExtensionRegistry {

    private static final String WILDCARD_EDITOR_ID = "*";
    private static final String CONSTRAINT_ATTRIBUTE_NAME = "constraint";
    private static final String VALIDATION_RUNTIME_CONSTRAINT_EXTENSION_ID = "org.eclipse.viatra.addon.validation.runtime.constraint";
    private static final String EDITOR_ID_ATTRIBUTE_NAME = "editorId";
    private static final String ENABLED_FOR_EDITOR_ATTRIBUTE_NAME = "enabledForEditor";
    private static final String CLASS_ATTRIBUTE_NAME = "class";

    static Set<String> genericEditorIds = Stream.of(
                "org.eclipse.emf.ecore.presentation.XMLReflectiveEditorID",
                "org.eclipse.emf.ecore.presentation.ReflectiveEditorID", 
                "org.eclipse.emf.genericEditor"
            ).collect(Collectors.toSet());
    
    private static IMultiLookup<String, IProvider<IConstraintSpecification>> editorConstraintSpecificationMap;

    /**
     * Constructor hidden for utility class
     */
    private ConstraintExtensionRegistry() {

    }

    /**
     * Returns the map of all the registered constraint specifications for the particular editor Ids.
     * 
     * @return A Multimap containing all the registered constraint specifications for each editor Id.
     */
    protected static synchronized IMultiLookup<String, IProvider<IConstraintSpecification>> getEditorConstraintSpecificationMap() {
        if (editorConstraintSpecificationMap == null) {
            editorConstraintSpecificationMap = loadConstraintSpecificationsFromExtensions();
        }
        return editorConstraintSpecificationMap;
    }
    
    /**
     * Returns whether there are constraint specifications registered for an editor Id.
     * 
     * @param editorId
     *            The editor Id which should be checked
     * @return <code>true</code> if there are registered constraint specifications
     */
    public static synchronized boolean isConstraintSpecificationsRegisteredForEditorId(String editorId) {
        IMultiLookup<String, IProvider<IConstraintSpecification>> specificationMap = getEditorConstraintSpecificationMap();
        if(specificationMap.lookupExists(WILDCARD_EDITOR_ID)) {
            return true;
        }
        return specificationMap.lookupExists(editorId);
    }

    /**
     * Returns the registered constraint specifications for a particular editor Id.
     * 
     * @param editorId
     *            The editor Id for which the constraint specifications should be retrieved.
     * @return The Set of constraint specifications registered.
     */
    public static synchronized Set<IConstraintSpecification> getConstraintSpecificationsForEditorId(String editorId) {
        if (genericEditorIds.contains(editorId)) {
            Set<IConstraintSpecification> constraintSpecifications = unwrapConstraintSpecifications(StreamSupport.stream(getEditorConstraintSpecificationMap().distinctValues().spliterator(), false));
            return constraintSpecifications;
        }
        Set<IConstraintSpecification> set = unwrapConstraintSpecifications(Stream.concat(
                getEditorConstraintSpecificationMap().lookupOrEmpty(editorId).asStream(), 
                getEditorConstraintSpecificationMap().lookupOrEmpty(WILDCARD_EDITOR_ID).asStream()
        ));
        return set;
    }

    private static Set<IConstraintSpecification> unwrapConstraintSpecifications(Stream<IProvider<IConstraintSpecification>> providers) {
        return providers.filter(Objects::nonNull).map(Supplier::get).collect(Collectors.toSet());
    }

    /**
     * Loads and returns the constraint specifications registered in the available extensions. (constraintspecification
     * extension point extensions)
     * 
     * @return A IMultiLookup containing all the registered constraint specifications from the available extension for each
     *         editor Id.
     */
    private static synchronized IMultiLookup<String, IProvider<IConstraintSpecification>> loadConstraintSpecificationsFromExtensions() {
        IMultiLookup<String, IProvider<IConstraintSpecification>> result = 
                CollectionsFactory.createMultiLookup(Object.class, MemoryType.SETS, Object.class);
    
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint ep = reg.getExtensionPoint(VALIDATION_RUNTIME_CONSTRAINT_EXTENSION_ID);
    
        for (IExtension extension : ep.getExtensions()) {
            for (IConfigurationElement ce : extension.getConfigurationElements()) {
                if (ce.getName().equals(CONSTRAINT_ATTRIBUTE_NAME)) {
                    processConstraintSpecificationConfigurationElement(result, ce);
                }
            }
        }
        return result;
    }

    /**
     * Processes the given configuration element: in case if it is an instance of IConstraintSpecification it puts it in
     * the provided Multimap with the editor Ids it is registered for.
     * 
     * @param result
     *            The Multimap in which the constraint specification will be placed with it's editorIds.
     * @param ce
     *            The configuration element to be processed.
     */
    private static void processConstraintSpecificationConfigurationElement(
            IMultiLookup<String, IProvider<IConstraintSpecification>> result, IConfigurationElement ce) {
        List<String> ids = new ArrayList<String>();
        for (IConfigurationElement child : ce.getChildren()) {
            if (child.getName().equals(ENABLED_FOR_EDITOR_ATTRIBUTE_NAME)) {
                String id = child.getAttribute(EDITOR_ID_ATTRIBUTE_NAME);
                if (id != null && !id.equals("")) {
                    ids.add(id);
                }
            }
        }

        ConstraintSpecificationProvider constraintSpecificationProvider = new ConstraintSpecificationProvider(ce);
        if (ids.isEmpty()) {
            ids.add(WILDCARD_EDITOR_ID);
        }
        for (String id : ids) {
            result.addPair(id, constraintSpecificationProvider);
        }
    }
    
    /**
     * A provider implementation for PQuery instances based on extension elements. It is expected that the getter will only
     * @author stampie
     *
     */
    private static final class ConstraintSpecificationProvider implements IProvider<IConstraintSpecification> {

        private final IConfigurationElement element;
        private IConstraintSpecification constraintSpecification;
        
        public ConstraintSpecificationProvider(IConfigurationElement element) {
            this.element = element;
            this.constraintSpecification = null;
        }

        @Override
        public IConstraintSpecification get() {
            try {
                if (constraintSpecification == null) {
                    constraintSpecification = (IConstraintSpecification) element.createExecutableExtension(CLASS_ATTRIBUTE_NAME);
                }
                return constraintSpecification;
            } catch (CoreException e) {
                throw new IllegalArgumentException("Error initializing constraint specification", e);
            }
        }
    }
    
}
