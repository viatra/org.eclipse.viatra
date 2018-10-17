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
package org.eclipse.viatra.query.tooling.ui.queryresult.properties;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultTreeMatcher;

import com.google.common.collect.Lists;

/**
 * @author Abel Hegedus
 */
public class QueryResultTreeTabDescriptorProvider implements ITabDescriptorProvider {

    private static final String QUERY_RESULT_PROPERTIES_CATEGORY = "org.eclipse.viatra.query.tooling.ui.result.propertiesCategory";
    private static final String QUERY_TAB_ID = "org.eclipse.viatra.query.tooling.ui.properties.query";
    private static final String MATCHER_TAB_ID = "org.eclipse.viatra.query.tooling.ui.properties.matcher";
    private static final String MATCH_TAB_ID = "org.eclipse.viatra.query.tooling.ui.properties.match";
    private static final String ADVANCED_TAB_ID = "org.eclipse.viatra.query.tooling.ui.properties.advanced";
    private AbstractTabDescriptor queryTabDescriptor;
    private AbstractTabDescriptor matcherTabDescriptor;
    private AbstractTabDescriptor matchTabDescriptor;
    private AbstractTabDescriptor advancedTabDescriptor;

    /**
     * @author Abel Hegedus
     */
    public QueryResultTreeTabDescriptorProvider() {
        matchTabDescriptor = new AbstractTabDescriptor() {
            
            @Override
            public String getLabel() {
                return "Match";
            }
            
            @Override
            public String getId() {
                return MATCH_TAB_ID;
            }
            
            @Override
            public String getCategory() {
                return QUERY_RESULT_PROPERTIES_CATEGORY;
            }
            
            @SuppressWarnings("rawtypes")
            @Override
            public List getSectionDescriptors() {
                List<ISectionDescriptor> sections = Lists.newArrayList();
                sections.add(new AdvancedSectionDescriptor(PropertyKind.MATCH));
                return sections;
            }
        };
        matcherTabDescriptor = new AbstractTabDescriptor() {
            
            @Override
            public String getLabel() {
                return "Matcher";
            }
            
            @Override
            public String getId() {
                return MATCHER_TAB_ID;
            }
            
            @Override
            public String getCategory() {
                return QUERY_RESULT_PROPERTIES_CATEGORY;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List getSectionDescriptors() {
                List<ISectionDescriptor> sections = Lists.newArrayList();
                sections.add(new AdvancedSectionDescriptor(PropertyKind.MATCHER));
                return sections;
            }
        };
        queryTabDescriptor = new AbstractTabDescriptor() {
            
            @Override
            public String getLabel() {
                return "Query";
            }
            
            @Override
            public String getId() {
                return QUERY_TAB_ID;
            }
            
            @Override
            public String getCategory() {
                return QUERY_RESULT_PROPERTIES_CATEGORY;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List getSectionDescriptors() {
                List<ISectionDescriptor> sections = Lists.newArrayList();
                sections.add(new AdvancedSectionDescriptor(PropertyKind.QUERY));
                return sections;
            }
        };
        advancedTabDescriptor = new AbstractTabDescriptor() {
            
            @Override
            public String getLabel() {
                return "Advanced";
            }
            
            @Override
            public String getId() {
                return ADVANCED_TAB_ID;
            }
            
            @Override
            public String getCategory() {
                return QUERY_RESULT_PROPERTIES_CATEGORY;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List getSectionDescriptors() {
                List<ISectionDescriptor> sections = Lists.newArrayList();
                sections.add(new AdvancedSectionDescriptor(PropertyKind.ADVANCED));
                return sections;
            }
            
        };
    }
    
    @Override
    public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part, ISelection selection) {
        
        List<ITabDescriptor> descriptors = Lists.newArrayList();
        
        if (selection instanceof StructuredSelection) {
            Object firstElement = ((StructuredSelection)selection).getFirstElement();
            if (firstElement instanceof IPatternMatch) {
                descriptors.add(matchTabDescriptor);
                descriptors.add(matcherTabDescriptor);
                descriptors.add(queryTabDescriptor);
            } else if (firstElement instanceof QueryResultTreeMatcher) {
                descriptors.add(matcherTabDescriptor);
                descriptors.add(queryTabDescriptor);
            } else {
                descriptors.add(advancedTabDescriptor);
            }
        }
        
        return descriptors.toArray(new ITabDescriptor[0]);
    }

    /**
     * @author Abel Hegedus
     */
    private final class AdvancedSectionDescriptor extends AbstractSectionDescriptor {
        
        private PropertyKind kind;
    
        public AdvancedSectionDescriptor(PropertyKind kind) {
            this.kind = kind;
        }
        
        @Override
        public String getTargetTab() {
            switch (kind) {
            case MATCH:
                return MATCH_TAB_ID;
            case MATCHER:
                return MATCHER_TAB_ID;
            case QUERY:
                return QUERY_TAB_ID;
            case ADVANCED:
                return ADVANCED_TAB_ID;
            }
            return null;
        }
    
        @Override
        public ISection getSectionClass() {
            return new QueryResultPropertySection(kind);
        }
    
        @Override
        public String getId() {
            return QueryResultPropertySection.class.getName();
        }
    }
    
    private enum PropertyKind {
        MATCH, MATCHER, QUERY, ADVANCED
    }

    /**
     * @author Abel Hegedus
     */
    private final class QueryResultPropertySection extends AdvancedPropertySection {
        
        private PropertyKind kind;

        public QueryResultPropertySection(PropertyKind kind) {
            this.kind = kind;
        }
        
        @Override
        public void setInput(IWorkbenchPart part, ISelection selection) {
            if (selection instanceof TreeSelection) {
                TreeSelection treeSelection = (TreeSelection)selection;
                Object firstElement = treeSelection.getFirstElement();
                TreePath[] treePaths = treeSelection.getPathsFor(firstElement);
                if(treePaths.length == 1) {
                    TreePath treePath = treePaths[0];
                    if (firstElement instanceof IPatternMatch) {
                        TreePath parentPath = treePath.getParentPath();
                        TreeSelection matcherSelection = new TreeSelection(parentPath);
                        if(kind == PropertyKind.MATCHER) {
                            super.setInput(part, matcherSelection);
                            return;
                        }
                        if(kind == PropertyKind.QUERY) {
                            setInput(part, matcherSelection);
                            return;
                        }
                    } else if (firstElement instanceof QueryResultTreeMatcher && kind == PropertyKind.QUERY) {
                        IQuerySpecificationRegistryEntry entry = ((QueryResultTreeMatcher<?>) firstElement).getEntry();
                        StructuredSelection structuredSelection = new StructuredSelection(entry);
                        super.setInput(part, structuredSelection);
                        return;
                    }
                }
            }
            super.setInput(part, selection);
        }
        
    }
}
