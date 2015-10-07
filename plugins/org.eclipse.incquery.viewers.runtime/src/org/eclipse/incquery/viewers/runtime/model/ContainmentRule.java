/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.viewers.runtime.specifications.ContainmentQuerySpecificationDescriptor;
import org.eclipse.incquery.viewmodel.core.ViewModelRule;
import org.eclipse.incquery.viewmodel.core.util.ViewModelUtil;

public class ContainmentRule extends ViewModelRule {

    private final ContainmentQuerySpecificationDescriptor descriptor;
    private final ViewerState state;
    private ViewerDataFilter filter;

    public ContainmentRule(ContainmentQuerySpecificationDescriptor descriptor, ViewerState state,
            ViewerDataFilter filter) {
        super(descriptor);
        this.descriptor = descriptor;
        this.state = state;
        this.filter = filter;
    }

    public static ContainmentRule initiate(IQuerySpecification<?> specification, PAnnotation annotation,
            ViewerState state, ViewerDataFilter filter) throws QueryInitializationException {
        ContainmentQuerySpecificationDescriptor descriptor = new ContainmentQuerySpecificationDescriptor(specification,
                annotation);
        return new ContainmentRule(descriptor, state, filter);
    }

    private EventFilter<IPatternMatch> createFilter(ViewerDataFilter baseFilter) {
        if (!baseFilter.isFiltered(getBaseSpecification()))
            return null;

        ViewerFilterDefinition filterDefinition = baseFilter.getFilter(getBaseSpecification());
        return EventFilterBuilder.createEventFilter(filterDefinition, getReferencedSpecification());
    }

    @Override
    public Job<GenericPatternMatch> getAppearedJob() {
        return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {
                        String sourceParam = "trace<" + descriptor.getContainer() + ">";
                        String targetParam = "trace<" + descriptor.getItem() + ">";

                        Item source = (Item) match.get(sourceParam);
                        Item target = (Item) match.get(targetParam);

                        EObject eObject = ViewModelUtil.create(NotationPackage.eINSTANCE.getContainment(),
                                state.getNotationModel(), NotationPackage.eINSTANCE.getNotationModel_Containments());
                        ViewModelUtil.trace(state.getManager(), getReferencedSpecification().getFullyQualifiedName(),
                                Collections.singleton(eObject), match.get(descriptor.getContainer()),
                                match.get(descriptor.getItem()));

                        Containment edge = (Containment) eObject;
                        edge.setSource(source);
                        edge.setTarget(target);

                        state.containmentAppeared(edge);
                        logger.debug("Containment appeared: " + "<"+getTracedSpecification().getFullyQualifiedName()+">" + edge.toString());

                    }

                }));
    }

    @Override
    protected Job<GenericPatternMatch> getUpdatedJob() {
    	return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.UPDATED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {
                        return;
                    }
                }));
    }
    
    @Override
    public Job<GenericPatternMatch> getDisappearedJob() {
        return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {
                        if (ViewModelUtil.target(match) instanceof Containment) {
                            Collection<EObject> edges = ViewModelUtil.delete(match);
                            Iterator<EObject> iterator = edges.iterator();
                            while(iterator.hasNext()) {
                            	EObject edge = iterator.next();
                            	EcoreUtil.delete(edge);
                                state.containmentDisappeared((Containment) edge);
                                logger.debug("Containment disappeared: " + "<"+getTracedSpecification().getFullyQualifiedName()+">" + edge.toString());
                            }
                            
                        }
                    }
                }));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.incquery.viewmodel.core.ViewModelRule#prepareFilter()
     */
    @Override
    protected EventFilter<IPatternMatch> prepareFilter() {
        return createFilter(filter);
    }
}
