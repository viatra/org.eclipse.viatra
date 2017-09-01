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

import java.util.List;

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
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.IPatternBasedSpecificationProvider;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
        if (provider instanceof IPatternBasedSpecificationProvider) {
            IPatternBasedSpecificationProvider patternBasedProvider = (IPatternBasedSpecificationProvider) provider;
            uriOpener.open(patternBasedProvider.getSpecificationSourceURI(), true);            
        } else {
            List<String> list = Lists.newArrayList(Splitter.on(".").split(provider.getFullyQualifiedName()));
            
            Pattern p = PatternLanguageFactory.eINSTANCE.createPattern();
            String patternName = list.remove(list.size() - 1);
            p.setName(patternName);
            EMFPatternLanguageJvmModelInferrerUtil inferrerUtil = new EMFPatternLanguageJvmModelInferrerUtil();
            String querySpecificationClassName = inferrerUtil.findInferredSpecification(p).getQualifiedName();
            
            String publicFqn = Joiner.on(".").join(Iterables.concat(list, ImmutableList.of("util", querySpecificationClassName)));
            String privateFqn = Joiner.on(".").join(Iterables.concat(list, ImmutableList.of("internal", querySpecificationClassName)));
            SearchPattern publicPattern = SearchPattern.createPattern(publicFqn, IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
            SearchPattern privatePattern = SearchPattern.createPattern(privateFqn, IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
            IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
            LocationSearchRequestor requestor = new LocationSearchRequestor();
            
            try {
                // Try to find first public, then private query specification definition
                new SearchEngine().search(publicPattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, requestor, new NullProgressMonitor());
                if (!requestor.opened) {
                    new SearchEngine().search(privatePattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, requestor, new NullProgressMonitor());
                    if (!requestor.opened) {
                        MessageDialog.openWarning(HandlerUtil.getActiveShellChecked(event), "Show Location", String.format("Cannot open source pattern %s. Is contributing project %s indexed by JDT?", provider.getFullyQualifiedName(), provider.getSourceProjectName()));
                    }
                }
            } catch (CoreException e) {
                throw new ExecutionException("Error while opening editor", e);
            }
            
            
        }
    }

}