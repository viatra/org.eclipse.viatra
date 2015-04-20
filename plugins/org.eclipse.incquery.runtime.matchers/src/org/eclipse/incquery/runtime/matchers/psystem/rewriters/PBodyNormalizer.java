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

package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.planning.helpers.TypeHelper;
import org.eclipse.incquery.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

import com.google.common.collect.Sets;

/**
 * A disjunction rewriter for creating a normalized form of specification, unifying variables and running basic sanity
 * checks. This rewriter does not copy but modifies directly the original specification, requiring a mutable
 * disjunction.
 * 
 * @author Gabor Bergmann
 * 
 */
public class PBodyNormalizer extends PDisjunctionRewriter {

    /**
     * If set to true, shrinks the net by avoiding unnecessary typechecks
     */
    public boolean calcImpliedTypes;
    private IQueryMetaContext context;

    public PBodyNormalizer(IQueryMetaContext context) {
        this(context, true);
    }
    
    /**
     * 
     * @param calculateImpliedTypes
     *            If set to true, shrinks the net by avoiding unnecessary typechecks
     */
    public PBodyNormalizer(IQueryMetaContext context, boolean calculateImpliedTypes) {
        this.context = context;
        calcImpliedTypes = calculateImpliedTypes;
    }
    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        Set<PBody> normalizedBodies = Sets.newHashSet();
        for (PBody body : disjunction.getBodies()) {
            PBodyCopier copier = new PBodyCopier(body);
            PBody modifiedBody = copier.getCopiedBody();
            normalizeBody(modifiedBody);
            normalizedBodies.add(modifiedBody);
            modifiedBody.setStatus(PQueryStatus.OK);
        }
        return new PDisjunction(normalizedBodies);
    }

    public void setContext(IQueryMetaContext context) {
        this.context = context;
    }

    /**
     * Provides a normalized version of the pattern body. May return a different version than the original version if
     * needed.
     * 
     * @param body
     */
    public PBody normalizeBody(PBody body) throws RewriterException {
        try {
            return normalizeBodyInternal(body);
        } catch (QueryProcessingException e) {
            throw new RewriterException("Error during rewriting: {1}", new String[]{e.getMessage()}, e.getShortMessage(), body.getPattern(), e);
        }
    }
    
    PBody normalizeBodyInternal(PBody body) throws QueryProcessingException {
        // UNIFICATION AND WEAK INEQUALITY ELMINATION
        unifyVariablesAlongEqualities(body);
        eliminateWeakInequalities(body);
        removeMootEqualities(body);

        // UNARY ELIMINATION WITH TYPE INFERENCE
        if (calcImpliedTypes) {
            eliminateInferrableTypes(body, context);
        }
        // PREVENTIVE CHECKS
        checkSanity(body);
        return body;
    }

    private void removeMootEqualities(PBody body) {
        Set<Equality> equals = body.getConstraintsOfType(Equality.class);
        for (Equality equality : equals) {
            if (equality.isMoot()) {
                equality.delete();
            }
        }
    }

    /**
     * Unifies allVariables along equalities so that they can be handled as one.
     * 
     * @param body
     */
    void unifyVariablesAlongEqualities(PBody body) {
        Set<Equality> equals = body.getConstraintsOfType(Equality.class);
        for (Equality equality : equals) {
            if (!equality.isMoot()) {
                equality.getWho().unifyInto(equality.getWithWhom());
            }
        }
    }

    /**
     * Eliminates weak inequalities if they are not substantiated.
     * 
     * @param body
     */
    void eliminateWeakInequalities(PBody body) {
        for (Inequality inequality : body.getConstraintsOfType(Inequality.class))
            inequality.eliminateWeak();
    }

    /**
     * Eliminates all type constraints that are inferrable from other constraints.
     */
    void eliminateInferrableTypes(final PBody body, IQueryMetaContext context) {
    	Set<TypeJudgement> subsumedByRetainedConstraints = new HashSet<TypeJudgement>();
    	LinkedList<TypeConstraint> allTypeConstraints = new LinkedList<TypeConstraint>();
        for (PConstraint pConstraint : body.getConstraints()) {
			if (pConstraint instanceof TypeConstraint) {
				allTypeConstraints.add((TypeConstraint) pConstraint);
			} else if (pConstraint instanceof ITypeInfoProviderConstraint) { 
				// non-type constraints are all retained
				final Set<TypeJudgement> directJudgements = 
						((ITypeInfoProviderConstraint) pConstraint).getImpliedJudgements(context);
				subsumedByRetainedConstraints.addAll(TypeHelper.typeClosure(directJudgements, context));
			}
		}
        Collections.sort(allTypeConstraints, PConstraint.CompareByMonotonousID.INSTANCE);
        Queue<TypeConstraint> potentialConstraints = allTypeConstraints; // rename for better comprehension
        
        while (!potentialConstraints.isEmpty()) {
        	TypeConstraint candidate = potentialConstraints.poll();
        	
        	boolean isSubsumed = 
        			subsumedByRetainedConstraints.contains(candidate.getEquivalentJudgement());
        	if (!isSubsumed) 
	        	for (TypeConstraint subsuming : potentialConstraints) { // the remaining ones
	        		final Set<TypeJudgement> directJudgements = 
	        				subsuming.getImpliedJudgements(context);
	        		final Set<TypeJudgement> typeClosure = TypeHelper.typeClosure(directJudgements, context);
					
	        		if (typeClosure.contains(candidate.getEquivalentJudgement())) {
	        			isSubsumed = true;
	        			break;
	        		}
	        	}          
        	
        	if (isSubsumed) { // eliminated
        		candidate.delete();
        	} else { // retained
        		subsumedByRetainedConstraints.addAll(TypeHelper.typeClosure(candidate.getImpliedJudgements(context), context));
        	}      		
        }        
    }

    /**
     * Verifies the sanity of all constraints. Should be issued as a preventive check before layouting.
     * 
     * @param body
     * @throws RetePatternBuildException
     */
    void checkSanity(PBody body) throws QueryProcessingException {
        for (PConstraint pConstraint : body.getConstraints())
            pConstraint.checkSanity();
    }

}
