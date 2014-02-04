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
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PQueries;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

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

    private final class AnnotationNameTester implements Predicate<PAnnotation> {
        private final String annotationName;

        private AnnotationNameTester(String annotationName) {
            this.annotationName = annotationName;
        }

        @Override
        public boolean apply(PAnnotation annotation) {
            return annotationName.equals(annotation.getName());
        }
    }

    protected abstract Matcher instantiate(IncQueryEngine engine) throws IncQueryException;

    @Override
    public Matcher getMatcher(Notifier emfRoot) throws IncQueryException {
        IncQueryEngine engine = IncQueryEngine.on(emfRoot);
        return instantiate(engine);
    }

    @Override
    public Matcher getMatcher(IncQueryEngine engine) throws IncQueryException {
        return instantiate(engine);
    }

    private PQueryStatus status = PQueryStatus.UNINITIALIZED;
    private List<PAnnotation> annotations = new ArrayList<PAnnotation>();

    @Override
    public Integer getPositionOfParameter(String parameterName) {
        int index = getParameterNames().indexOf(parameterName);
        return index != -1 ? index : null;
    }

    protected void setStatus(PQueryStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public PQueryStatus getStatus() {
        return status;
    }

    @Override
    public void checkMutability() throws IllegalStateException {
        Preconditions.checkState(getStatus().equals(PQueryStatus.UNINITIALIZED), "Cannot edit query definition " + getFullyQualifiedName());
    }

    @Override
    public Set<PBody> getContainedBodies() {
        Preconditions.checkState(!status.equals(PQueryStatus.UNINITIALIZED), "Query " + getFullyQualifiedName() + " is not initialized.");
        Preconditions.checkState(!status.equals(PQueryStatus.ERROR), "Query " + getFullyQualifiedName() + " contains errors.");
        return doGetContainedBodies();
    }

    protected abstract Set<PBody> doGetContainedBodies();

    public void addAnnotation(PAnnotation annotation) {
        checkMutability();
        annotations.add(annotation);
    }

    @Override
    public List<PAnnotation> getAllAnnotations() {
        return Lists.newArrayList(annotations);
    }

    @Override
    public List<PAnnotation> getAnnotationsByName(final String annotationName) {
        return Lists.newArrayList(Iterables.filter(annotations, new AnnotationNameTester(annotationName)));
    }

    @Override
    public PAnnotation getFirstAnnotationByName(String annotationName) {
        return Iterables.find(annotations, new AnnotationNameTester(annotationName), null);
    }

    @Override
    public List<String> getParameterNames() {
        return Lists.transform(getParameters(), PQueries.parameterNameFunction());
    }

    @Override
    public Set<PQuery> getDirectReferredQueries() {
        Iterable<PQuery> queries = Iterables.concat(Iterables.transform(getContainedBodies(),
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
