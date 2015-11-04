/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.validation.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.incquery.validation.core.api.IConstraintSpecification;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * The constraint extension registry is singleton utility for 
 * accessing the constraint specifications registered through extensions
 * (see EMF-IncQuery @Constraint annotation)
 * 
 * @author Abel Hegedus
 *
 */
public class ConstraintExtensionRegistry {
    
    private static final String CONSTRAINT_ATTRIBUTE_NAME = "constraint";
    private static final String VALIDATION_RUNTIME_CONSTRAINT_EXTENSION_ID = "org.eclipse.incquery.validation.runtime.constraint";
    private static final String EDITOR_ID_ATTRIBUTE_NAME = "editorId";
    private static final String ENABLED_FOR_EDITOR_ATTRIBUTE_NAME = "enabledForEditor";
    private static final String CLASS_ATTRIBUTE_NAME = "class";

    static Set<String> genericEditorIds = Sets.newHashSet(
            "org.eclipse.emf.ecore.presentation.XMLReflectiveEditorID",
            "org.eclipse.emf.ecore.presentation.ReflectiveEditorID", "org.eclipse.emf.genericEditor");
    
    private static Multimap<String, IConstraintSpecification> editorConstraintSpecificationMap;

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
    public static synchronized Multimap<String, IConstraintSpecification> getEditorConstraintSpecificationMap() {
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
        return getEditorConstraintSpecificationMap().containsKey(editorId);
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
            return ImmutableSet.copyOf(getEditorConstraintSpecificationMap().values());
        }
        Set<IConstraintSpecification> set = new HashSet<IConstraintSpecification>(getEditorConstraintSpecificationMap()
                .get(editorId));
        set.addAll(getEditorConstraintSpecificationMap().get("*"));
        return set;
    }

    /**
     * Loads and returns the constraint specifications registered in the available extensions. (constraintspecification
     * extension point extensions)
     * 
     * @return A Multimap containing all the registered constraint specifications from the available extension for each
     *         editor Id.
     */
    private static synchronized Multimap<String, IConstraintSpecification> loadConstraintSpecificationsFromExtensions() {
        Multimap<String, IConstraintSpecification> result = HashMultimap.create();
    
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
            Multimap<String, IConstraintSpecification> result, IConfigurationElement ce) {
        try {
            List<String> ids = new ArrayList<String>();
            for (IConfigurationElement child : ce.getChildren()) {
                if (child.getName().equals(ENABLED_FOR_EDITOR_ATTRIBUTE_NAME)) {
                    String id = child.getAttribute(EDITOR_ID_ATTRIBUTE_NAME);
                    if (id != null && !id.equals("")) {
                        ids.add(id);
                    }
                }
            }
    
            Object o = ce.createExecutableExtension(CLASS_ATTRIBUTE_NAME);
            if (o instanceof IConstraintSpecification) {
                if (ids.isEmpty()) {
                    ids.add("*");
                }
                for (String id : ids) {
                    result.put(id, (IConstraintSpecification) o);
                }
            }
        } catch (CoreException e) {
            Logger logger = IncQueryLoggingUtil.getLogger(ConstraintExtensionRegistry.class);
            logger.error("Error loading EMF-IncQuery Validation ConstraintSpecification", e);
        }
    }
    
}
