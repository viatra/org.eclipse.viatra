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
package org.eclipse.viatra.query.testing.core.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * Customizable query specification.
 * 
 * @author Marton Bur
 *
 */
public class DisjunctionBasedPQuery implements PQuery {

    private PDisjunction pDisjunction;
    private PQuery wrapped;

    /**
     * The constructor.
     * 
     * @param wrapped
     *            is the basis of the new query
     * @param pDisjunction
     *            the returned PDisjunction instance when the getDisjunctbodes() method is called
     */
    public DisjunctionBasedPQuery(PQuery wrapped, PDisjunction pDisjunction) {
        this.pDisjunction = pDisjunction;
        this.wrapped = wrapped;
    }

    @Override
    public PDisjunction getDisjunctBodies() {
        return pDisjunction;
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public Set<PQuery> getDirectReferredQueries() {
        return pDisjunction.getDirectReferredQueries();
    }

    @Override
    public Set<PQuery> getAllReferredQueries() {
        return pDisjunction.getAllReferredQueries();
    }

    @Override
    public List<String> getParameterNames() {
        return wrapped.getParameterNames();
    }

    @Override
    public List<PParameter> getParameters() {
        return wrapped.getParameters();
    }

    @Override
    public Integer getPositionOfParameter(String parameterName) {
        return wrapped.getPositionOfParameter(parameterName);
    }

    @Override
    public PQueryStatus getStatus() {
        return wrapped.getStatus();
    }

    @Override
    public List<PProblem> getPProblems() {
        return wrapped.getPProblems();
    }

    @Override
    public void checkMutability() throws IllegalStateException {
        wrapped.checkMutability();
    }

    @Override
    public boolean isMutable() {
        return wrapped.isMutable();
    }

    @Override
    public List<PAnnotation> getAllAnnotations() {
        return wrapped.getAllAnnotations();
    }

    @Override
    public List<PAnnotation> getAnnotationsByName(String annotationName) {
        return wrapped.getAnnotationsByName(annotationName);
    }

    @Override
    public PAnnotation getFirstAnnotationByName(String annotationName) {
        return wrapped.getFirstAnnotationByName(annotationName);
    }

    @Override
    public QueryEvaluationHint getEvaluationHints() {
        return wrapped.getEvaluationHints();
    }

    @Override
    public void ensureInitialized() throws QueryInitializationException {
        wrapped.ensureInitialized();
    }

    List<Object> specificationTraces = new ArrayList<Object>();

    @Override
    public List<Object> publishedAs() {
        return specificationTraces;
    }

    @Override
    public Set<TypeJudgement> getTypeGuarantees() {
        return Collections.emptySet();
    }

}
