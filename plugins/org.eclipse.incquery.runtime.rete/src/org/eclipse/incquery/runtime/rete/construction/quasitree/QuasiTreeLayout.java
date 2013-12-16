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

package org.eclipse.incquery.runtime.rete.construction.quasitree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.SubPlanProcessor;
import org.eclipse.incquery.runtime.matchers.planning.helpers.BuildHelper;
import org.eclipse.incquery.runtime.matchers.planning.helpers.LayoutHelper;
import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.util.Options;

/**
 * Layout ideas: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=398763
 * 
 * @author Gabor Bergmann
 * 
 */
public class QuasiTreeLayout implements IQueryPlannerStrategy {

    @Override
    public SubPlan layout(PSystem pSystem, IOperationCompiler<?, ?> compiler)
            throws QueryPlannerException {
        return new Scaffold(pSystem, compiler).run();
    }

	public class Scaffold {
        PSystem pSystem;
        Object pattern;
        IPatternMatcherContext context;
        IOperationCompiler<?, ?> buildable;
        SubPlanProcessor planProcessor = new SubPlanProcessor();

        Set<DeferredPConstraint> deferredConstraints = null;
        Set<EnumerablePConstraint> enumerableConstraints = null;
        Set<SubPlan> forefront = new LinkedHashSet<SubPlan>();

        Scaffold(PSystem pSystem, IOperationCompiler<?, ?> compiler) {
            this.pSystem = pSystem;
            pattern = pSystem.getPattern();
            context = pSystem.getContext();
            buildable = compiler;
            planProcessor.setCompiler(compiler);
        }

        /**
         * @return
         */
        public SubPlan run() throws QueryPlannerException {
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

                // PROCESS CONSTRAINTS
                deferredConstraints = pSystem.getConstraintsOfType(DeferredPConstraint.class);
                enumerableConstraints = pSystem.getConstraintsOfType(EnumerablePConstraint.class);
                for (EnumerablePConstraint enumerable : enumerableConstraints) {
                    SubPlan plan = planProcessor.processEnumerableConstraint(enumerable);
                    admitSubPlan(plan);
                }
                if (enumerableConstraints.isEmpty()) { // EXTREME CASE
                    SubPlan plan = buildable.buildStartingPlan(new Object[] {}, new Object[] {});
                    admitSubPlan(plan);
                }

                // JOIN FOREFRONT PLANS WHILE POSSIBLE
                while (forefront.size() > 1) {
                    // TODO QUASI-TREE TRIVIAL JOINS?

                    List<JoinCandidate> candidates = generateJoinCandidates();
                    JoinOrderingHeuristics ordering = new JoinOrderingHeuristics();
                    JoinCandidate selectedJoin = Collections.min(candidates, ordering);
                    doJoin(selectedJoin.getPrimary(), selectedJoin.getSecondary());
                }

                // FINAL CHECK, whether all exported variables are present
                assert (forefront.size() == 1);
                SubPlan finalPlan = forefront.iterator().next();
                LayoutHelper.finalCheck(pSystem, finalPlan);

                context.logDebug(String.format(
                		"%s: patternbody build concluded for %s",
                		getClass().getSimpleName(), 
                		context.printPattern(pattern)));
               return finalPlan;
            } catch (RetePatternBuildException ex) {
                ex.setPatternDescription(pattern);
                throw ex;
            }
        }

        public List<JoinCandidate> generateJoinCandidates() {
            List<JoinCandidate> candidates = new ArrayList<JoinCandidate>();
            int bIndex = 0;
            for (SubPlan b : forefront) {
                int aIndex = 0;
                for (SubPlan a : forefront) {
                    if (aIndex++ >= bIndex)
                        break;
                    candidates.add(new JoinCandidate(a, b));
                }
                bIndex++;
            }
            return candidates;
        }

        private void admitSubPlan(SubPlan plan) throws QueryPlannerException {
        	// are there any variables that will not be needed anymore and are worth trimming?
        	// (check only if there are unenforced enumerables, so that there are still upcoming joins)
        	if (Options.planTrimOption != Options.PlanTrimOption.OFF &&
        			!plan.getAllEnforcedConstraints().containsAll(enumerableConstraints)) {
        		final SubPlan trimmed = BuildHelper.trimUnneccessaryVariables(buildable, plan, true);
				plan = trimmed;
        	}        	
        	// are there any checkable constraints?
            for (DeferredPConstraint deferred : deferredConstraints) {
                if (!plan.getAllEnforcedConstraints().contains(deferred)) {
                    if (deferred.isReadyAt(plan)) {
                        admitSubPlan(planProcessor.processDeferredConstraint(deferred, plan));
                        return;
                    }
                }
            }
            // if no checkable constraints and no unused variables
            forefront.add(plan);
        }

        private void doJoin(SubPlan primaryPlan, SubPlan secondaryPlan)
                throws QueryPlannerException {
            SubPlan joinedPlan = BuildHelper.naturalJoin(buildable, primaryPlan, secondaryPlan);
            forefront.remove(primaryPlan);
            forefront.remove(secondaryPlan);
            admitSubPlan(joinedPlan);
        }

    }

}
