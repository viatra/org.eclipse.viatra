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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.guidance.ICriteria.EvaluationResult;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;

public class Guidance implements Cloneable {

    private IDependencyGraphResolver dependencyGraphResolver;
    private IOccurrenceVectorResolver occurrenceVectorResolver;

    private IDependencyGraph dependencyGraph;
    private Map<DSETransformationRule<?, ?>, RuleInfo> ruleInfos = new HashMap<DSETransformationRule<?, ?>, RuleInfo>();

    private List<ICriteria> cutOffCriterias = new ArrayList<ICriteria>();
    private List<ICriteria> selectionCriterias = new ArrayList<ICriteria>();

    private Set<PatternWithCardinality> goalPatterns;
    private Set<PatternWithCardinality> constraints;
    private Set<DSETransformationRule<?, ?>> rules;

    private List<DSETransformationRule<?, ?>> sortedRules;
    private final CriteriaContext criteriaContext = new CriteriaContext(this);

    private PetriAbstractionResult petriNetAbstractionResult;

    private final Comparator<DSETransformationRule<?, ?>> rulePriorityComparator = new Comparator<DSETransformationRule<?, ?>>() {
        @Override
        public int compare(DSETransformationRule<?, ?> o1,
                DSETransformationRule<?, ?> o2) {
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

    public Map<DSETransformationRule<?, ?>, RuleInfo> cloneRuleInfos() {
        HashMap<DSETransformationRule<?, ?>, RuleInfo> result = new HashMap<DSETransformationRule<?, ?>, RuleInfo>();
        for (DSETransformationRule<?, ?> rule : ruleInfos.keySet()) {
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
            Map<DSETransformationRule<?, ?>, Integer> occurrence = petriNetAbstractionResult
                    .getSolutions().get(0).getOccurrence();

            for (DSETransformationRule<?, ?> rule : rules) {

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

    public void ruleFired(DSETransformationRule<?, ?> rule, RuleEngine ruleEngine) {
        ruleInfos.get(rule).incApp();
        resetActivations(ruleEngine);
    }

    public void ruleUndone(DSETransformationRule<?, ?> rule, RuleEngine ruleEngine) {
        ruleInfos.get(rule).decApp();
        resetActivations(ruleEngine);
    }

    public void resetActivations(RuleEngine ruleEngine) {
        for (DSETransformationRule<?, ?> rule : rules) {
            Set<?> genericSet = ruleEngine.getActivations(rule.getRuleSpecification());
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

    public List<DSETransformationRule<?, ?>> evaluateSelectionCriterias() {
        for (RuleInfo ruleInfo : ruleInfos.values()) {
            ruleInfo.resetSelectionPriority();
        }
        for (ICriteria criteria : selectionCriterias) {
            criteria.evaluate(criteriaContext);
        }
        if (sortedRules == null || sortedRules.isEmpty()) {
            sortedRules = new ArrayList<DSETransformationRule<?, ?>>(rules);
        }
        Collections.sort(sortedRules, rulePriorityComparator);
        return sortedRules;
    }

    // ******* getters and setter *******

    public IDependencyGraphResolver getDependencyGraphResolver() {
        return dependencyGraphResolver;
    }

    /**
     * Defines a method to calculate a {@link IDependencyGraph} from {@link DSETransformationRule}s, global constraints and
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

    public Map<DSETransformationRule<?, ?>, RuleInfo> getRuleInfos() {
        return ruleInfos;
    }

    public void setRuleInfos(Map<DSETransformationRule<?, ?>, RuleInfo> ruleInfos) {
        this.ruleInfos = ruleInfos;
    }

    public RuleInfo addPriorityAndCostRuleInfo(DSETransformationRule<?, ?> rule, double priority,
            double cost) {
        RuleInfo ruleInfo = new RuleInfo(priority, cost);
        return ruleInfos.put(rule, ruleInfo);
    }

    public RuleInfo addPriorityRuleInfo(DSETransformationRule<?, ?> rule, double priority) {
        return addPriorityAndCostRuleInfo(rule, priority, 0);
    }

    public RuleInfo addCostRuleInfo(DSETransformationRule<?, ?> rule, double cost) {
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

    public Set<DSETransformationRule<?, ?>> getRules() {
        return rules;
    }

    public void setRules(Set<DSETransformationRule<?, ?>> rules) {
        this.rules = rules;
    }

    public PetriAbstractionResult getPetriNetAbstractionResult() {
        return petriNetAbstractionResult;
    }
    
    private static void processEObject(Map<EModelElement, Integer> initialMarking, EObject eObject) {

        // increment number of objects
        EClass eClass = eObject.eClass();
        Integer i = initialMarking.get(eClass);
        if (i == null) {
            throw new DSEException(
                    "The class "
                            + eClass.getName()
                            + " not found in the given meta models. Maybe you missed to call addMetaModelPackage with this parameter: "
                            + eClass.getEPackage().getNsURI());
        }
        initialMarking.put(eClass, i + 1);
        for (EClass superType : eClass.getEAllSuperTypes()) {
            initialMarking.put(superType, initialMarking.get(superType) + 1);
        }

        // increment number of references
        for (EReference eReference : eClass.getEReferences()) {
            if (!(eReference.isContainment() || eReference.isContainer())) {
                Object object = eObject.eGet(eReference);
                if (object != null) {
                    Integer i2 = initialMarking.get(eReference);
                    if (object instanceof EList<?>) {
                        i2 = i2 + ((EList<?>) object).size();
                    } else {
                        i2 = i2 + 1;
                    }
                    initialMarking.put(eReference, i2);
                }
            }
        }
    }

    public static Map<EModelElement, Integer> getInitialMarking(EObject rootEObject,
            List<? extends EModelElement> classesAndReferences) {

        // init initialMarking (result map)
        HashMap<EModelElement, Integer> initialMarking = new HashMap<EModelElement, Integer>();
        for (EModelElement element : classesAndReferences) {
            initialMarking.put(element, 0);
        }

        // process instance model
        processEObject(initialMarking, rootEObject);
        TreeIterator<EObject> allContents = rootEObject.eAllContents();
        while (allContents.hasNext()) {
            EObject eObject = allContents.next();
            processEObject(initialMarking, eObject);

        }

        return initialMarking;
    }
}
