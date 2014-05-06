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

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.api.strategy.StrategyBase;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.guidance.ApplicationVectorUpdater;
import org.eclipse.viatra.dse.guidance.Guidance;

/**
 * This class holds all the information that is related to a single processing thread of the DesignSpaceExploration
 * process. For any attributes related to the Design Space Exploration process as a whole, see {@link GlobalContext}.
 * 
 * @author Miklos Foldenyi
 * 
 */
public class ThreadContext {

    private final GlobalContext globalContext;
    private final StrategyBase strategyBase;
    private/* final */IStrategy strategy;
    private RuleEngine ruleEngine;
    private IncQueryEngine incqueryEngine;
    private TransactionalEditingDomain ted;
    private EObject modelRoot;
    private DesignSpaceManager designSpaceManager;

    /**
     * This value is true after the {@link ThreadContext} has been initialized in it's own thread.
     */
    private AtomicBoolean inited = new AtomicBoolean(false);

    private static AtomicBoolean isFirstThreadInit = new AtomicBoolean(true);

    private final TrajectoryInfo trajectoryInfo;

    private Guidance guidance;

    /**
     * Creates a {@link ThreadContext} and sets it up to be initialized on the given {@link TransactionalEditingDomain}
     * 
     * @param globalContext
     * @param strategyBase
     * @param ted
     * @param trajectoryInfoToClone
     * @param parentGuidance
     */
    public ThreadContext(final GlobalContext globalContext, StrategyBase strategyBase, TransactionalEditingDomain ted,
            TrajectoryInfo trajectoryInfoToClone, Guidance parentGuidance) {
        this.globalContext = globalContext;
        this.strategyBase = strategyBase;
        this.ted = ted;

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
        // prohibit re-initialization
        checkArgument(!inited.getAndSet(true), "This Thread context has been initialized already!");

        modelRoot = ted.getResourceSet().getResources().get(0).getContents().get(0);
        checkArgument(modelRoot != null, "Cannot initialize ThreadContext on a null model.");

        try {
            // initialize IQEngine
            incqueryEngine = AdvancedIncQueryEngine.createUnmanagedEngine(modelRoot, true);
        } catch (IncQueryException e) {
            throw new DSEException("Failed to create unmanaged IncQueryEngine on the model.", e);
        }

        // initialize RuleEngine
        ruleEngine = RuleEngines.createIncQueryRuleEngine(incqueryEngine);

        RecordingCommand addRuleCommand = new RecordingCommand(ted) {
            @Override
            protected void doExecute() {
                // add rules to the RuleEngine
                for (TransformationRule<?> tr : globalContext.getTransformations()) {
                    ruleEngine.addRule(tr);
                }
            }
        };
        ted.getCommandStack().execute(addRuleCommand);

        if (isFirstThreadInit.get()) {
            // This code ensures, that the query specification is initialized, because it cannot be done in parallel
            try {

                for (PatternWithCardinality constraint : globalContext.getConstraints()) {
                    constraint.getQuerySpecification().getMatcher(incqueryEngine).countMatches();
                }

                for (PatternWithCardinality goal : globalContext.getGoalPatterns()) {
                    goal.getQuerySpecification().getMatcher(incqueryEngine).countMatches();
                }

            } catch (IncQueryException e) {
                throw new DSEException("IncqueryException when initializing query specifications", e);
            }
            isFirstThreadInit.set(false);
        }
        // create the thread specific DesignSpaceManager
        designSpaceManager = new DesignSpaceManager(ted, globalContext.getStateSerializerFactory(),
                globalContext.getDesignSpace(), trajectoryInfo, ruleEngine, incqueryEngine);

        // if there is a guidance registered, hook this thread's
        // ApplicationVectorUpdater
        if (guidance != null) {
            guidance.resetActivations(ruleEngine);
            designSpaceManager.setiRuleApplicationNumberChanged(new ApplicationVectorUpdater(guidance));
        }
    }

    // *** getters and setters

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public DesignSpaceManager getDesignSpaceManager() {
        return designSpaceManager;
    }

    public TransactionalEditingDomain getTed() {
        return ted;
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

    public StrategyBase getStrategyBase() {
        return strategyBase;
    }

    public IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }

}
