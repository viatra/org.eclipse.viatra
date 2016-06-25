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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.IPatternBasedSpecificationProvider;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ShowLocationHandler extends AbstractHandler {

    @Inject
    IURIEditorOpener uriOpener;
    
    private static class LocationSearchRequestor extends SearchRequestor {

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
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        QueryRegistryTreeEntry entry = (QueryRegistryTreeEntry) selection.getFirstElement();
        IQuerySpecificationProvider _provider = entry.getEntry().getProvider();
        if (_provider instanceof IPatternBasedSpecificationProvider) {
            IPatternBasedSpecificationProvider provider = (IPatternBasedSpecificationProvider) _provider;
            uriOpener.open(provider.getSpecificationSourceURI(), true);            
        } else {
            List<String> list = Lists.newArrayList(Splitter.on(".").split(_provider.getFullyQualifiedName()));
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
                    MessageDialog.openWarning(HandlerUtil.getActiveShellChecked(event), "Show Location", String.format("Cannot open event source. Is contributing project %s indexed by JDT?", _provider.getSourceProjectName()));
                }
            } catch (CoreException e) {
                throw new ExecutionException("Error while opening editor", e);
            }
            
            
        }   
        return null;
    }


}
