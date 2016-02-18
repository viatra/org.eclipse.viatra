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
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormatSpecification;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.notation.NotationPackage;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ItemQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.util.FormatParser;
import org.eclipse.viatra.addon.viewers.runtime.util.LabelParser;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.views.core.ViewModelRule;
import org.eclipse.viatra.transformation.views.core.util.ViewModelUtil;

public class ItemRule extends ViewModelRule {

    private final ItemQuerySpecificationDescriptor descriptor;
    private final ViewerState state;
    private ViewerDataFilter baseFilter;

    private ItemRule(ItemQuerySpecificationDescriptor descriptor, ViewerState state, ViewerDataFilter filter)
            throws QueryInitializationException {
        super(descriptor);
        this.state = state;
        this.descriptor = descriptor;
        this.baseFilter = filter;
    }
    
    public static ItemRule initiate(IQuerySpecification<?> specification, PAnnotation annotation, ViewerState state,
            ViewerDataFilter filter) throws QueryInitializationException {
        ItemQuerySpecificationDescriptor descriptor = new ItemQuerySpecificationDescriptor(specification, annotation);
        return new ItemRule(descriptor, state, filter);
    }

    private EventFilter<IPatternMatch> createFilter(ViewerDataFilter baseFilter) {
        if (!baseFilter.isFiltered(getBaseSpecification()))
            return null;

        ViewerFilterDefinition filterDefinition = baseFilter.getFilter(getBaseSpecification());
        return EventFilterBuilder.createEventFilter(filterDefinition, getReferencedSpecification());
    }

    @Override
    public Job<GenericPatternMatch> getAppearedJob() {
        return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(CRUDActivationStateEnum.CREATED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {

                        Object param = match.get(descriptor.getSource());
                        EObject eObject = ViewModelUtil.<Item> create(NotationPackage.eINSTANCE.getItem(),
                                state.getNotationModel(), NotationPackage.eINSTANCE.getNotationModel_Items());

                        ViewModelUtil.trace(state.getManager(), getReferencedSpecification().getFullyQualifiedName(),
                                Collections.singleton(eObject), param);

                        Item item = (Item) eObject;
                        if (param instanceof EObject)
                            item.setParamEObject((EObject) param);
                        else
                            item.setParamObject(param);
                        item.setPolicy(descriptor.getPolicy());
                        item.setLabel(LabelParser.calculateLabel(match, descriptor.getLabel()));

                        if (descriptor.isFormatted()) {
                            FormatSpecification formatSpecification = FormatParser.parseFormatAnnotation(descriptor
                                    .getFormatAnnotation());
                            item.setFormat(formatSpecification);
                        }

                        state.itemAppeared(item);
                        logger.debug("Item appeared: " + "<"+getTracedSpecification().getFullyQualifiedName()+">" + item.toString());
                    }

                }));
    }

    @Override
    public Job<GenericPatternMatch> getDisappearedJob() {
        return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(CRUDActivationStateEnum.DELETED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {
                        if (ViewModelUtil.target(match) instanceof Item) {
                            Collection<EObject> deletedItems = ViewModelUtil.delete(match);
                            for (EObject item : deletedItems) {
                                state.itemDisappeared((Item) item);
                                logger.debug("Item disappeared: " + "<"+getTracedSpecification().getFullyQualifiedName()+">" + item.toString());
                            }
                        }
                    }

                }));
    }

    @Override
    public Job<GenericPatternMatch> getUpdatedJob() {
        return Jobs.newErrorLoggingJob(Jobs.newStatelessJob(CRUDActivationStateEnum.UPDATED,
                new IMatchProcessor<GenericPatternMatch>() {

                    @Override
                    public void process(GenericPatternMatch match) {
                        if (ViewModelUtil.target(match) instanceof Item) {
                            Item item = (Item) ViewModelUtil.target(match);
                            String oldLabel = item.getLabel();
                            String newLabel = LabelParser.calculateLabel(match, descriptor.getLabel());
                            if (!oldLabel.equals(newLabel)) {
                                item.setLabel(newLabel);
                                state.labelUpdated(item, newLabel);
                                logger.debug("Item updated: " + "<"+getTracedSpecification().getFullyQualifiedName()+">" + item.toString());
                            }
                        }
                    }
                }));
    }

    @Override
    protected EventFilter<IPatternMatch> prepareFilter() {
        return createFilter(baseFilter);
    }
}
