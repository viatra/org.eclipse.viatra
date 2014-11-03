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

package org.eclipse.incquery.runtime.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.RewriterException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Base implementation of IQuerySpecification.
 *
 * @author Gabor Bergmann
 *
 */
public abstract class BaseQuerySpecification<Matcher extends IncQueryMatcher<? extends IPatternMatch>> implements
        IQuerySpecification<Matcher> {

    private static final Logger LOGGER = IncQueryLoggingUtil.getLogger(BaseQuerySpecification.class);
    protected static final IPatternMatcherContext CONTEXT = new EMFPatternMatcherContext(LOGGER);
    protected static final PBodyNormalizer NORMALIZER = new PBodyNormalizer(CONTEXT);
    
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

    protected abstract Matcher instantiate(IncQueryEngine engine) throws IncQueryException;

    @Override
    public Matcher getMatcher(Notifier emfRoot) throws IncQueryException {
        IncQueryEngine engine = IncQueryEngine.on(emfRoot);
        ensureInitialized();
        return instantiate(engine);
    }

    @Override
    public Matcher getMatcher(IncQueryEngine engine) throws IncQueryException {
        ensureInitialized();
        return instantiate(engine);
    }

    protected PQueryStatus status = PQueryStatus.UNINITIALIZED;
    protected List<PProblem> pProblems = new ArrayList<PProblem>();
    private List<PAnnotation> annotations = new ArrayList<PAnnotation>();

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
        return status.equals(PQueryStatus.UNINITIALIZED);
    }
    
    @Override
    public void checkMutability() throws IllegalStateException {
        Preconditions.checkState(isMutable(), "Cannot edit query definition " + getFullyQualifiedName());
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

    PDisjunction canonicalDisjunction;
    
    @Override
    public PDisjunction getDisjunctBodies() {
        ensureInitialized();
        Preconditions.checkState(!status.equals(PQueryStatus.ERROR), "Query " + getFullyQualifiedName() + " contains errors.");
        return canonicalDisjunction;
    }
    
    protected final void ensureInitialized() {
        try {
            if (status.equals(PQueryStatus.UNINITIALIZED)) {
                setBodies(doGetContainedBodies());
                setStatus(PQueryStatus.OK);
            }
        } catch (IncQueryException e) {
            addError(new PProblem(e, e.getShortMessage()));
            throw new RuntimeException(e);
        } catch (RewriterException e) {
            addError(new PProblem(e));
            throw new RuntimeException(e);
        }
    }

    protected final void setBodies(Set<PBody> bodies) throws RewriterException {
        canonicalDisjunction = new PDisjunction(this, bodies);
        for (PBody body : canonicalDisjunction.getBodies()) {
            body.setStatus(null);
        }
        NORMALIZER.rewrite(canonicalDisjunction);
        setStatus(PQueryStatus.OK);
    }
    
    /**
     * Creates and returns the bodies of the query. If recalled again, a new instance is created.
     * 
     * @return
     */
    protected abstract Set<PBody> doGetContainedBodies() throws IncQueryException;

    // // EXPERIMENTAL
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain) throws IncQueryRuntimeException {
    // return getMatcher(trDomain, 0);
    // }
    //
    // @Override
    // public Matcher getMatcher(TransactionalEditingDomain trDomain, int numThreads) throws IncQueryRuntimeException {
    // try {
    // IncQueryEngine engine = EngineManager.getInstance().getReteEngine(trDomain, numThreads);
    // return instantiate(engine);
    // } catch (RetePatternBuildException e) {
    // throw new IncQueryRuntimeException(e);
    // }
    // }

}
