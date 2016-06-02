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
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IConstraintFilter.AllowAllFilter;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IConstraintFilter.ExportedParameterFilter;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IVariableRenamer.HierarchicalName;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IVariableRenamer.SameName;

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

    /**
     * Utility function to produce the permutation of every possible mapping of values.
     * 
     * @param values
     * @return
     */
    private static <K,V> Set<Map<K, V>> permutation(Map<K, Set<V>> values){    
        // An ordering of keys is defined here which will help restoring the appropriate values after the execution of the cartesian product
        List<K> keyList = Lists.newArrayList(values.keySet());
        
        // Produce list of value sets with the ordering defined by keyList
        List<Set<V>> valuesList = new ArrayList<Set<V>>(keyList.size());
        for(K key : keyList){
            valuesList.add(values.get(key));
        }
        
        // Cartesian product will obey ordering of the list
        Set<List<V>> valueMappings = Sets.cartesianProduct(valuesList);
        
        // Build result
        Set<Map<K, V>> result = Sets.newLinkedHashSet();
        for(List<V> valueList : valueMappings){
            Map<K, V> map = Maps.newHashMap();
            for(int i=0;i<keyList.size();i++){
                map.put(keyList.get(i), valueList.get(i));
            }
            result.add(map);
        }
        
        return result;
    }
    
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
					Map<PositivePatternCall, Set<PBody>> flattenedBodies = Maps.newHashMap();
					for (PConstraint pConstraint : pBody.getConstraints()) {

						if (pConstraint instanceof PositivePatternCall) {
							PositivePatternCall positivePatternCall = (PositivePatternCall) pConstraint;
							if (flattenCallPredicate.shouldFlatten(positivePatternCall)) {
								// If the above preconditions meet, do the flattening and return the disjoint bodies
								PDisjunction disjunction = positivePatternCall.getReferredQuery().getDisjunctBodies();

								flattenedBodies.put(positivePatternCall, flatBodyMapping.get(disjunction));
							}
						}
					}
					containerSet.addAll(createSetOfFlatPBodies(pBody, flattenedBodies));
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
    private Set<PBody> createSetOfFlatPBodies(PBody pBody, Map<PositivePatternCall, Set<PBody>> flattenedCalls) {
        PQuery pQuery = pBody.getPattern();

        Set<Map<PositivePatternCall, PBody>> conjunctedCalls = permutation(flattenedCalls);
        
        // The result set containing the merged conjuncted bodies
        Set<PBody> conjunctedBodies = Sets.<PBody> newHashSet();

        for (Map<PositivePatternCall, PBody> calledBodies : conjunctedCalls) {
            FlattenerCopier copier = createBodyCopier(pQuery, calledBodies); 

            int i = 0;
            HierarchicalName hierarchicalNamingTool = new HierarchicalName();
            for (PositivePatternCall patternCall : calledBodies.keySet()) {
                // Merge each called body
                hierarchicalNamingTool.setCallCount(i++);
                copier.mergeBody(patternCall, hierarchicalNamingTool, new ExportedParameterFilter());
            }

            // Merge the caller's constraints to the conjunct body
            copier.mergeBody(pBody);

            PBody copiedBody = copier.getCopiedBody();
            copiedBody.setStatus(PQueryStatus.OK);
            conjunctedBodies.add(copiedBody);
        }

        return conjunctedBodies;
    }

    private FlattenerCopier createBodyCopier(PQuery query, Map<PositivePatternCall, PBody> calledBodies) {
    	return new FlattenerCopier(query, calledBodies);
    }
    
    private PBody prepareFlatPBody(PBody pBody) {
        PBodyCopier copier = createBodyCopier(pBody.getPattern(), Collections.<PositivePatternCall, PBody>emptyMap());
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
