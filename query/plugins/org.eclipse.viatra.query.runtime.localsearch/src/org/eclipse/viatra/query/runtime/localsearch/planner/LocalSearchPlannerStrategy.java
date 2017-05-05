/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.EvaluablePConstraint;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluablePredicateProvider;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.OrderingHeuristics;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlanFactory;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PApply;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PStart;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Marton Bur
 *
 * @deprecated Use the {@link org.eclipse.viatra.query.runtime.localsearch.planner.LocalSearchRuntimeBasedStrategy} and its belonging components instead.
 */
@Deprecated
public class LocalSearchPlannerStrategy implements IQueryPlannerStrategy {

    private IConstraintEvaluablePredicateProvider constraintPredicateProvider;

    public LocalSearchPlannerStrategy() {
        this(true);
    }

    public LocalSearchPlannerStrategy(final boolean allowInverseNavigation) {
        //TODO this method should be removed later
        constraintPredicateProvider = new IConstraintEvaluablePredicateProvider() {
            
            @Override
            public Predicate<PConstraint> getConstraint(SubPlan plan) {
                return new EvaluablePConstraint(plan, allowInverseNavigation);
            }
        };
    }
    
    public LocalSearchPlannerStrategy(IConstraintEvaluablePredicateProvider constraintEnabler) {
        this.constraintPredicateProvider = constraintEnabler;
    }
    
    private Set<PVariable> initialBoundVariables = Collections.emptySet();

    /**
     * Sets the initial adornment of the pattern.
     * 
     * @param initialBoundVariables set of PVariables that are bound
     */
    public void setBoundVariables(Set<PVariable>initialBoundVariables){
        this.initialBoundVariables = ImmutableSet.copyOf(initialBoundVariables);
    }
    
    /**
     * The implementation of a local search-based algorithm to create a search plan for a flattened (and normalized)
     * PBody
     */
    @Override
    public SubPlan plan(PBody pBody, Logger logger, IQueryMetaContext context) throws QueryProcessingException {

        // Create a starting plan
        SubPlanFactory subPlanFactory = new SubPlanFactory(pBody);
        
        // The constants are handled with corresponding cheap operations (of cost 0.0)

        // We assume that the adornment (now the bound variables) is previously set 
        SubPlan plan = subPlanFactory.createSubPlan(new PStart(initialBoundVariables));
        // Get all the constraints of the pBody
        Set<PConstraint> constraintSet = pBody.getConstraints();

        // Repeat constraint processing until all constraints are processed
        while (!constraintSet.isEmpty()) {
            // Select constraint according to the cost and heuristics
            PConstraint pConstraint = selectAndRemoveNextPConstraint(pBody, plan, constraintSet, context); // TODO rename this method to indicate removal of the constraint
            plan = subPlanFactory.createSubPlan(new PApply(pConstraint), plan);
        }

        return subPlanFactory.createSubPlan(new PProject(pBody.getSymbolicParameterVariables()), plan);
    }

    protected PConstraint selectFirstConstraint(PBody pBody, final SubPlan plan, Set<PConstraint> constraintSet, IQueryMetaContext context) {
        List<PVariable> parameterVariables = pBody.getSymbolicParameterVariables();
        for (PVariable pVariable : parameterVariables) {
            Set<PConstraint> referringPConstraints = pVariable.getReferringConstraints();
            for (PConstraint referringPConstraint : referringPConstraints) {
                // If the type constraint is unprocessed and is unary, then select it
                if (constraintSet.contains(referringPConstraint) && 
                        referringPConstraint instanceof TypeConstraint && 
                        referringPConstraint.getAffectedVariables().size()==1) {
                    return referringPConstraint;
                }
            }
        }
        return null;
    }
    
    protected PConstraint selectNextConstraint(PBody pBody, final SubPlan plan, Set<PConstraint> constraintSet, IQueryMetaContext context) {
        // TODO use better ordering heuristic based on the runtime context
        return Collections.min(Collections2.filter(constraintSet, constraintPredicateProvider.getConstraint(plan)),
                new OrderingHeuristics(plan,context));
    }
    
    /**
     * Selects the next PConstraint that is to be placed in the search plan a.k.a. ordered list of PConstraints
     * @param pBody the PBody, for which the plan is being created
     * @param plan the part of the plan which is ready so far
     * @param constraintSet the set of possibly chosen constraints 
     * @param context the meta context of the search
     * @return the selected PConstraint that is removed from the parameter <code>constraintSet</code>
     */
    private PConstraint selectAndRemoveNextPConstraint(PBody pBody, final SubPlan plan, Set<PConstraint> constraintSet, IQueryMetaContext context) {

        PConstraint pConstraint = null;
 
        // Strategy: begin with TypeUnary constraints from parameters, if able
        // TODO consider adornment here as well
        if(plan.getOperation() instanceof PStart){
            pConstraint = selectFirstConstraint(pBody, plan, constraintSet, context);
        }
        // If no such constraint left, go with the ordering heuristic for the rest of the constraints
        if (pConstraint == null) {
            pConstraint = selectNextConstraint(pBody, plan, constraintSet, context);
        }
        // Remove it from the to-be-processed constraints list
        constraintSet.remove(pConstraint);
        return pConstraint;
    }

    protected EClassifier extractClassifierLiteral(String packageUriAndClassifierName) {
        int lastSlashPosition = packageUriAndClassifierName.lastIndexOf('/');
        int scopingPosition = packageUriAndClassifierName.lastIndexOf("::");
        String packageUri = packageUriAndClassifierName.substring(scopingPosition + 2, lastSlashPosition);
        String classifierName = packageUriAndClassifierName.substring(lastSlashPosition + 1);

        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
        Preconditions.checkState(ePackage != null, "EPackage %s not found in EPackage Registry.", packageUri);
        EClassifier literal = ePackage.getEClassifier(classifierName);
        Preconditions.checkState(literal != null, "Classifier %s not found in EPackage %s", classifierName, packageUri);
        return literal;
    }

}
