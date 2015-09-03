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
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IConstraintFilter.AllowAllFilter;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IConstraintFilter.ExportedParameterFilter;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IVariableRenamer.HierarchicalName;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IVariableRenamer.SameName;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * This rewriter class holds the query flattening logic
 * 
 * @author Marton Bur
 * 
 */
public class PQueryFlattener extends PDisjunctionRewriter {

    private static final String FLATTENING_ERROR_MESSAGE = "Error occured while flattening";
	private IFlattenCallPredicate flattenCallPredicate;

	public PQueryFlattener(IFlattenCallPredicate flattenCallPredicate) {
		this.flattenCallPredicate = flattenCallPredicate;
	}
    
    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        PQuery query = disjunction.getQuery();

        // Check for recursion
        Set<PQuery> allReferredQueries = disjunction.getAllReferredQueries();
        for (PQuery referredQuery : allReferredQueries) {
            if (referredQuery.getAllReferredQueries().contains(referredQuery)) {
                throw new RewriterException("Recursive queries are not supported, can't flatten query named \"{1}\"", 
                        new String[] {query.getFullyQualifiedName()}, "Unsupported recursive query", query);
            }
        }

        try {
            return this.doFlatten(disjunction);
        } catch (Exception e) {
            throw new RewriterException(FLATTENING_ERROR_MESSAGE, new String[0], FLATTENING_ERROR_MESSAGE, query, e);
        }
    }

    /**
     * Flattens a given PQuery.
     * 
     * @param pQuery
     *            the query to flatten
     * @return a PDisjunction containing the flattened bodies
     * @deprecated Use {@link #rewrite(PDisjunction)} instead
     */
    public PDisjunction flatten(PQuery pQuery) {
        try {
            return rewrite(pQuery.getDisjunctBodies());
        } catch (RewriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
	 * This function holds the actual flattening logic for a PQuery
	 * 
	 * @param rootDisjunction
	 *            to be flattened
	 * @return the flattened bodies of the pQuery
	 */
	private PDisjunction doFlatten(PDisjunction rootDisjunction) {

		Map<PDisjunction, Set<PBody>> flatBodyMapping = Maps.newHashMap();

		Deque<Object> preStack = Queues.newArrayDeque();
		Deque<Object> postStack = Queues.newArrayDeque();

		List<PositivePatternCall> callsToFlatten = Lists.newArrayList();

		preStack.push(rootDisjunction);

		while (!preStack.isEmpty()) {

			Object item = preStack.pop();
			postStack.push(item);

			if (item instanceof PDisjunction) {

				PDisjunction disjunction = (PDisjunction) item;
				// First check if any of the bodies need flattening
				Set<PBody> flatBodies = Sets.newHashSet();
				if (isFlatteningNeeded(disjunction)) {
					// Push to schedule the contained bodies for processing
					for (PBody pBody : disjunction.getBodies()) {
						preStack.push(pBody);
					}
				} else {
					// No body needs flattening, simply copy them all
					for (PBody pBody : disjunction.getBodies()) {
						flatBodies.add(prepareFlatPBody(pBody));
					}
				}
				flatBodyMapping.put(disjunction, flatBodies);

			} else if (item instanceof PBody) {
				PBody pBody = (PBody) item;
				Set<PBody> containerSet = flatBodyMapping.get(pBody.getContainerDisjunction());

				if (isFlatteningNeeded(pBody)) {
					for (PConstraint pConstraint : pBody.getConstraints()) {
						if (pConstraint instanceof PositivePatternCall) {
							PositivePatternCall positivePatternCall = (PositivePatternCall) pConstraint;
							if (flattenCallPredicate.shouldFlatten(positivePatternCall)) {
								// If the above preconditions meet, the call should be flattened
								PDisjunction disjunction = positivePatternCall.getReferredQuery().getDisjunctBodies();
								preStack.push(disjunction);
								callsToFlatten.add(positivePatternCall);
							}
						}
					}
				} else {
					containerSet.add(prepareFlatPBody(pBody));
				}
			}
		}

		// Post order traversal
		while (!postStack.isEmpty()) {

			Object item = postStack.pop();

			// There are only actions left for non-leaf PBodies
			// Post order processing is needed in order to make sure that all called body is
			// flattened before the caller
			if (item instanceof PBody) {
				PBody pBody = (PBody) item;
				Set<PBody> containerSet = flatBodyMapping.get(pBody.getContainerDisjunction());

				if (isFlatteningNeeded(pBody)) {
					List<Set<PBody>> flattenedBodies = Lists.newArrayList();
					for (PConstraint pConstraint : pBody.getConstraints()) {

						if (pConstraint instanceof PositivePatternCall) {
							PositivePatternCall positivePatternCall = (PositivePatternCall) pConstraint;
							if (flattenCallPredicate.shouldFlatten(positivePatternCall)) {
								// If the above preconditions meet, do the flattening and return the disjoint bodies
								PDisjunction disjunction = positivePatternCall.getReferredQuery().getDisjunctBodies();

								flattenedBodies.add(flatBodyMapping.get(disjunction));
							}
						}
					}
					containerSet.addAll(createSetOfFlatPBodies(pBody, flattenedBodies, callsToFlatten));
				}

			}

		}

		return new PDisjunction(rootDisjunction.getQuery(), flatBodyMapping.get(rootDisjunction));
	}

    /**
     * Creates the flattened bodies based on the caller body and the called (and already flattened) disjunctions
     * 
     * @param pBody the body to flatten
     * @param flattenedDisjunctions the 
     * @param flattenedCalls
     * @return
     */
    private Set<PBody> createSetOfFlatPBodies(PBody pBody, List<Set<PBody>> flattenedBodies,
    		List<PositivePatternCall> flattenedCalls) {
        PQuery pQuery = pBody.getPattern();

        // The members of this set are lists containing bodies in conjunction
        // Ordering is not important within the list, only the cartesian product function requires a list
      
        Set<List<PBody>> conjunctBodySets = Sets.cartesianProduct(flattenedBodies);
        
        // The result set containing the merged conjuncted bodies
        Set<PBody> conjunctedBodies = Sets.<PBody> newHashSet();

        for (List<PBody> bodySet : conjunctBodySets) {
            PBodyCopier copier = createBodyCopier(pQuery, flattenedCalls, bodySet); 

            for (PBody calledBody : bodySet) {
                // Merge each called body
                copier.mergeBody(calledBody, new HierarchicalName(), new ExportedParameterFilter());
            }

            // Merge the caller's constraints to the conjunct body
            copier.mergeBody(pBody);

            PBody copiedBody = copier.getCopiedBody();
            copiedBody.setStatus(PQueryStatus.OK);
            conjunctedBodies.add(copiedBody);
        }

        return conjunctedBodies;
    }

    protected PBodyCopier createBodyCopier(PQuery query, List<PositivePatternCall> flattenedCalls, List<PBody> calledBodies) {
    	return new FlattenerCopier(query, flattenedCalls, calledBodies);
    }
    
    private PBody prepareFlatPBody(PBody pBody) {
        PBodyCopier copier = createBodyCopier(pBody.getPattern(), Lists.<PositivePatternCall> newArrayList(), Lists.<PBody> newArrayList());
        copier.mergeBody(pBody, new SameName(), new AllowAllFilter());
        // the copying of the body here is necessary for only one containing PDisjunction can be assigned to a PBody
        return copier.getCopiedBody();
    }

    private boolean isFlatteningNeeded(PDisjunction pDisjunction) {
		boolean needsFlattening = false;
		for (PBody pBody : pDisjunction.getBodies()) {
			needsFlattening |= isFlatteningNeeded(pBody);
		}
		return needsFlattening;
	}
    
    private boolean isFlatteningNeeded(PBody pBody) {
        // Check if the body contains positive pattern call AND if it should be flattened
        for (PConstraint pConstraint : pBody.getConstraints()) {
            if (pConstraint instanceof PositivePatternCall) {
                return flattenCallPredicate.shouldFlatten((PositivePatternCall) pConstraint);
            }
        }
        return false;
    }

}
