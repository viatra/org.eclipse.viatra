/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.addon.viewers.tooling.ui.views.ViewersMultiSandboxView;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherCollection;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;

public abstract class InitializeViewersHandler extends AbstractHandler {

    public InitializeViewersHandler(IModelConnectorTypeEnum modelconnectortype) {
        super();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof TreeSelection) {
            Object firstElement = ((TreeSelection) selection).getFirstElement();
            try {
                if (firstElement instanceof IFilteredMatcherContent) {
                    IFilteredMatcherContent<?> matcherContent = (IFilteredMatcherContent<?>) firstElement;
                    ViatraQueryMatcher<?> matcher = matcherContent.getMatcher();
                    EMFScope scope = (EMFScope) matcher.getEngine().getScope();
                    
                    IFilteredMatcherCollection parent = matcherContent.getParent();
                    initializeViewersSandboxOnCollection(scope, parent);
                } else if(firstElement instanceof IFilteredMatcherCollection) {
                    IFilteredMatcherCollection parent = (IFilteredMatcherCollection) firstElement;
                    
                    Iterable<IFilteredMatcherContent<?>> filteredMatchers = parent.getFilteredMatchers();
                    if(filteredMatchers != null){
                        Iterator<IFilteredMatcherContent<?>> iterator = filteredMatchers.iterator();
                        if(iterator != null && iterator.hasNext()) {
                            IFilteredMatcherContent<?> matcherContent = iterator.next();
                            EMFScope scope = (EMFScope) matcherContent.getMatcher().getEngine().getScope();
                            initializeViewersSandboxOnCollection(scope, parent);
                        }
                    }
                }
            } catch (ViatraQueryException e) {
                throw new ExecutionException("Error initializing pattern matcher.", e);
            } catch (IllegalArgumentException e) {
                throw new ExecutionException("Invalid selection", e);
            }
        }

        return null;
    }

    private void initializeViewersSandboxOnCollection(EMFScope scope, IFilteredMatcherCollection parent) throws ViatraQueryException {
        Iterable<IFilteredMatcherContent<?>> filteredMatchers = parent.getFilteredMatchers();
        
        // collect specifications from matchers
        // construct viewer filter from filters 
        Set<IQuerySpecification<?>> specifications = new HashSet<>();
        ViewerDataFilter dataFilter = new ViewerDataFilter();
        for (IFilteredMatcherContent<?> filteredMatcherContent : filteredMatchers) {
            IQuerySpecification<?> specification = filteredMatcherContent.getMatcher().getSpecification();
            specifications.add(specification);
            IPatternMatch filter = filteredMatcherContent.getFilterMatch();
            if (Arrays.stream(filter.toArray()).anyMatch(el -> el != null)) {
                dataFilter.addSingleFilter(specification, filter);
            }
        }
        
        ViewersMultiSandboxView.ensureOpen();
        ViewersMultiSandboxView.getInstance().initializeContents(scope, specifications, dataFilter);
    }

}