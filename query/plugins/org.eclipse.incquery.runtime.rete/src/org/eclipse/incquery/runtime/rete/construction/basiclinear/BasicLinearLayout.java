/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.basiclinear;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.SubPlanFactory;
import org.eclipse.incquery.runtime.matchers.planning.helpers.BuildHelper;
import org.eclipse.incquery.runtime.matchers.planning.operations.PApply;
import org.eclipse.incquery.runtime.matchers.planning.operations.PProject;
import org.eclipse.incquery.runtime.matchers.planning.operations.PStart;
import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.VariableDeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.util.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;

/**
 * Basic layout that builds a linear RETE net based on a heuristic ordering of constraints.
 * 
 * @author Gabor Bergmann
 * 
 */
public class BasicLinearLayout implements IQueryPlannerStrategy {

	//SubPlanProcessor planProcessor = new SubPlanProcessor();
	
    @Override
    public SubPlan plan(final PBody pSystem, Logger logger, IQueryMetaContext context) throws QueryProcessingException {
    	SubPlanFactory planFactory = new SubPlanFactory(pSystem);
        PQuery query = pSystem.getPattern();
        //planProcessor.setCompiler(compiler);
        try {
            logger.debug(String.format(
            		"%s: patternbody build started for %s",
            		getClass().getSimpleName(), 
            		query.getFullyQualifiedName()));

            // STARTING THE LINE
            SubPlan plan = planFactory.createSubPlan(new PStart());

            Set<PConstraint> pQueue = CollectionsFactory.getSet(pSystem.getConstraints());//new HashSet<PConstraint>(pSystem.getConstraints()); // TreeSet<PConstraint>(new
                                                                                          // OrderingHeuristics());

            // MAIN LOOP
            while (!pQueue.isEmpty()) {
                PConstraint pConstraint = Collections.min(pQueue,
                        new OrderingHeuristics(plan, context)); // pQueue.iterator().next();
                pQueue.remove(pConstraint);

                // if we have no better option than an unready deferred constraint, raise error
				if (pConstraint instanceof DeferredPConstraint) {
					final DeferredPConstraint deferred = (DeferredPConstraint) pConstraint;
                    if (!deferred.isReadyAt(plan, context)) {
                        raiseForeverDeferredError(deferred, plan, context);
                    }
                }
				// TODO integrate the check above in SubPlan / POperation??
				
				// replace incumbent plan with its child
				plan = planFactory.createSubPlan(new PApply(pConstraint), plan);              		
            }

            // PROJECT TO PARAMETERS
            SubPlan finalPlan = planFactory.createSubPlan(new PProject(pSystem.getSymbolicParameterVariables()), plan);
            
            // FINAL CHECK, whether all exported variables are present + all constraint satisfied
            BuildHelper.finalCheck(pSystem, finalPlan, context);
			// TODO integrate the check above in SubPlan / POperation 
            
            logger.debug(String.format(
            		"%s: patternbody query plan concluded for %s as: %s",
            		getClass().getSimpleName(), 
            		query.getFullyQualifiedName(),
            		finalPlan.toLongString()));

            return finalPlan;

        } catch (RetePatternBuildException ex) {
            ex.setPatternDescription(query);
            throw ex;
        }
    }

    /**
     * Called when the constraint is not ready, but cannot be deferred further.
     * 
     * @param plan
     * @throws RetePatternBuildException
     *             to indicate the error in detail.
     */
    private void raiseForeverDeferredError(DeferredPConstraint constraint, SubPlan plan, IQueryMetaContext context) throws RetePatternBuildException {
    	if (constraint instanceof Equality) {
    		raiseForeverDeferredError((Equality)constraint, plan, context);
    	} else if (constraint instanceof ExportedParameter) {
    		raiseForeverDeferredError((ExportedParameter)constraint, plan, context);
    	} else if (constraint instanceof ExpressionEvaluation) {
    		raiseForeverDeferredError((ExpressionEvaluation)constraint, plan, context);
    	} else if (constraint instanceof VariableDeferredPConstraint) {
    		raiseForeverDeferredError(constraint, plan, context);
    	}
    }
    
    private void raiseForeverDeferredError(Equality constraint, SubPlan plan, IQueryMetaContext context) throws RetePatternBuildException {
    	String[] args = { constraint.getWho().toString(), constraint.getWithWhom().toString() };
        String msg = "Cannot express equality of variables {1} and {2} if neither of them is deducable.";
        String shortMsg = "Equality between undeducible variables.";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    private void raiseForeverDeferredError(ExportedParameter constraint, SubPlan plan, IQueryMetaContext context) throws RetePatternBuildException {
    	String[] args = { constraint.getParameterName().toString() };
        String msg = "Pattern Graph Search terminated incompletely: "
                + "exported pattern variable {1} could not be determined based on the pattern constraints. "
                + "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
        String shortMsg = "Could not deduce value of parameter";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    private void raiseForeverDeferredError(ExpressionEvaluation constraint, SubPlan plan, IQueryMetaContext context) throws RetePatternBuildException {
        if (constraint.checkTypeSafety(plan, context) == null) {
            raiseForeverDeferredError(constraint, plan);
        } else {
            String[] args = { toString(), constraint.checkTypeSafety(plan, context).toString() };
            String msg = "The checking of pattern constraint {1} cannot be deferred further, but variable {2} is still not type safe. "
                    + "HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
            String shortMsg = "Could not check all constraints due to undeducible type restrictions";
            throw new RetePatternBuildException(msg, args, shortMsg, null);
        }
    }
    private void raiseForeverDeferredError(VariableDeferredPConstraint constraint, SubPlan plan) throws RetePatternBuildException {
    	Set<PVariable> missing = CollectionsFactory.getSet(constraint.getDeferringVariables());//new HashSet<PVariable>(getDeferringVariables());
        missing.removeAll(plan.getVisibleVariables());
        String[] args = { toString(), Arrays.toString(missing.toArray()) };
        String msg = "The checking of pattern constraint {1} requires the values of variables {2}, but it cannot be deferred further. "
                + "HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
        String shortMsg = "Could not check all constraints due to undeducible variables";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    
    
}
