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
package org.eclipse.incquery.runtime.matchers.psystem.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Default implementation of PQuery.
 * 
 * @author Bergmann Gabor
 */
public abstract class BasePQuery implements PQuery {

	protected PQueryStatus status = PQueryStatus.UNINITIALIZED;
	protected List<PProblem> pProblems = new ArrayList<PProblem>();
	private List<PAnnotation> annotations = new ArrayList<PAnnotation>();
	private QueryEvaluationHint evaluationHints = null;
	PDisjunction canonicalDisjunction;
	
	/** For traceability only. */
	private List<Object> wrappingQuerySpecifications = new ArrayList<Object>(1);

    private final class AnnotationNameTester implements Predicate<PAnnotation> {
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
		ensureInitializedSneaky();
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
	    return status.equals(PQueryStatus.UNINITIALIZED);
	}

	@Override
	public void checkMutability() throws IllegalStateException {
	    Preconditions.checkState(isMutable(), "Cannot edit query definition " + getFullyQualifiedName());
	}

	protected void setEvaluationHints(QueryEvaluationHint hints) {
	    checkMutability();
	    this.evaluationHints = hints;
	}

	@Override
	public QueryEvaluationHint getEvaluationHints() {
		ensureInitializedSneaky();
		return evaluationHints;
		// TODO instead of field, compute something from annotations?
	}

	protected void addAnnotation(PAnnotation annotation) {
	    checkMutability();
	    annotations.add(annotation);
	}

	@Override
	public List<PAnnotation> getAllAnnotations() {
		ensureInitializedSneaky();
	    return Lists.newArrayList(annotations);
	}

	@Override
	public List<PAnnotation> getAnnotationsByName(final String annotationName) {
		ensureInitializedSneaky();
	    return Lists.newArrayList(Iterables.filter(annotations, new AnnotationNameTester(annotationName)));
	}

	@Override
	public PAnnotation getFirstAnnotationByName(String annotationName) {
		ensureInitializedSneaky();
	    return Iterables.find(annotations, new AnnotationNameTester(annotationName), null);
	}

	@Override
	public List<String> getParameterNames() {
		ensureInitializedSneaky();
	    return Lists.transform(getParameters(), PQueries.parameterNameFunction());
	}

	@Override
	public Set<PQuery> getDirectReferredQueries() {
		ensureInitializedSneaky();
	    Iterable<PQuery> queries = Iterables.concat(Iterables.transform(canonicalDisjunction.getBodies(),
	            PQueries.directlyReferencedQueriesFunction()));
	    return Sets.newHashSet(queries);
	}

	@Override
	public Set<PQuery> getAllReferredQueries() {
	    Set<PQuery> processedQueries = Sets.newHashSet((PQuery)this);
	    Set<PQuery> foundQueries = getDirectReferredQueries();
	    Set<PQuery> newQueries = Sets.newHashSet(foundQueries);
	
	    while(!processedQueries.containsAll(newQueries)) {
	        PQuery query = newQueries.iterator().next();
	        processedQueries.add(query);
	        newQueries.remove(query);
	        Set<PQuery> referred = query.getDirectReferredQueries();
	        referred.removeAll(processedQueries);
	        foundQueries.addAll(referred);
	        newQueries.addAll(referred);
	    }
	    return foundQueries;
	}


	@Override
	public List<Object> publishedAs() {
		return wrappingQuerySpecifications;
	}
	
	/**
	 * 
	 */
	public BasePQuery() {
		super();
	}

	@Override
	public PDisjunction getDisjunctBodies() {
		ensureInitializedSneaky();
	    Preconditions.checkState(!status.equals(PQueryStatus.ERROR), "Query " + getFullyQualifiedName() + " contains errors.");
	    return canonicalDisjunction;
	}

	@Override
	public final void ensureInitialized() throws QueryInitializationException {
	    try {
            if (status.equals(PQueryStatus.UNINITIALIZED)) {
                setBodies(doGetContainedBodies());
                setStatus(PQueryStatus.OK);
            }
	    } catch (QueryInitializationException e) {
	        addError(new PProblem(e, e.getShortMessage()));
	        throw e;
	    }
	}
	
	public final void ensureInitializedSneaky() {
	    try {
	       ensureInitialized();
	    } catch (QueryInitializationException e) {
	        throw new RuntimeException(e);
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
	 */
	protected abstract Set<PBody> doGetContainedBodies() throws QueryInitializationException;

}