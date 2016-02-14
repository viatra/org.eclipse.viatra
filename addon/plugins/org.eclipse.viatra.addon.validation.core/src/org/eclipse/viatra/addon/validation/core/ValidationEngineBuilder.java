/*******************************************************************************
 * Copyright (c) 2010-2014, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import org.apache.log4j.Logger;
import org.eclipse.viatra.addon.validation.core.api.IValidationEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;

/**
 * 
 * Builder for {@link ValidationEngine} objects.
 * 
 * @author Abel Hegedus
 *
 */
public class ValidationEngineBuilder {

    private ViatraQueryEngine queryEngine = null;
    private Logger logger = null;

    protected ValidationEngineBuilder() {
        
    }
    
    /**
     * @return a new builder to setup validation engines
     */
    public static ValidationEngineBuilder create(){
        
        return new ValidationEngineBuilder();
        
    }
    
    /**
     * Prepares a new {@link ValidationEngine} using the configured parameters.
     * 
     * @return an uninitialized validation engine
     * @throws IllegalStateException if the {@link ViatraQueryEngine} for the
     *   new validation engine cannot be determined
     */
    public IValidationEngine build() {
        checkState(queryEngine != null, "Must initialize engine before building!");
        if(logger == null){
            logger = Logger.getLogger(ValidationEngine.class);
        }
        checkState(logger != null, "Must initialize logger before building!");
        return new ValidationEngine(queryEngine, logger);
    }
    
    public ValidationEngineBuilder setEngine(ViatraQueryEngine engine) {
        checkArgument(engine != null, "Engine cannot be null!");
        this.queryEngine = engine;
        return this;
    }
    
    public ValidationEngineBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
    
    
    
}
