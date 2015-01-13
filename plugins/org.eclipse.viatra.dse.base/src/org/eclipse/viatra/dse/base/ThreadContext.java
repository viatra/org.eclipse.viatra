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
package org.eclipse.viatra.dse.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.guidance.ApplicationVectorUpdater;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;

/**
 * This class holds all the information that is related to a single processing thread of the DesignSpaceExploration
 * process. For any attributes related to the Design Space Exploration process as a whole, see {@link GlobalContext}.
 * 
 * @author Miklos Foldenyi
 * 
 */
public class ThreadContext {

    private final GlobalContext globalContext;
    private final IStrategy strategy;
    private IExplorerThread explorerThread;
    private RuleEngine ruleEngine;
    private IncQueryEngine incqueryEngine;
    private EditingDomain domain;
    private EObject modelRoot;
    private DesignSpaceManager designSpaceManager;
    private List<IObjective> objectives;
    private List<IGlobalConstraint> globalConstraints;

    /**
     * This value is true after the {@link ThreadContext} has been initialized in it's own thread.
     */
    private AtomicBoolean inited = new AtomicBoolean(false);

    private final TrajectoryInfo trajectoryInfo;

    private Guidance guidance;
    private boolean isFirstThread = false;

    /**
     * Creates a {@link ThreadContext} and sets it up to be initialized on the given {@link TransactionalEditingDomain}
     * 
     * @param globalContext
     * @param strategyBase
     * @param domain
     * @param trajectoryInfoToClone
     * @param parentGuidance
     */
    public ThreadContext(final GlobalContext globalContext, IStrategy strategy, EditingDomain domain,
            TrajectoryInfo trajectoryInfoToClone, Guidance parentGuidance) {
        this.globalContext = globalContext;
        this.strategy = strategy;
        this.domain = domain;

        // clone if it is not null
        this.trajectoryInfo = trajectoryInfoToClone == null ? null : trajectoryInfoToClone.clone();

        if (parentGuidance != null) {
            guidance = parentGuidance.clone();
        }
    }

    /**
     * Initializes the {@link ThreadContext} by initializing the underlying {@link IncQueryEngine} and
     * {@link RuleEngine}. {@link Guidance} initialization is also happening within this method.
     * 
     * @throws IncQueryException
     */
    public void init() throws DSEException {

        AtomicBoolean isFirst = globalContext.getFirstThreadContextIniting();
        AtomicBoolean isFirstReady = globalContext.getFirstThreadContextInited();
        if (!isFirstReady.get()) {
            if (!isFirst.compareAndSet(false, true)) {
                try {
                    do {
                        Thread.sleep(5);
                    } while (!isFirstReady.get());
                } catch (InterruptedException e) {
                }
            } else {
                isFirstThread = true;
            }
        }
        // prohibit re-initialization
        checkArgument(!inited.getAndSet(true), "This Thread context has been initialized already!");

        modelRoot = domain.getResourceSet().getResources().get(0).getContents().get(0);
        checkArgument(modelRoot != null, "Cannot initialize ThreadContext on a null model.");

        try {
            // initialize IQEngine
            final EMFScope scope = new EMFScope(modelRoot);
            incqueryEngine = IncQueryEngine.on(scope);
        } catch (IncQueryException e) {
            throw new DSEException("Failed to create unmanaged IncQueryEngine on the model.", e);
        }

        // initialize RuleEngine
        ruleEngine = RuleEngines.createIncQueryRuleEngine(incqueryEngine);

        ChangeCommand addRuleCommand = new ChangeCommand(modelRoot) {
            @Override
            protected void doExecute() {
                // add rules to the RuleEngine
                for (TransformationRule<?> tr : globalContext.getTransformations()) {
                    ruleEngine.addRule(tr);
                }
            }
        };
        domain.getCommandStack().execute(addRuleCommand);

        if (isFirstThread) {

            objectives = globalContext.getObjectives();
            globalConstraints = globalContext.getGlobalConstraints();

        } else {
            objectives = new ArrayList<IObjective>();
            for (IObjective objective : globalContext.getObjectives()) {
                objectives.add(objective.createNew());
            }
            
            globalConstraints = new ArrayList<IGlobalConstraint>();
            for (IGlobalConstraint globalConstraint : globalContext.getGlobalConstraints()) {
                globalConstraints.add(globalConstraint);
            }
        }
        // create the thread specific DesignSpaceManager
        designSpaceManager = new DesignSpaceManager(modelRoot, domain, globalContext.getStateSerializerFactory(),
                globalContext.getDesignSpace(), trajectoryInfo, ruleEngine, incqueryEngine);

        // if there is a guidance registered, hook this thread's
        // ApplicationVectorUpdater
        if (guidance != null) {
            guidance.resetActivations(ruleEngine);
            designSpaceManager.setiRuleApplicationNumberChanged(new ApplicationVectorUpdater(guidance));
        }

        for (IObjective objective : objectives) {
            objective.init(this);
        }
        for (IGlobalConstraint globalConstraint : globalConstraints) {
            globalConstraint.init(this);
        }

        globalContext.initVisualizersForThread(this);
        
        strategy.init(this);
        
        if (isFirstThread) {
            isFirstReady.set(true);
        }

    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public DesignSpaceManager getDesignSpaceManager() {
        return designSpaceManager;
    }

    public EditingDomain getEditingDomain() {
        return domain;
    }

    public EObject getModelRoot() {
        return modelRoot;
    }

    public IncQueryEngine getIncqueryEngine() {
        return incqueryEngine;
    }

    public Guidance getGuidance() {
        return guidance;
    }

    public void setGuidance(Guidance guidance) {
        this.guidance = guidance;
    }

    public IStrategy getStrategy() {
        return strategy;
    }

    public IExplorerThread getExplorerThread() {
        return explorerThread;
    }

    public void setExplorerThread(IExplorerThread explorerThread) {
        this.explorerThread = explorerThread;
    }

}
