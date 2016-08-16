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
            p.setName(list.get(list.size() - 1));
            list.set(list.size() - 1, new EMFPatternLanguageJvmModelInferrerUtil().matcherClassName(p)); 
            
            String fqn = Joiner.on(".").join(list);
            SearchPattern pattern = SearchPattern.createPattern(fqn, IJavaSearchConstants.CLASS, IJavaSearchConstants.ALL_OCCURRENCES, SearchPattern.R_EXACT_MATCH);
            IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
            LocationSearchRequestor requestor = new LocationSearchRequestor();
            
            try {
                new SearchEngine().search(pattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, requestor, new NullProgressMonitor());
                if (!requestor.opened) {
                    MessageDialog.openWarning(HandlerUtil.getActiveShellChecked(event), "Show Location", String.format("Cannot open event source. Is contributing project %s indexed by JDT?", provider.getSourceProjectName()));
                }
            } catch (CoreException e) {
                throw new ExecutionException("Error while opening editor", e);
            }
            
            
        }
    }

}