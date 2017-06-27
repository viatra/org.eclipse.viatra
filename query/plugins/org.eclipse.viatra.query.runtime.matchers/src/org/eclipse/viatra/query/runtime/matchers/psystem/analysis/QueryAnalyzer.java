/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.FunctionalDependencyHelper;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * Object responsible for computing and caching static query analysis results.
 * <p> Any client can instantiate this to statically analyze queries. 
 * Query backends should share an instance obtained via {@link IQueryBackendContext} to save resources.
 * <p> Precondition: all involved queries must be initialized.
 * @noinstantiate Considered unstable API; subject to change in future versions. 
 * Either use the analyzer provided by {@link IQueryBackendContext}, or anticipate 
 * potential future breakage when instantiating your own analyzer.
 * @author Gabor Bergmann
 * @since 1.5
 */
public final class QueryAnalyzer {
    
    private IQueryMetaContext metaContext;

    public QueryAnalyzer(IQueryMetaContext metaContext) {
        this.metaContext = metaContext;
    }

    // Functional dependencies
    
    /**
     * Maps query and strictness to functional dependencies 
     */
    private Table<PQuery, Boolean, Map<Set<Integer>, Set<Integer>>> functionalDependencyGuarantees =  
            HashBasedTable.create();

    /**
     * Functional dependency information, expressed on query parameters, that the match set of the query is guaranteed to respect. 
     * <p> The type dependencies shall be expressed on the <i>parameter index</i> integers, NOT the {@link PParameter} object.
     * @return a non-null map of functional dependencies on parameters that can be processed by {@link FunctionalDependencyHelper}
     * @param strict if true, only "hard" dependencies are taken into account that are strictly enforced by the model representation; 
     *  if false, user-provided soft dependencies (@FunctionalDependency) are included as well, that are anticipated but not guaranteed by the storage mechanism;
     *  use true if superfluous dependencies may taint the correctness of a computation, false if they would merely impact performance
     * @since 1.5
     */
    public Map<Set<Integer>, Set<Integer>> getProjectedFunctionalDependencies(PQuery query, boolean strict) {
        Map<Set<Integer>, Set<Integer>> dependencies = 
                functionalDependencyGuarantees.get(query, strict);
        
        if (dependencies == null) {
            dependencies = new HashMap<Set<Integer>, Set<Integer>>();
            functionalDependencyGuarantees.put(query, strict, dependencies);

            Set<PBody> bodies = query.getDisjunctBodies().getBodies();            
            if (bodies.size() == 1) { // no support for recursion or disjunction

                PBody body = bodies.iterator().next();
                
                // collect parameter variables
                Map<PVariable, Integer> parameters = new HashMap<PVariable, Integer>();
                for (ExportedParameter exportedParameter : body.getSymbolicParameters()) {
                    parameters.put(
                            exportedParameter.getParameterVariable(),
                            query.getParameters().indexOf(exportedParameter.getPatternParameter()));
                }
                
                // collect all internal dependencies
                Map<Set<PVariable>, Set<PVariable>> internalDependencies = 
                        getFunctionalDependencies(body.getConstraints(), strict);
                
                // project onto parameter variables
                Map<Set<PVariable>, Set<PVariable>> projectedDeps = 
                        FunctionalDependencyHelper.projectDependencies(internalDependencies, parameters.keySet());
                
                // translate into indices
                for (Entry<Set<PVariable>, Set<PVariable>> entry : projectedDeps.entrySet()) {
                    Set<Integer> left = new HashSet<Integer>();
                    Set<Integer> right = new HashSet<Integer>();
                    for (PVariable pVariable : entry.getKey()) {
                        left.add(parameters.get(pVariable));
                    }
                    for (PVariable pVariable : entry.getValue()) {
                        right.add(parameters.get(pVariable));
                    }
                    dependencies.put(left, right);
                }
                
            } else {
                // Disjunctive case, no dependencies are inferred
                // TODO: we can still salvage the intersection of dependencies IF 
                // - all bodies have disjoint match sets
                // - and we avoid recursion
            }
            
            // add annotation-based soft dependencies (regardless of number of bodies)
            if (!strict) {
                outer:
                    for (PAnnotation annotation : query.getAnnotationsByName("FunctionalDependency")) {
                        Set<Integer> lefts = new HashSet<Integer>();
                        Set<Integer> rights = new HashSet<Integer>();
                        
                        for (Object object : annotation.getAllValues("forEach")) {
                            ParameterReference parameter = (ParameterReference) object;
                            Integer position = query.getPositionOfParameter(parameter.getName());
                            if (position == null) continue outer;
                            lefts.add(position);
                        }
                        for (Object object : annotation.getAllValues("unique")) {
                            ParameterReference parameter = (ParameterReference) object;
                            Integer position = query.getPositionOfParameter(parameter.getName());
                            if (position == null) continue outer;
                            rights.add(position);
                        }
                        
                        FunctionalDependencyHelper.includeDependency(dependencies, lefts, rights);
                    }
            }
        }
        
        return dependencies;
    }

    /**
     * Functional dependency information, expressed on PVariables within a body, that the selected constraints imply. 
     * @return a non-null map of functional dependencies on PVariables that can be processed by {@link FunctionalDependencyHelper}
     * @param constraints the set of constraints whose consequences will be analyzed
     * @param strict if true, only "hard" dependencies are taken into account that are strictly enforced by the model representation; 
     *  if false, user-provided soft dependencies (@FunctionalDependency) are included as well, that are anticipated but not guaranteed by the storage mechanism;
     *  use true if superfluous dependencies may taint the correctness of a computation, false if they would merely impact performance
     * @since 1.5
     */
   public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies(Set<? extends PConstraint> constraints, boolean strict) {
        Map<Set<PVariable>, Set<PVariable>> accumulator = new HashMap<Set<PVariable>, Set<PVariable>>();
        for (PConstraint pConstraint : constraints){
            if (pConstraint instanceof PositivePatternCall) {
                // use query analysis results instead
                PositivePatternCall call = (PositivePatternCall) pConstraint;
                PQuery query = call.getSupplierKey();
                Map<Set<Integer>, Set<Integer>> paramDependencies = getProjectedFunctionalDependencies(query, strict);
                for (Entry<Set<Integer>, Set<Integer>> entry : paramDependencies.entrySet()) {
                    Set<PVariable> lefts = new HashSet<PVariable>();
                    Set<PVariable> rights = new HashSet<PVariable>();
                    
                    for (Integer index : entry.getKey()) {
                        lefts.add(call.getVariableInTuple(index));
                    }
                    for (Integer index : entry.getValue()) {
                        rights.add(call.getVariableInTuple(index));
                    }
                    
                    FunctionalDependencyHelper.includeDependency(accumulator, 
                            lefts, rights);
                }
            } else {
                // delegate to PConstraint
                FunctionalDependencyHelper.includeDependencies(accumulator, 
                        pConstraint.getFunctionalDependencies(metaContext));
            }
        }
        return accumulator;
    }


}
