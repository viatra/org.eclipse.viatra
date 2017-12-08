/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecificationWithGenericMatcher;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.IPatternBasedSpecificationProvider;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.inject.Inject;

/**
 * @author Abel Hegedus
 * @since 1.4
 */
public abstract class ShowPatternLocationHandler extends AbstractHandler {

    @Inject
    protected IURIEditorOpener uriOpener;
    
    protected static class LocationSearchRequestor extends SearchRequestor {
    
            private boolean opened = false;
    
            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
                Object element = match.getElement();
                if (!opened && element instanceof IJavaElement) {
                    opened = true;
                    JavaUI.openInEditor((IJavaElement) element);
                }
            }
            
        }

    public ShowPatternLocationHandler() {
        super();
    }

    protected void showPatternLocation(ExecutionEvent event, IQuerySpecificationProvider provider) throws ExecutionException {
        @SuppressWarnings("rawtypes")
        Class<? extends IQuerySpecification> specificationClass = provider.get().getClass();
        if (provider instanceof IQuerySpecificationRegistryEntry) {
            showPatternLocation(event, ((IQuerySpecificationRegistryEntry) provider).getProvider());
        } else if (provider instanceof IPatternBasedSpecificationProvider) {
            IPatternBasedSpecificationProvider patternBasedProvider = (IPatternBasedSpecificationProvider) provider;
            uriOpener.open(patternBasedProvider.getSpecificationSourceURI(), true);            
        } else if (BaseGeneratedEMFQuerySpecification.class.isAssignableFrom(specificationClass)
                || BaseGeneratedEMFQuerySpecificationWithGenericMatcher.class.isAssignableFrom(specificationClass)) {
               
            
            String patternFqn = specificationClass.getName();
            SearchPattern searchPattern = SearchPattern.createPattern(patternFqn, IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
            IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
            LocationSearchRequestor requestor = new LocationSearchRequestor();
            
            try {
                new SearchEngine().search(searchPattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, requestor, new NullProgressMonitor());
                if (!requestor.opened) {
                    MessageDialog.openWarning(HandlerUtil.getActiveShellChecked(event), "Show Location", String.format("Cannot open source pattern %s. Is contributing project %s indexed by JDT?", provider.getFullyQualifiedName(), provider.getSourceProjectName()));
                }
            } catch (CoreException e) {
                throw new ExecutionException("Error while opening editor", e);
            }
            
            
        }
    }

}