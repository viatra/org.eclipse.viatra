/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.planner.rewriters;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.planning.helpers.TypeHelper;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyCopier;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PDisjunctionRewriter;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.RewriterException;

import com.google.common.collect.Sets;

/**
 * A type of rewriter that carries out PBody normalization. It only considers and removes redundant unary constraints.
 * 
 * @author Marton Bur
 *
 */
public class PBodyUnaryTypeNormalizer extends PDisjunctionRewriter {
	private IQueryMetaContext context;
	
	public PBodyUnaryTypeNormalizer(IQueryMetaContext context) {
		this.context = context;
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

	private void normalizeBody(PBody body) {
		Set<TypeJudgement> subsumedByRetainedConstraints = new HashSet<TypeJudgement>();
		LinkedList<TypeConstraint> allUnaryTypeConstraints = new LinkedList<TypeConstraint>();

		for (PConstraint pConstraint : body.getConstraints()) {
			if (pConstraint instanceof TypeConstraint) {
				TypeConstraint typeConstraint = (TypeConstraint) pConstraint;
				IInputKey inputKey = typeConstraint.getSupplierKey(); 
				if (inputKey instanceof EClassTransitiveInstancesKey) {
					allUnaryTypeConstraints.add((TypeConstraint) pConstraint);
				}
			}
		}
		Collections.sort(allUnaryTypeConstraints, PConstraint.CompareByMonotonousID.INSTANCE);
		Queue<TypeConstraint> potentialConstraints = allUnaryTypeConstraints; // rename for better comprehension

		while (!potentialConstraints.isEmpty()) {
			TypeConstraint candidate = potentialConstraints.poll();

			boolean isSubsumed = subsumedByRetainedConstraints.contains(candidate.getEquivalentJudgement());
			if (!isSubsumed)
				for (TypeConstraint subsuming : potentialConstraints) { // the remaining ones
					final Set<TypeJudgement> directJudgements = subsuming.getImpliedJudgements(context);
					final Set<TypeJudgement> typeClosure = TypeHelper.typeClosure(directJudgements, context);

					if (typeClosure.contains(candidate.getEquivalentJudgement())) {
						isSubsumed = true;
						break;
					}
				}

			if (isSubsumed) { // eliminated
				candidate.delete();
			} else { // retained
				subsumedByRetainedConstraints.addAll(TypeHelper.typeClosure(candidate.getImpliedJudgements(context),
						context));
			}
		}
	}

	public void setContext(IQueryMetaContext context) {
		this.context = context;
	}
}
