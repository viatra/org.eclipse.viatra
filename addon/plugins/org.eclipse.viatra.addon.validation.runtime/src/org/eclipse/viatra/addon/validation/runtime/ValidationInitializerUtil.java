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
package org.eclipse.viatra.addon.validation.runtime;

import org.apache.log4j.Logger;
import org.eclipse.viatra.addon.validation.core.ValidationEngine;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.addon.validation.core.api.IValidationEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public final class ValidationInitializerUtil {

    private ValidationInitializerUtil() {}
    
    /**
     * Initializes a new validation engine implementing the IValidationEngine interface on the provided Notifier
     * instance with the constrains specified for the given editor Id.
     * 
     * @param scope
     *            The Notifier object on which the validation engine should be initialized.
     * @param editorId
     *            An editor Id for which we wish to use the registered constraint specifications at the
     *            org.eclipse.viatra.addon.livevalidation.runtime.constraintspecification extension point.
     * @return The initialized validation engine.
     * @throws ViatraQueryException if there is an error creating the engine on the scope
     */
    public static IValidationEngine initializeValidationWithRegisteredConstraintsOnScope(QueryScope scope,
            String editorId) throws ViatraQueryException {
        ViatraQueryEngine engine = ViatraQueryEngine.on(scope);
        Logger logger = ViatraQueryLoggingUtil.getLogger(ValidationEngine.class);
        IValidationEngine validationEngine = ValidationEngine.builder().setEngine(engine).setLogger(logger).build();
    
        for (IConstraintSpecification constraintSpecification : ConstraintExtensionRegistry.getConstraintSpecificationsForEditorId(editorId)) {
            validationEngine.addConstraintSpecification(constraintSpecification);
        }
        validationEngine.initialize();
    
        return validationEngine;
    }

}
