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

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.IOperationCompiler;
import org.eclipse.incquery.runtime.rete.construction.IReteLayoutStrategy;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.SubPlan;
import org.eclipse.incquery.runtime.rete.construction.SubPlanProcessor;
import org.eclipse.incquery.runtime.rete.construction.helpers.BuildHelper;
import org.eclipse.incquery.runtime.rete.construction.helpers.LayoutHelper;
import org.eclipse.incquery.runtime.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.VariableDeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * Basic layout that builds a linear RETE net based on a heuristic ordering of constraints.
 * 
 * @author Gabor Bergmann
 * 
 */
public class BasicLinearLayout implements IReteLayoutStrategy {

	SubPlanProcessor planProcessor = new SubPlanProcessor();
	
    @Override
    public SubPlan layout(final PSystem pSystem, final IOperationCompiler<?, ?> compiler) throws RetePatternBuildException {
        Object pattern = pSystem.getPattern();
        IPatternMatcherContext context = pSystem.getContext();
        planProcessor.setCompiler(compiler);
        try {
            context.logDebug(String.format(
            		"%s: patternbody build started for %s",
            		getClass().getSimpleName(), 
            		context.printPattern(pattern)));

            // UNIFICATION AND WEAK INEQUALITY ELMINATION
            LayoutHelper.unifyVariablesAlongEqualities(pSystem);
            LayoutHelper.eliminateWeakInequalities(pSystem);

            // UNARY ELIMINATION WITH TYPE INFERENCE
            if (Options.calcImpliedTypes) {
                LayoutHelper.eliminateInferrableUnaryTypes(pSystem, context);
            }

            // PREVENTIVE CHECKS
            LayoutHelper.checkSanity(pSystem);

            // STARTING THE LINE
            SubPlan plan = compiler.buildStartingPlan(new Object[] {}, new Object[] {});

            // Set<ConstantValue> constants = pSystem.getConstraintsOfType(ConstantValue.class);
            // for (ConstantValue<PatternDescription, StubHandle> pConstraint : constants) {
            // S<StubHandle> sideStub = pConstraint.doCreateStub();
            // stub = BuildHelper.naturalJoin(buildable, stub, sideStub);
            // }

            Set<PConstraint> pQueue = CollectionsFactory.getSet(pSystem.getConstraints());//new HashSet<PConstraint>(pSystem.getConstraints()); // TreeSet<PConstraint>(new
                                                                                          // OrderingHeuristics());
            // pQueue.addAll(pSystem.getConstraintsOfType(EnumerablePConstraint.class));
            // pQueue.addAll(pSystem.getConstraintsOfType(DeferredPConstraint.class));
            // // omitted: symbolic & equality -- not anymore

            // MAIN LOOP
            while (!pQueue.isEmpty()) {
                PConstraint pConstraint = Collections.min(pQueue,
                        new OrderingHeuristics(plan)); // pQueue.iterator().next();
                pQueue.remove(pConstraint);

                if (pConstraint instanceof EnumerablePConstraint) {
                    EnumerablePConstraint enumerable = (EnumerablePConstraint) pConstraint;
                    SubPlan sidePlan = planProcessor.processEnumerableConstraint(enumerable);
                    plan = BuildHelper.naturalJoin(compiler, plan, sidePlan);
                } else {
                    DeferredPConstraint deferred = (DeferredPConstraint) pConstraint;
                    if (deferred.isReadyAt(plan)) {
                        plan = planProcessor.processDeferredConstraint(deferred, plan);
                    } else {
                        raiseForeverDeferredError(deferred, plan);
                    }
                }
            }

            // FINAL CHECK, whether all exported variables are present
            LayoutHelper.finalCheck(pSystem, plan);

            // // output
            // int paramNum = patternScaffold.gtPattern.getSymParameters().size();
            // int[] tI = new int[paramNum];
            // int tiW = stub.getVariablesTuple().getSize();
            // for (int i = 0; i < paramNum; i++) {
            // PatternVariable variable = patternScaffold.gtPattern.getSymParameters().get(i);
            // // for (Object o : variable.getElementInPattern()) // in all bodies
            // // {
            // PatternNodeBase pNode = pGraph.getPNode(variable);
            // // if (stub.calibrationIndex.containsKey(pNode))
            // tI[i] = stub.getVariablesIndex().get(pNode);
            // // }
            // }
            // TupleMask trim = new TupleMask(tI, tiW);
            // Stub<StubHandle> trimmer = buildable.buildTrimmer(stub, trim);
            // buildable.buildConnection(trimmer, collector);

            context.logDebug(String.format(
            		"%s: patternbody build concluded for %s",
            		getClass().getSimpleName(), 
            		context.printPattern(pattern)));

            return plan;

        } catch (RetePatternBuildException ex) {
            ex.setPatternDescription(pattern);
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
    private void raiseForeverDeferredError(DeferredPConstraint constraint, SubPlan plan) throws RetePatternBuildException {
    	if (constraint instanceof Equality) {
    		raiseForeverDeferredError((Equality)constraint, plan);
    	} else if (constraint instanceof ExportedParameter) {
    		raiseForeverDeferredError((ExportedParameter)constraint, plan);
    	} else if (constraint instanceof ExpressionEvaluation) {
    		raiseForeverDeferredError((ExpressionEvaluation)constraint, plan);
    	} else if (constraint instanceof VariableDeferredPConstraint) {
    		raiseForeverDeferredError((VariableDeferredPConstraint)constraint, plan);
    	}
    }
    
    private void raiseForeverDeferredError(Equality constraint, SubPlan plan) throws RetePatternBuildException {
    	String[] args = { constraint.getWho().toString(), constraint.getWithWhom().toString() };
        String msg = "Cannot express equality of variables {1} and {2} if neither of them is deducable.";
        String shortMsg = "Equality between undeducible variables.";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    private void raiseForeverDeferredError(ExportedParameter constraint, SubPlan plan) throws RetePatternBuildException {
    	String[] args = { constraint.getParameterName().toString() };
        String msg = "Pattern Graph Search terminated incompletely: "
                + "exported pattern variable {1} could not be determined based on the pattern constraints. "
                + "HINT: certain constructs (e.g. negative patterns or check expressions) cannot output symbolic parameters.";
        String shortMsg = "Could not deduce value of parameter";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    private void raiseForeverDeferredError(ExpressionEvaluation constraint, SubPlan plan) throws RetePatternBuildException {
        if (constraint.checkTypeSafety(plan) == null) {
            raiseForeverDeferredError((VariableDeferredPConstraint)constraint, plan);
        } else {
            String[] args = { toString(), constraint.checkTypeSafety(plan).toString() };
            String msg = "The checking of pattern constraint {1} cannot be deferred further, but variable {2} is still not type safe. "
                    + "HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
            String shortMsg = "Could not check all constraints due to undeducible type restrictions";
            throw new RetePatternBuildException(msg, args, shortMsg, null);
        }
    }
    private void raiseForeverDeferredError(VariableDeferredPConstraint constraint, SubPlan plan) throws RetePatternBuildException {
    	Set<PVariable> missing = CollectionsFactory.getSet(constraint.getDeferringVariables());//new HashSet<PVariable>(getDeferringVariables());
        missing.removeAll(plan.getVariablesIndex().keySet());
        String[] args = { toString(), Arrays.toString(missing.toArray()) };
        String msg = "The checking of pattern constraint {1} requires the values of variables {2}, but it cannot be deferred further. "
                + "HINT: the incremental matcher is not an equation solver, please make sure that all variable values are deducible.";
        String shortMsg = "Could not check all constraints due to undeducible variables";
        throw new RetePatternBuildException(msg, args, shortMsg, null);
    }
    
    
}
