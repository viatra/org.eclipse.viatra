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
package org.eclipse.viatra.dse.guidance;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.guidance.ICriteria.EvaluationResult;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;

public class Guidance implements Cloneable {

    private IDependencyGraphResolver dependencyGraphResolver;
    private IOccurrenceVectorResolver occurrenceVectorResolver;

    private IDependencyGraph dependencyGraph;
    private Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos = new HashMap<TransformationRule<? extends IPatternMatch>, RuleInfo>();

    private List<ICriteria> cutOffCriterias = new ArrayList<ICriteria>();
    private List<ICriteria> selectionCriterias = new ArrayList<ICriteria>();

    private Set<PatternWithCardinality> goalPatterns;
    private Set<PatternWithCardinality> constraints;
    private Set<TransformationRule<? extends IPatternMatch>> rules;

    private List<TransformationRule<? extends IPatternMatch>> sortedRules;
    private final CriteriaContext criteriaContext = new CriteriaContext(this);

    private PetriAbstractionResult petriNetAbstractionResult;

    private final Comparator<TransformationRule<? extends IPatternMatch>> rulePriorityComparator = new Comparator<TransformationRule<? extends IPatternMatch>>() {
        @Override
        public int compare(TransformationRule<? extends IPatternMatch> o1,
                TransformationRule<? extends IPatternMatch> o2) {
            RuleInfo ruleInfo1 = ruleInfos.get(o1);
            RuleInfo ruleInfo2 = ruleInfos.get(o2);
            double priority1 = ruleInfo1.getSelectionPriority() + ruleInfo1.getPriority();
            double priority2 = ruleInfo2.getSelectionPriority() + ruleInfo2.getPriority();
            if (priority1 > priority2) {
                return 1;
            }
            if (priority1 < priority2) {
                return -1;
            }
            return 0;
        }
    };

    public Guidance clone() {
        Guidance guidance = new Guidance();

        guidance.setConstraints(constraints);
        guidance.setCutOffCriterias(cutOffCriterias);
        guidance.setDependencyGraph(dependencyGraph);
        guidance.setDependencyGraphResolver(dependencyGraphResolver);
        guidance.setGoalPatterns(goalPatterns);
        guidance.setOccurrenceVectorResolver(occurrenceVectorResolver);
        guidance.setRuleInfos(this.cloneRuleInfos());
        guidance.setRules(rules);
        guidance.setSelectionCriterias(selectionCriterias);

        return guidance;
    }

    public Map<TransformationRule<? extends IPatternMatch>, RuleInfo> cloneRuleInfos() {
        HashMap<TransformationRule<? extends IPatternMatch>, RuleInfo> result = new HashMap<TransformationRule<? extends IPatternMatch>, RuleInfo>();
        for (TransformationRule<? extends IPatternMatch> rule : ruleInfos.keySet()) {
            result.put(rule, ruleInfos.get(rule).clone());
        }
        return result;
    }

    public void resolveDependencyGraph() {
        if (dependencyGraphResolver != null) {
            checkNotNull(rules);
            checkNotNull(goalPatterns);
            checkNotNull(constraints);
            dependencyGraph = dependencyGraphResolver.createRuleDependencyGraph(rules, constraints, goalPatterns);
        }
    }

    public void resolveOccurrenceVector(List<? extends EModelElement> classesAndReferences,
            Map<? extends EModelElement, Integer> initialMarking, List<Predicate> predicates) {
        if (occurrenceVectorResolver != null) {

            checkNotNull(rules, "Rules must be specified to calculate an occurrance vector.");

            petriNetAbstractionResult = occurrenceVectorResolver.calculateOccurrenceVector(classesAndReferences,
                    initialMarking, rules, predicates);

            // set occurrence for the rule info
            Map<TransformationRule<? extends IPatternMatch>, Integer> occurrence = petriNetAbstractionResult
                    .getSolutions().get(0).getOccurrence();

            for (TransformationRule<? extends IPatternMatch> rule : rules) {

                RuleInfo ruleInfo = ruleInfos.get(rule);
                if (ruleInfo == null) {
                    ruleInfo = new RuleInfo();
                    ruleInfo.setOccurrence(occurrence.get(rule));
                    ruleInfos.put(rule, ruleInfo);
                } else {
                    ruleInfo.setOccurrence(occurrence.get(rule));
                }
            }
        }

    }

    public void ruleFired(TransformationRule<? extends IPatternMatch> rule, RuleEngine ruleEngine) {
        ruleInfos.get(rule).incApp();
        resetActivations(ruleEngine);
    }

    public void ruleUndone(TransformationRule<? extends IPatternMatch> rule, RuleEngine ruleEngine) {
        ruleInfos.get(rule).decApp();
        resetActivations(ruleEngine);
    }

    public void resetActivations(RuleEngine ruleEngine) {
        for (TransformationRule<? extends IPatternMatch> rule : rules) {
            Set<?> genericSet = ruleEngine.getActivations(rule);
            @SuppressWarnings("unchecked")
            Set<Activation<?>> activationSet = (Set<Activation<?>>) genericSet;
            ruleInfos.get(rule).setActivations(activationSet);
        }
    }

    public EvaluationResult evaluateCutOffCriterias() {
        for (ICriteria criteria : cutOffCriterias) {
            if (criteria.evaluate(criteriaContext) == EvaluationResult.CUT_OFF) {
                return EvaluationResult.CUT_OFF;
            }
        }
        return EvaluationResult.NONE;
    }

    public List<TransformationRule<? extends IPatternMatch>> evaluateSelectionCriterias() {
        for (RuleInfo ruleInfo : ruleInfos.values()) {
            ruleInfo.resetSelectionPriority();
        }
        for (ICriteria criteria : selectionCriterias) {
            criteria.evaluate(criteriaContext);
        }
        if (sortedRules == null || sortedRules.isEmpty()) {
            sortedRules = new ArrayList<TransformationRule<? extends IPatternMatch>>(rules);
        }
        Collections.sort(sortedRules, rulePriorityComparator);
        return sortedRules;
    }

    // ******* getters and setter *******

    public IDependencyGraphResolver getDependencyGraphResolver() {
        return dependencyGraphResolver;
    }

    /**
     * Defines a method to calculate a {@link IDependencyGraph} from {@link TransformationRule}s, global constraints and
     * goal patterns. A derived class can be registered through an extension, and can be reached with the
     * {@link StrategyBuildingBlocksManager} singleton. The graph will be reachable from the {@link ThreadContext}.
     * 
     * @param dependencyGraphResolver
     */
    public void setDependencyGraphResolver(IDependencyGraphResolver dependencyGraphResolver) {
        this.dependencyGraphResolver = dependencyGraphResolver;
    }

    public IOccurrenceVectorResolver getOccuranceVectorResolver() {
        return occurrenceVectorResolver;
    }

    /**
     * Defines a method to calculate an occurrence vector.
     * 
     * @param occurrenceVectorResolver
     */
    public void setOccurrenceVectorResolver(IOccurrenceVectorResolver occurrenceVectorResolver) {
        this.occurrenceVectorResolver = occurrenceVectorResolver;
    }

    public IDependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public void setDependencyGraph(IDependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public Map<TransformationRule<? extends IPatternMatch>, RuleInfo> getRuleInfos() {
        return ruleInfos;
    }

    public void setRuleInfos(Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos) {
        this.ruleInfos = ruleInfos;
    }

    public RuleInfo addPriorityAndCostRuleInfo(TransformationRule<? extends IPatternMatch> rule, double priority,
            double cost) {
        RuleInfo ruleInfo = new RuleInfo(priority, cost);
        return ruleInfos.put(rule, ruleInfo);
    }

    public RuleInfo addPriorityRuleInfo(TransformationRule<? extends IPatternMatch> rule, double priority) {
        return addPriorityAndCostRuleInfo(rule, priority, 0);
    }

    public RuleInfo addCostRuleInfo(TransformationRule<? extends IPatternMatch> rule, double cost) {
        return addPriorityAndCostRuleInfo(rule, 0, cost);
    }

    public List<ICriteria> getCutOffCriterias() {
        return cutOffCriterias;
    }

    public void setCutOffCriterias(List<ICriteria> cutOffCriterias) {
        this.cutOffCriterias = cutOffCriterias;
    }

    public List<ICriteria> getSelectionCriterias() {
        return selectionCriterias;
    }

    public void setSelectionCriterias(List<ICriteria> selectionCriterias) {
        this.selectionCriterias = selectionCriterias;
    }

    public Set<PatternWithCardinality> getGoalPatterns() {
        return goalPatterns;
    }

    public void setGoalPatterns(Set<PatternWithCardinality> goalPatterns) {
        this.goalPatterns = goalPatterns;
    }

    public Set<PatternWithCardinality> getConstraints() {
        return constraints;
    }

    public void setConstraints(Set<PatternWithCardinality> constraints) {
        this.constraints = constraints;
    }

    public Set<TransformationRule<? extends IPatternMatch>> getRules() {
        return rules;
    }

    public void setRules(Set<TransformationRule<? extends IPatternMatch>> rules) {
        this.rules = rules;
    }

    public PetriAbstractionResult getPetriNetAbstractionResult() {
        return petriNetAbstractionResult;
    }
}
