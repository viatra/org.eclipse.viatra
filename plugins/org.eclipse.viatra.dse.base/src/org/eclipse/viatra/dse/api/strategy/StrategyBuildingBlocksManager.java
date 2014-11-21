/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api.strategy;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.guidance.IDependencyGraphResolver;
import org.eclipse.viatra.dse.guidance.IOccurrenceVectorResolver;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public enum StrategyBuildingBlocksManager {
    INSTANCE;

    public static final String CHECK_CONSTRAINTS = "org.eclipse.viatra.dse.api.strategy.checkconstraints";
    public static final String CHECK_GOAL_STATE = "org.eclipse.viatra.dse.api.strategy.checkgoalstate";
    public static final String NEXT_TRANSITION = "org.eclipse.viatra.dse.api.strategy.nexttransition";

    public static final String DEPENDENCY_RESOLVER = "org.eclipse.viatra.dse.ruledependency.resolver";

    public static final String OCCURRENCE_RESOLVER = "org.eclipse.viatra.dse.ruledependency.occurrence";

    private final Set<String> extensionPointNames;
    private final Multimap<String, IConfigurationElement> extensions = ArrayListMultimap.create();

    public Set<String> getExtensionPointNames() {
        return extensionPointNames;
    }

    private StrategyBuildingBlocksManager() {

        extensionPointNames = new HashSet<String>();
        extensionPointNames.add(CHECK_CONSTRAINTS);
        extensionPointNames.add(CHECK_GOAL_STATE);
        extensionPointNames.add(NEXT_TRANSITION);
        extensionPointNames.add(OCCURRENCE_RESOLVER);
        extensionPointNames.add(DEPENDENCY_RESOLVER);

    }

    public void addExtension(String extensionPointName, IConfigurationElement extension) {
        extensions.put(extensionPointName, extension);
    }

    public Object createExtensionById(String extensionPointName, String extensionId) {
        for (IConfigurationElement extension : extensions.get(extensionPointName)) {
            if (extension.getAttribute("id").equals(extensionId)) {
                try {
                    return extension.createExecutableExtension("class");
                } catch (CoreException e) {
                    throw new DSEException(e);
                }
            }
        }
        return null;
    }

    public Object createExtensionByName(String extensionPointName, String extensionName) {
        for (IConfigurationElement extension : extensions.get(extensionPointName)) {
            if (extension.getAttribute("name").equals(extensionName)) {
                try {
                    return extension.createExecutableExtension("class");
                } catch (CoreException e) {
                    throw new DSEException(e);
                }
            }
        }
        return null;
    }

    // ************* get by extension name, returns good type
    // *****************//

    public IDependencyGraphResolver createRuleDependencyResolverByName(String name) {
        return (IDependencyGraphResolver) createExtensionByName(DEPENDENCY_RESOLVER, name);
    }

    public IOccurrenceVectorResolver createOccurrenceVectorResolverByName(String name) {
        return (IOccurrenceVectorResolver) createExtensionByName(OCCURRENCE_RESOLVER, name);
    }

    public ICheckConstraints createCheckConstraintsByName(String extensionName) {
        return (ICheckConstraints) createExtensionByName(CHECK_CONSTRAINTS, extensionName);
    }

    public ICheckGoalState createCheckGoalStateByName(String extensionName) {
        return (ICheckGoalState) createExtensionByName(CHECK_GOAL_STATE, extensionName);
    }

    public INextTransition createNextTransitionByName(String extensionName) {
        return (INextTransition) createExtensionByName(NEXT_TRANSITION, extensionName);
    }

    // ************* get by extension id, returns good type *****************//

    public IDependencyGraphResolver createRuleDependencyResolverById(String id) {
        return (IDependencyGraphResolver) createExtensionByName(DEPENDENCY_RESOLVER, id);
    }

    public IOccurrenceVectorResolver createOccurrenceVectorResolverById(String id) {
        return (IOccurrenceVectorResolver) createExtensionByName(OCCURRENCE_RESOLVER, id);
    }

    public ICheckConstraints createCheckConstraintsById(String extensionName) {
        return (ICheckConstraints) createExtensionById(CHECK_CONSTRAINTS, extensionName);
    }

    public ICheckGoalState createCheckGoalStateById(String extensionName) {
        return (ICheckGoalState) createExtensionById(CHECK_GOAL_STATE, extensionName);
    }

    public INextTransition createNextTransitionById(String extensionName) {
        return (INextTransition) createExtensionById(NEXT_TRANSITION, extensionName);
    }

}
