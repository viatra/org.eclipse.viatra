/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.runtime;

import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.addon.validation.core.api.IValidationEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.util.IProvider;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * The validation manager is singleton that serves as a single entry point for using the validation.
 * <p>
 * It provides capabilities for:
 * <ul>
 * <li>accessing the constraint specifications registered through extensions (see VIATRA Query @Constraint annotation)
 * <li>initializing a new validation engine
 * </ul>
 * 
 * @deprecated Use {@link ConstraintExtensionRegistry} and {@link ValidationInitializerUtil} instead.
 * 
 * @author Balint Lorand
 *
 */
public final class ValidationManager {

    /**
     * Constructor hidden for utility class
     */
    private ValidationManager() {

    }

    /**
     * Returns the map of all the registered constraint specifications for the particular editor Ids.
     * 
     * @return A Multimap containing all the registered constraint specifications for each editor Id.
     * @deprecated Use {@link ConstraintExtensionRegistry#getEditorConstraintSpecificationMap()} instead
     */
    public static synchronized Multimap<String, IConstraintSpecification> getEditorConstraintSpecificationMap() {
        Multimap<String, IProvider<IConstraintSpecification>> constraintSpecificationMap = ConstraintExtensionRegistry.getEditorConstraintSpecificationMap();
        Multimap<String, IConstraintSpecification> unwrappedMap = Multimaps.transformValues(constraintSpecificationMap, new Function<IProvider<IConstraintSpecification>, IConstraintSpecification>() {
            @Override
            public IConstraintSpecification apply(IProvider<IConstraintSpecification> provider) {
                return provider.get();
            }
        });
        return unwrappedMap;
    }
    
    /**
     * Returns whether there are constraint specifications registered for an editor Id.
     * 
     * @param editorId
     *            The editor Id which should be checked
     * @return <code>true</code> if there are registered constraint specifications
     * @deprecated Use {@link ConstraintExtensionRegistry#isConstraintSpecificationsRegisteredForEditorId(String)} instead
     */
    public static synchronized boolean isConstraintSpecificationsRegisteredForEditorId(String editorId) {
        return ConstraintExtensionRegistry.isConstraintSpecificationsRegisteredForEditorId(editorId);
    }

    /**
     * Returns the registered constraint specifications for a particular editor Id.
     * 
     * @param editorId
     *            The editor Id for which the constraint specifications should be retrieved.
     * @return The Set of constraint specifications registered.
     * @deprecated Use {@link ConstraintExtensionRegistry#getConstraintSpecificationsForEditorId(String)} instead
     */
    public static synchronized Set<IConstraintSpecification> getConstraintSpecificationsForEditorId(String editorId) {
        return ConstraintExtensionRegistry.getConstraintSpecificationsForEditorId(editorId);
    }

    /**
     * Initializes a new validation engine implementing the IValidationEngine interface on the provided Notifier
     * instance with the constrains specified for the given editor Id.
     * 
     * @param notifier
     *            The Notifier object on which the validation engine should be initialized.
     * @param editorId
     *            An editor Id for which we wish to use the registered constraint specifications at the
     *            org.eclipse.viatra.addon.livevalidation.runtime.constraintspecification extension point.
     * @return The initialized validation engine.
     * @throws ViatraQueryException if there is an error creating the engine on the notifier
     * @deprecated Use {@link ValidationInitializerUtil#initializeValidationWithRegisteredConstraintsOnNotifier} instead.
     */
    public static IValidationEngine initializeValidationEngine(Notifier notifier, String editorId) throws ViatraQueryException {
        EMFScope scope = new EMFScope(notifier);
        return ValidationInitializerUtil.initializeValidationWithRegisteredConstraintsOnScope(scope, editorId);
    }

}
