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
package org.eclipse.incquery.testing.core.base;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * Customizable query specification.
 * 
 * @author Marton Bur
 *
 */
public class DisjunctionBasedQuerySpecification implements IQuerySpecification<IncQueryMatcher<? extends IPatternMatch>> {

    private PDisjunction pDisjunction;
    private IQuerySpecification<?> querySpecifiaction;

    /**
     * The constructor. 
     * 
     * @param querySpecicifation is the basis of the new query specification
     * @param pDisjunction the returned PDisjunction instance when the getDisjunctbodes() method is called
     */
    public DisjunctionBasedQuerySpecification(
            IQuerySpecification<?> querySpecicifation, PDisjunction pDisjunction) {
        this.pDisjunction = pDisjunction;
        this.querySpecifiaction = querySpecicifation;
    }
    
    @Override
    public String getFullyQualifiedName() {
        return querySpecifiaction.getFullyQualifiedName();
    }

    @Override
    public PDisjunction getDisjunctBodies() {
        return pDisjunction;
    }

    @Override
    public Set<PQuery> getDirectReferredQueries() {
        return querySpecifiaction.getDirectReferredQueries();
    }

    @Override
    public Set<PQuery> getAllReferredQueries() {
        return querySpecifiaction.getAllReferredQueries();
    }

    @Override
    public List<String> getParameterNames() {
        return querySpecifiaction.getParameterNames();
    }

    @Override
    public List<PParameter> getParameters() {
        return querySpecifiaction.getParameters();
    }

    @Override
    public Integer getPositionOfParameter(String parameterName) {
        return querySpecifiaction.getPositionOfParameter(parameterName);
    }

    @Override
    public PQueryStatus getStatus() {
        return querySpecifiaction.getStatus();
    }

    @Override
    public List<PProblem> getPProblems() {
        return querySpecifiaction.getPProblems();
    }

    @Override
    public void checkMutability() throws IllegalStateException {
        querySpecifiaction.checkMutability();
    }

    @Override
    public boolean isMutable() {
        return querySpecifiaction.isMutable();
    }

    @Override
    public List<PAnnotation> getAllAnnotations() {
        return querySpecifiaction.getAllAnnotations();
    }

    @Override
    public List<PAnnotation> getAnnotationsByName(String annotationName) {
        return querySpecifiaction.getAnnotationsByName(annotationName);
    }

    @Override
    public PAnnotation getFirstAnnotationByName(String annotationName) {
        return querySpecifiaction.getFirstAnnotationByName(annotationName);
    }

    @Override
    @Deprecated
    public IncQueryMatcher<? extends IPatternMatch> getMatcher(Notifier emfRoot) throws IncQueryException {
        return querySpecifiaction.getMatcher(emfRoot);
    }

    @Override
    public IncQueryMatcher<? extends IPatternMatch> getMatcher(IncQueryEngine engine) throws IncQueryException {
        return querySpecifiaction.getMatcher(engine);
    }

    @Override
    public IPatternMatch newEmptyMatch() {
        return querySpecifiaction.newEmptyMatch();
    }

    @Override
    public IPatternMatch newMatch(Object... parameters) {
        return querySpecifiaction.newMatch(parameters);
    }

}
