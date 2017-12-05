/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Default implementation of PQuery.
 * 
 * @author Bergmann Gabor
 */
public abstract class BasePQuery implements PQuery {

	protected PQueryStatus status = PQueryStatus.UNINITIALIZED;
	/**
     * @since 2.0
     */
	protected final PVisibility visibility;
	protected List<PProblem> pProblems = new ArrayList<PProblem>();
	private List<PAnnotation> annotations = new ArrayList<PAnnotation>();
	private QueryEvaluationHint evaluationHints = new QueryEvaluationHint(null, (IQueryBackendFactory)null);
	PDisjunction canonicalDisjunction;
	
	/** For traceability only. */
	private List<Object> wrappingQuerySpecifications = new ArrayList<Object>(1);

    private static final class AnnotationNameTester implements Predicate<PAnnotation> {
        private final String annotationName;

        private AnnotationNameTester(String annotationName) {
            this.annotationName = annotationName;
        }

        @Override
        public boolean apply(PAnnotation annotation) {
            return (annotation == null) ? false : annotationName.equals(annotation.getName());
        }
    }

	@Override
	public Integer getPositionOfParameter(String parameterName) {
		ensureInitialized();
	    int index = getParameterNames().indexOf(parameterName);
	    return index != -1 ? index : null;
	}

	protected void setStatus(PQueryStatus newStatus) {
	    this.status = newStatus;
	}

	protected void addError(PProblem problem) {
		status = PQueryStatus.ERROR;
	    pProblems.add(problem);
	}

	@Override
	public PQueryStatus getStatus() {
	    return status;
	}

	@Override
	public List<PProblem> getPProblems() {
		return Collections.unmodifiableList(pProblems);
	}

	@Override
	public boolean isMutable() {
	    return status.equals(PQueryStatus.UNINITIALIZED) || status.equals(PQueryStatus.INITIALIZING);
	}

	@Override
	public void checkMutability() {
	    Preconditions.checkState(isMutable(), "Cannot edit query definition %s", getFullyQualifiedName());
	}

	/**
     * @since 1.5
     */
	public void setEvaluationHints(QueryEvaluationHint hints) {
	    checkMutability();
	    this.evaluationHints = hints;
	}

	@Override
	public QueryEvaluationHint getEvaluationHints() {
		ensureInitialized();
		return evaluationHints;
		// TODO instead of field, compute something from annotations?
	}

	protected void addAnnotation(PAnnotation annotation) {
	    checkMutability();
	    annotations.add(annotation);
	}

	@Override
	public List<PAnnotation> getAllAnnotations() {
		ensureInitialized();
	    return Lists.newArrayList(annotations);
	}

	@Override
	public List<PAnnotation> getAnnotationsByName(final String annotationName) {
		ensureInitialized();
	    return Lists.newArrayList(Iterables.filter(annotations, new AnnotationNameTester(annotationName)));
	}

	@Override
	public PAnnotation getFirstAnnotationByName(String annotationName) {
		ensureInitialized();
	    return Iterables.find(annotations, new AnnotationNameTester(annotationName), null);
	}

	@Override
	public List<String> getParameterNames() {
		ensureInitialized();
	    return Lists.transform(getParameters(), PQueries.parameterNameFunction());
	}

	@Override
	public Set<PQuery> getDirectReferredQueries() {
		ensureInitialized();
	    return canonicalDisjunction.getDirectReferredQueries();
	}

	@Override
	public Set<PQuery> getAllReferredQueries() {
	    ensureInitialized();
	    return canonicalDisjunction.getAllReferredQueries();
	}


	@Override
	public List<Object> publishedAs() {
		return wrappingQuerySpecifications;
	}
	
	@Override
	public Set<TypeJudgement> getTypeGuarantees() {
	    ensureInitialized();
		Set<TypeJudgement> result = new HashSet<TypeJudgement>();
		
		List<PParameter> parameters = getParameters();
		for (int i=0; i<parameters.size(); ++i) {
			PParameter parameter = parameters.get(i);
			IInputKey declaredUnaryType = parameter.getDeclaredUnaryType();
			if (declaredUnaryType != null) {
				result.add(new TypeJudgement(declaredUnaryType, Tuples.staticArityFlatTupleOf(i)));
			}
		}
		
		return result;
	}
	
	/**
     * @since 2.0
     */
	public BasePQuery(PVisibility visibility) {
		super();
		this.visibility = visibility;
	}

	@Override
	public PDisjunction getDisjunctBodies() {
		ensureInitialized();
	    Preconditions.checkState(!status.equals(PQueryStatus.ERROR), "Query %s contains errors.", getFullyQualifiedName());
	    return canonicalDisjunction;
	}

	@Override
	public final void ensureInitialized() {
	    try {
            if (status.equals(PQueryStatus.UNINITIALIZED)) {
                setStatus(PQueryStatus.INITIALIZING);
                setBodies(doGetContainedBodies());
                setStatus(PQueryStatus.OK);
            }
	    } catch (QueryInitializationException e) {
	        addError(new PProblem(e, e.getShortMessage()));
	        throw e;
	    }
	}

	protected final void setBodies(Set<PBody> bodies) {
	    canonicalDisjunction = new PDisjunction(this, bodies);
	    for (PBody body : canonicalDisjunction.getBodies()) {
	        body.setStatus(null);
	    }
	    setStatus(PQueryStatus.OK);
	}

	/**
	 * Creates and returns the bodies of the query. If recalled again, a new instance is created.
	 * 
	 * @return
	 * @throws ViatraQueryRuntimeException
	 */
	protected abstract Set<PBody> doGetContainedBodies();

	@Override
	public String toString() {
	    return String.format("PQuery<%s>=%s", getFullyQualifiedName(), super.toString());
	}

    /**
     * @since 2.0
     */
    @Override
    public PVisibility getVisibility() {
        return visibility;
    }
	
}