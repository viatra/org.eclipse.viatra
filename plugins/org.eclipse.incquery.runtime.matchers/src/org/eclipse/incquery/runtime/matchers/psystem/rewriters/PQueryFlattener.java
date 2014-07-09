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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class holds the flattening logic
 * 
 * @author Marton Bur
 * 
 */
public class PQueryFlattener extends PDisjunctionRewriter {

    // TODO handle recursive pattern calls - check for backward edges in the call graph
    
    private static final String FLATTENING_ERROR_MESSAGE = "Error occured while flattening";
    
    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        PQuery query = disjunction.getQuery();
        try {
            return this.flatten(query);
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
     * @throws Exception
     */
    public PDisjunction flatten(PQuery pQuery) throws Exception {
        return doFlatten(pQuery);
    }

    /**
     * This function holds the actual flattening logic for a PQuery
     * 
     * @param pQuery
     *            to be flattened
     * @return the flattened bodies of the pQuery
     * @throws Exception
     */
    private PDisjunction doFlatten(PQuery pQuery) throws Exception {
        Set<PBody> bodies = pQuery.getDisjunctBodies().getBodies();
        // This stores the flattened bodies; they are disjoint
        Set<PBody> flattenedBodies = Sets.<PBody>newHashSet();
        for (PBody pBody : bodies) {
            // OR connection between the bodies
            flattenedBodies.addAll(doFlatten(pBody));
        }
        return new PDisjunction(pQuery, flattenedBodies);
    }

    
    /**
     * This function holds the actual flattening logic for a PBody
     * 
     * @param pBody
     *            to be flattened
     * @return the flattened equivalent of the given pBody
     * @throws Exception
     */
    private Set<PBody> doFlatten(PBody pBody) throws Exception {

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
                if (shouldFlatten(positivePatternCall)) {
                    // If the above preconditions meet, do the flattening and return the disjoint bodies
                    PQuery referredQuery = positivePatternCall.getReferredQuery();
                    PDisjunction flattenedDisjunction = doFlatten(referredQuery);
                    flattenedDisjunctions.add(flattenedDisjunction);
                    flattenedCalls.add(positivePatternCall);
                }
            }
        }

        return createFlatPDisjunction(pBody,flattenedDisjunctions,flattenedCalls);

    }

    /**
     * @param pBody
     * @param flattenedDisjunctions
     * @param flattenedCalls 
     * @return
     */
    private Set<PBody> createFlatPDisjunction(PBody pBody, List<PDisjunction> flattenedDisjunctions, List<PositivePatternCall> flattenedCalls) {
        PQuery pQuery = pBody.getPattern();
        
        // The members of this set are sets containing bodies in disjunction
        Set<List<PBody>> conjunctBodySets = combineBodies(flattenedDisjunctions);
        
        // The result set containing the merged conjuncted bodies
        Set<PBody> conjunctedBodies = Sets.<PBody> newHashSet();
        
        for (List<PBody> bodySet : conjunctBodySets) {
            FlattenerCopier copier = new FlattenerCopier(pQuery, flattenedCalls, bodySet);

            for (PBody calledBody : bodySet) {
                // Copy each called body
                copyBody(calledBody, copier, new HierarchicalName(), new ExportedParameterFilter());                
            }
            
            // Copy the caller body
            copyBody(pBody, copier, new SameName());
            
            PBody copiedBody = copier.getCopiedBody();
            copiedBody.setStatus(PQueryStatus.OK);
            conjunctedBodies.add(copiedBody);
        }
        
        // Create a new (flattened) PDisjunction referring to the corresponding query and return it
        return conjunctedBodies;
    }

    private Set<PBody> prepareFlatPBody(PBody pBody) {
        Set<PBody> bodySet = Sets.newHashSet();
        // Copy here with hierarchical variable renaming
        FlattenerCopier flattenerCopier = copyBody(pBody, new HierarchicalName());
        bodySet.add(flattenerCopier.getCopiedBody());
        return bodySet;
    }

    private boolean isFlatteningNeeded(Set<PConstraint> constraints) {
        // Check if the body contains positive pattern call AND if it should be flattened
        for (PConstraint pConstraint : constraints) {
            if (pConstraint instanceof PositivePatternCall) {
                 if(shouldFlatten((PositivePatternCall) pConstraint))
                     return true;
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

        ArrayList<Set<PBody>> setsToCombine = Lists.newArrayList();
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
    
    /**
     * Helper function to copy a PBody object. Creates a new copier.
     * 
     * @param pBody
     * @param namingTool
     * @return
     */
    private FlattenerCopier copyBody(PBody pBody, INamingTool namingTool) {
        FlattenerCopier copier = new FlattenerCopier(pBody.getPattern(), Lists.<PositivePatternCall> newArrayList(), Lists.<PBody> newArrayList());
        copyBody(pBody, copier, namingTool);
        return copier;
    }

    /**
     * Helper function to copy a PBody object. Uses a given copier.
     * 
     * @param pBody
     * @param copier
     * @param namingTool
     * @return
     */
    private void copyBody(PBody pBody, FlattenerCopier copier, INamingTool namingTool) {
        copyBody(pBody, copier, namingTool, new AllowAllFilter());
    }

    /**
     * Helper function to copy a PBody object. Uses a given copier and a filter to copy only specific constraints.
     * 
     * @param pBody
     * @param copier
     * @param namingTool
     * @return
     */
    private void copyBody(PBody pBody, FlattenerCopier copier, INamingTool namingTool, IConstraintFilter filter) {
        
        // Copy variables
        Set<PVariable> allVariables = pBody.getAllVariables();
        for (PVariable pVariable : allVariables) {
            copier.copyVariable(pVariable, namingTool.createVariableName(pVariable, pBody.getPattern()));
        }
        
        // Copy constraints which are not filtered
        Set<PConstraint> constraints = pBody.getConstraints();
        for (PConstraint pConstraint : constraints) {
            if(!filter.filter(pConstraint)){
                copier.copyConstraint(pConstraint);
            }
        }
    }

    /**
     * Helper interface to exclude constraints from PBody copy processes
     * 
     * @author Marton Bur
     * 
     */
    private interface IConstraintFilter {
        /**
         * Returns true, if the given constraint should be filtered (thus should not be copied)
         * 
         * @param constraint to check
         * @return true, if the constraint should be filtered
         */
        boolean filter(PConstraint constraint);
    }
    
    private class ExportedParameterFilter implements IConstraintFilter{

        @Override
        public boolean filter(PConstraint constraint) {
            return constraint instanceof ExportedParameter;
        }
        
    }
    
    private class AllowAllFilter implements IConstraintFilter{

        @Override
        public boolean filter(PConstraint constraint) {
            // Nothing is filtered
            return false;
        }
        
    }
    
    /**
     * Helper interface to ease the naming of the new variables during flattening
     * 
     * @author Marton Bur
     * 
     */
    private interface INamingTool {
        /**
         * Creates a variable name based on a given variable and a given query. It only creates a String, doesn't set
         * anything.
         * 
         * @param pVariable
         * @param query
         * @return the new variable name as a String
         */
        String createVariableName(PVariable pVariable, PQuery query);
    }

    private class SameName implements INamingTool {
        @Override
        public String createVariableName(PVariable pVariable, PQuery query) {
            return pVariable.getName();
        }
    }

    private class HierarchicalName implements INamingTool {
        @Override
        public String createVariableName(PVariable pVariable, PQuery query) {
            return getPQueryName(query) + "_" + pVariable.getName();
        }
    }

    /**
     * Helper function to get the name of a query without qualifier
     * 
     * @param query
     * @return the name of the query
     */
    private String getPQueryName(PQuery query) {
        String fullyQualifiedName = query.getFullyQualifiedName();
        int beginIndex = fullyQualifiedName.lastIndexOf(".") + 1;
        return fullyQualifiedName.substring(beginIndex);
    }

    /**
     * Decides whether the pattern should be flattened or not.
     * 
     * 
     * @param positivePatternCall
     *            the pattern call
     * @return true if the call should be flattened
     */
    private boolean shouldFlatten(PositivePatternCall positivePatternCall) {
        boolean shouldFlatten = true;
        /* TODO implement logic here */
        return shouldFlatten;
    }

}
