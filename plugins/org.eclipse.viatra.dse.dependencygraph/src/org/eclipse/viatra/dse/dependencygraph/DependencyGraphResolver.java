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
package org.eclipse.viatra.dse.dependencygraph;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.ModelElementMetaData;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.RuleMetaData;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.guidance.IDependencyGraphResolver;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.EdgeType;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.EdgeType.ClassType;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.NodeType;
import org.eclipse.viatra.dse.guidance.dependencygraph.simpleimpl.DependencyGraph;

public class DependencyGraphResolver implements IDependencyGraphResolver {

    @Override
    public IDependencyGraph createRuleDependencyGraph(Set<TransformationRule<? extends IPatternMatch>> transformations,
            Set<PatternWithCardinality> constraints, Set<PatternWithCardinality> goalPatterns) {

        IDependencyGraph dependencyGraph = new DependencyGraph();

        // Create nodes

        for (PatternWithCardinality goal : goalPatterns) {
            dependencyGraph.addNode(goal, NodeType.GOAL);
        }

        for (PatternWithCardinality constraint : constraints) {
            dependencyGraph.addNode(constraint, NodeType.CONSTRAINT);
        }

        for (TransformationRule<? extends IPatternMatch> rule : transformations) {
            dependencyGraph.addNode(rule);
        }

        // Create edges

        // edges goes rhs -> lhs only

        // for each rule (edges can only start from a rule, only they have rhs)
        for (TransformationRule<? extends IPatternMatch> rule : transformations) {

            // for each modelElement which is in the RHS of the rule

            RuleMetaData ruleMetaData = rule.getMetaData();
            ArrayList<EModelElement> rhsModelElements = new ArrayList<EModelElement>();
            rhsModelElements.addAll(ruleMetaData.getClasses());
            rhsModelElements.addAll(ruleMetaData.getReferences());
            rhsModelElements.addAll(ruleMetaData.getAttributes());

            for (EModelElement modelElement : rhsModelElements) {

                // check if rule has the element in rhs (and not only in lhs)
                ModelElementMetaData metaData = null;
                if (modelElement instanceof EClass) {
                    metaData = ruleMetaData.getMetaDataForClass((EClass) modelElement);
                } else if (modelElement instanceof EReference) {
                    metaData = ruleMetaData.getMetaDataForReference((EReference) modelElement);
                } else if (modelElement instanceof EAttribute) {
                    metaData = ruleMetaData.getMetaDataForAttribute((EAttribute) modelElement);
                }

                // If not, than continue
                if (metaData.getCreatesInRHS() + metaData.getDeletesInRHS() == 0) {
                    continue;
                }

                // foreach nodes' lhs

                for (PatternWithCardinality goal : goalPatterns) {
                    INode targetNode = dependencyGraph.getNodeByGoalPattern(goal);
                    createEdges(dependencyGraph, rule, modelElement, metaData, targetNode, goal, null, null);
                }

                for (PatternWithCardinality constraint : constraints) {
                    INode targetNode = dependencyGraph.getNodeByConstraint(constraint);
                    createEdges(dependencyGraph, rule, modelElement, metaData, targetNode, null, constraint, null);
                }

                for (TransformationRule<? extends IPatternMatch> targetRule : transformations) {
                    INode targetNode = dependencyGraph.getNodeByTransformationRule(targetRule);
                    createEdges(dependencyGraph, rule, modelElement, metaData, targetNode, null, null, targetRule);
                }

            }

        }

        dependencyGraph.save("dg");

        return dependencyGraph;
    }

    private void createEdges(IDependencyGraph dependencyGraph, TransformationRule<? extends IPatternMatch> rule,
            EModelElement modelElement, ModelElementMetaData metaData, INode targetNode, PatternWithCardinality goal,
            PatternWithCardinality constraint, TransformationRule<? extends IPatternMatch> targetRule) {

        // get target's classifiers
        Map<? extends EModelElement, Integer> modelElementLHS;
        Map<? extends EModelElement, Integer> modelElementLHSNAC;

        EdgeType.ClassType classType;

        ModelElementMetaData lhsReferenceMetaData = null;

        if (modelElement instanceof EClass) {
            classType = ClassType.CLASS;
            if (goal != null) {
                modelElementLHS = goal.getMetaData().getLHSNumbersForClasses();
                modelElementLHSNAC = goal.getMetaData().getLHSNACNumbersForClasses();
            } else if (constraint != null) {
                modelElementLHS = constraint.getMetaData().getLHSNumbersForClasses();
                modelElementLHSNAC = constraint.getMetaData().getLHSNACNumbersForClasses();
            } else if (targetRule != null) {
                modelElementLHS = targetRule.getMetaData().getLHSNumbersForClasses();
                modelElementLHSNAC = targetRule.getMetaData().getLHSNACNumbersForClasses();
            } else {
                throw new DSEException();
            }
        } else if (modelElement instanceof EReference) {
            classType = ClassType.REFERENCE;
            if (goal != null) {
                lhsReferenceMetaData = goal.getMetaData().getMetaDataForReference((EReference) modelElement);
                modelElementLHS = goal.getMetaData().getLHSNumbersForReferences();
                modelElementLHSNAC = goal.getMetaData().getLHSNACNumbersForReferences();
            } else if (constraint != null) {
                lhsReferenceMetaData = constraint.getMetaData().getMetaDataForReference((EReference) modelElement);
                modelElementLHS = constraint.getMetaData().getLHSNumbersForReferences();
                modelElementLHSNAC = constraint.getMetaData().getLHSNACNumbersForReferences();
            } else if (targetRule != null) {
                lhsReferenceMetaData = targetRule.getMetaData().getMetaDataForReference((EReference) modelElement);
                modelElementLHS = targetRule.getMetaData().getLHSNumbersForReferences();
                modelElementLHSNAC = targetRule.getMetaData().getLHSNACNumbersForReferences();
            } else {
                throw new DSEException();
            }
        } else if (modelElement instanceof EAttribute) {
            classType = ClassType.ATTRIBUTE;
            if (goal != null) {
                modelElementLHS = goal.getMetaData().getLHSNumbersForAttributes();
                modelElementLHSNAC = goal.getMetaData().getLHSNACNumbersForAttributes();
            } else if (constraint != null) {
                modelElementLHS = constraint.getMetaData().getLHSNumbersForAttributes();
                modelElementLHSNAC = constraint.getMetaData().getLHSNACNumbersForAttributes();
            } else if (targetRule != null) {
                modelElementLHS = targetRule.getMetaData().getLHSNumbersForAttributes();
                modelElementLHSNAC = targetRule.getMetaData().getLHSNACNumbersForAttributes();
            } else {
                throw new DSEException();
            }
        } else {
            throw new DSEException();
        }

        // if they have a classifier in common, create edges
        for (EModelElement c : modelElementLHS.keySet()) {
            if (c.equals(modelElement)) {

                // a reference can be a false positive
                if (modelElement instanceof EReference) {

                    boolean containmentFalsePozitive = true;
                    boolean referencedFalsePozitive = true;

                    EClass rhsContainmentEClass = metaData.getReferenceContainmentEClass();
                    EClass rhsReferencedEClass = metaData.getReferencedEClass();

                    EClass lhsContainmentEClass = lhsReferenceMetaData.getReferenceContainmentEClass();
                    EClass lhsReferencedEClass = lhsReferenceMetaData.getReferencedEClass();

                    // check if lhs classes are in rhs classes' supertypes

                    // part 1: containment class
                    if (rhsContainmentEClass.equals(lhsContainmentEClass)) {
                        containmentFalsePozitive = false;
                    } else {
                        for (EClass superType : rhsContainmentEClass.getEAllSuperTypes()) {
                            if (superType.equals(lhsContainmentEClass)) {
                                containmentFalsePozitive = false;
                                break;
                            }
                        }
                    }

                    // if the containment class of the lhs reference is not a supertype of the rhs containment class the
                    // it is false pozitive
                    if (containmentFalsePozitive) {
                        continue;
                    }

                    // part 2: referenced class
                    if (rhsReferencedEClass.equals(lhsReferencedEClass)) {
                        referencedFalsePozitive = false;
                    } else {
                        for (EClass superType : rhsReferencedEClass.getEAllSuperTypes()) {
                            if (superType.equals(lhsReferencedEClass)) {
                                referencedFalsePozitive = false;
                                break;
                            }
                        }
                    }

                    // if the referenced class of the lhs reference is not a supertype of the rhs referenced class the
                    // it is false pozitive
                    if (referencedFalsePozitive) {
                        continue;
                    }
                }

                // get appearances in the rules rhs and the other node's lhs
                int createsInRHS = metaData.getCreatesInRHS();
                int deletesInRHS = metaData.getDeletesInRHS();
                Integer lhsAppearance = modelElementLHS.get(modelElement);
                Integer lhsNacAppearance = modelElementLHSNAC.get(modelElement);

                // get node for creating the edge
                INode ruleNode = dependencyGraph.getNodeByTransformationRule(rule);

                // Create USES_ATTRIBUTE edges

                // if it is an attribute, probably the other 2 condition will be true
                if (classType == ClassType.ATTRIBUTE && createsInRHS + deletesInRHS > 0
                        && lhsAppearance + lhsNacAppearance > 0) {
                    dependencyGraph.addEdge(ruleNode, targetNode, EdgeType.USES_ATTRIBUTE, modelElement, 1);
                    // If it was an attribute then continue and don't created trigger and inhibit edges
                    continue;
                }

                // Create INHIBIT edges

                if (
                // if RHS creates object, which is used by the other's LHS in
                // negative
                (createsInRHS > 0 && lhsNacAppearance > 0) ||
                // if RHS deletes object, which is used by the other's LHS
                        (deletesInRHS > 0 && lhsAppearance > 0)) {

                    dependencyGraph.addEdge(ruleNode, targetNode, EdgeType.getInhibitFor(classType), modelElement,
                            createsInRHS - deletesInRHS);
                }

                // Create TRIGGER edges

                if (
                // if RHS creates object which is in the other's LHS
                (createsInRHS > 0 && lhsAppearance > 0) ||
                // if RHS deletes object which is in the other's LHS in negative
                        (deletesInRHS > 0 && lhsNacAppearance > 0)) {

                    dependencyGraph.addEdge(ruleNode, targetNode, EdgeType.getTriggerFor(classType), modelElement,
                            createsInRHS - deletesInRHS);
                }
            }
        }
    }
}
