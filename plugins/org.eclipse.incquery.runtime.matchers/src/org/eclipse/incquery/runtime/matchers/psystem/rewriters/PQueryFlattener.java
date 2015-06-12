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

import java.util.List;
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
        if (disjunction.getAllReferredQueries().contains(query)) {
            throw new RewriterException("Recursive queries are not supported, can't flatten query named \"{1}\"", 
                    new String[] {query.getFullyQualifiedName()}, "Unsupported recursive query", query);
        }

        try {
            return this.doFlatten(disjunction);
        } catch (Exception e) {
            throw new RewriterException(FLATTENING_ERROR_MESSAGE, null, FLATTENING_ERROR_MESSAGE, query, e);
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
     * @param disjunction
     *            to be flattened
     * @return the flattened bodies of the pQuery
     * @throws Exception
     */
    private PDisjunction doFlatten(PDisjunction disjunction) {
        Set<PBody> bodies = disjunction.getBodies();
        // This stores the flattened bodies; they are disjoint
        Set<PBody> flattenedBodies = Sets.<PBody> newHashSet();
        for (PBody pBody : bodies) {
            // OR connection between the bodies
            flattenedBodies.addAll(doFlatten(pBody));
        }
        return new PDisjunction(disjunction.getQuery(), flattenedBodies);
    }

    /**
     * This function holds the actual flattening logic for a PBody
     * 
     * @param pBody
     *            to be flattened
     * @return the flattened equivalent of the given pBody
     * @throws Exception
     */
    private Set<PBody> doFlatten(PBody pBody) {

        Set<PConstraint> constraints = pBody.getConstraints();

        // If the received pBody should not be flattened, return it alone.
        if (!isFlatteningNeeded(constraints)) {
            return prepareFlatPBody(pBody);
        }

        // The calls that are flattened
        List<PositivePatternCall> flattenedCalls = Lists.newArrayList();

        // This point we know the body needs flattening
        // Flatten each positive pattern call where shouldFlatten() returns true
        List<PDisjunction> flattenedDisjunctions = Lists.<PDisjunction> newArrayList();
        for (PConstraint pConstraint : constraints) {
            if (pConstraint instanceof PositivePatternCall) {
                PositivePatternCall positivePatternCall = (PositivePatternCall) pConstraint;
                if (flattenCallPredicate.shouldFlatten(positivePatternCall)) {
                    // If the above preconditions meet, do the flattening and return the disjoint bodies
                    PQuery referredQuery = positivePatternCall.getReferredQuery();
                    PDisjunction flattenedDisjunction = doFlatten(referredQuery.getDisjunctBodies());
                    flattenedDisjunctions.add(flattenedDisjunction);
                    flattenedCalls.add(positivePatternCall);
                }
            }
        }

        return createFlatPDisjunction(pBody, flattenedDisjunctions, flattenedCalls);

    }

    private Set<PBody> createFlatPDisjunction(PBody pBody, List<PDisjunction> flattenedDisjunctions,
            List<PositivePatternCall> flattenedCalls) {
        PQuery pQuery = pBody.getPattern();

        // The members of this set are sets containing bodies in disjunction
        Set<List<PBody>> conjunctBodySets = combineBodies(flattenedDisjunctions);

        // The result set containing the merged conjuncted bodies
        Set<PBody> conjunctedBodies = Sets.<PBody> newHashSet();

        for (List<PBody> bodySet : conjunctBodySets) {
            PBodyCopier copier = createBodyCopier(pQuery, flattenedCalls, bodySet); 

            for (PBody calledBody : bodySet) {
                // Copy each called body
                copier.mergeBody(calledBody, new HierarchicalName(), new ExportedParameterFilter());
            }

            // Copy the caller body
            copier.mergeBody(pBody);

            PBody copiedBody = copier.getCopiedBody();
            copiedBody.setStatus(PQueryStatus.OK);
            conjunctedBodies.add(copiedBody);
        }

        // Create a new (flattened) PDisjunction referring to the corresponding query and return it
        return conjunctedBodies;
    }

    protected PBodyCopier createBodyCopier(PQuery query, List<PositivePatternCall> flattenedCalls, List<PBody> calledBodies) {
    	return new FlattenerCopier(query, flattenedCalls, calledBodies);
    }
    
    private Set<PBody> prepareFlatPBody(PBody pBody) {
        Set<PBody> bodySet = Sets.newHashSet();
        PBodyCopier copier = createBodyCopier(pBody.getPattern(), Lists.<PositivePatternCall> newArrayList(), Lists.<PBody> newArrayList());
        copier.mergeBody(pBody, new SameName(), new AllowAllFilter());
        // the copying of the body here is necessary for only one containing PDisjunction can be assigned to a PBody
        PBodyCopier flattenerCopier = copier;
        bodySet.add(flattenerCopier.getCopiedBody());
        return bodySet;
    }

    private boolean isFlatteningNeeded(Set<PConstraint> constraints) {
        // Check if the body contains positive pattern call AND if it should be flattened
        for (PConstraint pConstraint : constraints) {
            if (pConstraint instanceof PositivePatternCall) {
                return flattenCallPredicate.shouldFlatten((PositivePatternCall) pConstraint);
            }
        }
        return false;
    }

    /**
     * Combines the elements of the sets together. Puts all newly created bodies under the parent query.
     * 
     * @param pDisjunctions
     *            the collection of sets; all possible full matchings are created and merged
     * @return
     */
    private Set<List<PBody>> combineBodies(List<PDisjunction> pDisjunctions) {
        // Note: Sets.cartesianProduct(sets) would also be useful to create matchings

        List<Set<PBody>> setsToCombine = Lists.newArrayList();
        Set<List<PBody>> result = Sets.<List<PBody>> newHashSet();

        if (pDisjunctions.size() == 0) {
            // Do nothing (error handling should happen here?)
        } else {
            for (PDisjunction pDisjunction : pDisjunctions) {
                setsToCombine.add(pDisjunction.getBodies());
            }
            // Create matchings between the bodies
            result = Sets.cartesianProduct(setsToCombine);
        }
        return result;
    }

}
