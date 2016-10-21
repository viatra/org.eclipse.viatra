/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.preference.PreferenceConstants;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public enum QueryResultViewModel {

    INSTANCE;
    
    private Set<QueryResultTreeInput> inputs;
    private QueryEvaluationHint defaultHint;
    
    private QueryResultViewModel() {
        this.inputs = Sets.newHashSet();
        this.defaultHint = new QueryEvaluationHint(null, new ReteBackendFactory());
    }
    
    protected QueryResultTreeInput createInput(IModelConnector connector, IModelConnectorTypeEnum type) throws ViatraQueryException {
        checkArgument(connector != null, "Connector cannot be null");
        checkArgument(type != null, "Type cannot be null");
        Notifier notifier = connector.getNotifier(type);
        
        boolean wildcardMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.WILDCARD_MODE);
        boolean dynamicEMFMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.DYNAMIC_EMF_MODE);
        BaseIndexOptions options = new BaseIndexOptions(dynamicEMFMode, wildcardMode);
        QueryScope scope = new EMFScope(notifier, options);
        
        AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
        QueryResultTreeInput input = createInput(engine, false);
        input.setModelConnector(connector);
        return input;
    }

    /**
     * This method is intended to support existing engines 
     * @param engine
     * @return
     */
    protected QueryResultTreeInput createInput(AdvancedViatraQueryEngine engine, boolean readOnlyEngine) {
        QueryResultTreeInput input = new QueryResultTreeInput(engine, QuerySpecificationRegistry.getInstance(),
                readOnlyEngine, defaultHint);
        inputs.add(input);
        return input;
    }
    
    protected boolean removeInput(QueryResultTreeInput input) {
        checkArgument(input != null, "Input cannot be null");
        boolean removed = inputs.remove(input);
        if(removed) {
            AdvancedViatraQueryEngine engine = input.getEngine();
            input.dispose();
            if(!input.isReadOnlyEngine()) {
                engine.dispose();
            }
        }
        return removed;
    }
}
