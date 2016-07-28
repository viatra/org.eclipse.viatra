/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategyFactory;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

/**
 * This interface is only to overview the required methods for exploration strategies. It is not used explicitly.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public interface IDseStrategyContext {

    void init();
    
    Notifier getModel();
    EditingDomain getEditingDomain();
    ViatraQueryEngine getQueryEngine();
    RuleEngine getRuleEngine();
    IStrategy getStrategy();
    ExplorerThread getExplorerThread();
    List<IObjective> getObjectives();
    IObjective[][] getLeveledObjectives();
    List<IGlobalConstraint> getGlobalConstraints();
    
    SolutionStore getSolutionStore();
    void newSolution();
// TODO void newSolution(TrajectoryFitness trajectoryFitness);
    
    
    ObjectiveComparatorHelper getObjectiveComparatorHelper();

    GlobalContext getGlobalContext();
    Set<BatchTransformationRule<?, ?>> getRules();
    BatchTransformationRule<?, ?> getRuleByRuleSpecification(RuleSpecification<?> ruleSpecification);
    ExplorerThread tryStartNewThread(IStrategy strategy); /*IDseStrategyContext originalContext*/
    ExplorerThread tryStartNewThreadWithoutModelClone(IStrategy strategy);
    void startAllThreads(IStrategyFactory strategyFactory);
    Object getSharedObject();
    void setSharedObject(Object sharedObject);
    
    
    DesignSpaceManager getDesignSpaceManager();
    IStateCoder getStateCoder();
    IDesignSpace getDesignSpace();
    TrajectoryInfo getTrajectoryInfo();
    List<Object> getTrajectory();
    List<Object> getTrajectoryCopied();
    int getDepth();
    Object getCurrentStateId();

    Object getTransitionByActivation(Activation<?> activation);
    Activation<?> getActivationById(Object activationId);
    BatchTransformationRule<?, ?> getRuleByActivation(Activation<?> activation);
    BatchTransformationRule<?, ?> getRuleByActivationId(Object activationId);
    
    Collection<Object> getCurrentActivationIds();
    Collection<Object> getUntraversedActivationIds();
// TODO Object getArbitraryActivationId();
// TODO Object getArbitraryUntraversedActivationId();

    void executeAcitvationId(Object activationId);
    boolean tryExecuteAcitvationId(Object activationId);
    boolean executeRandomActivationId();
    void executeTrajectory(Object[] activationIds);
    void executeTrajectory(Object[] activationIds, int excludedIndex);
    int executeTrajectoryByTrying(Object[] activationIds);
    int executeTrajectoryByTrying(Object[] activationIds, int excludedIndex);
    int executeTrajectoryWithoutStateCoding(Object[] activationIds);
    int executeTrajectoryWithoutStateCoding(Object[] activationIds, int excludedIndex);
    int executeTrajectoryByTryingWithoutStateCoding(Object[] activationIds);
    int executeTrajectoryByTryingWithoutStateCoding(Object[] activationIds, int excludedIndex);

    boolean backtrack();
 // TODO int backtrack(int times);
    void backtrackUntilRoot();

    Fitness calculateFitness();
    Fitness getLastFitness();
    boolean checkGlobalConstraints();
    boolean isCurrentStateAlreadyTraversed();
    // this needs states stored:
    boolean isCurrentStateInTrajectory();

}
