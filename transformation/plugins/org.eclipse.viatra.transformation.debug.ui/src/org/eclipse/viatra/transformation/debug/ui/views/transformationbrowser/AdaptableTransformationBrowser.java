/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.transformation.debug.model.ITransformationStateListener;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVMFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.IAdaptableEVMFactoryListener;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class AdaptableTransformationBrowser extends ViewPart
        implements IAdaptableEVMFactoryListener, ITransformationStateListener {
    public static final String ID = "org.eclipse.viatra.transformation.debug.ui.AdaptableTransformationBrowser";

    private Map<AdaptableEVM, TransformationState> transformationStateMap = Maps.newHashMap();
    private Multimap<Class<?>, Object> expandedElementsMap = ArrayListMultimap.create();
    private TreeViewer treeViewer;
    private Object selection;

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        treeViewer = new TreeViewer(composite, SWT.BORDER);

        treeViewer.setContentProvider(new RuleBrowserContentProvider(this));
        treeViewer.setLabelProvider(new RuleBrowserLabelProvider(this));

        treeViewer.addDoubleClickListener(new AdaptableEVMDoubleClickListener(this));
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                }
            }
        });

        AdaptableEVMFactory.getInstance().registerListener(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        expandedElementsMap.clear();
        transformationStateMap.clear();
        AdaptableEVMFactory.getInstance().unRegisterListener(this);
        TransformationThreadFactory.getInstance().unRegisterListener(this);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    @Override
    public void adaptableEVMPoolChanged(final List<AdaptableEVM> adaptableEVMs) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                for (AdaptableEVM evm : transformationStateMap.keySet()) {
                    if (!adaptableEVMs.contains(evm)) {
                        transformationStateMap.remove(evm);
                    }
                }
                for (AdaptableEVM evm : adaptableEVMs) {
                    if (!transformationStateMap.containsKey(evm)) {
                        transformationStateMap.put(evm, null);
                    }
                }

                Object[] expandedElements = treeViewer.getExpandedElements();
                treeViewer.setInput(transformationStateMap);
                treeViewer.setExpandedElements(expandedElements);
            }
        });

    }

    @Override
    public void transformationStateChanged(final TransformationState state, final String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                AdaptableEVM evmInstance = AdaptableEVMFactory.getInstance().getAdaptableEVMInstance(id);

                transformationStateMap.put(evmInstance, state);
                Object[] expandedElements = treeViewer.getExpandedElements();
                treeViewer.setInput(transformationStateMap);
                treeViewer.setExpandedElements(expandedElements);
            }
        });
    }

    @Override
    public void transformationStateDisposed(final TransformationState state, final String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                AdaptableEVM evmInstance = AdaptableEVMFactory.getInstance().getAdaptableEVMInstance(id);

                transformationStateMap.remove(evmInstance);
                treeViewer.setInput(transformationStateMap);
            }
        });

    }

    public Object getSelection() {
        return selection;
    }

    public void setViewConfiguration(final TransformationViewConfiguration config) {
        treeViewer.getControl().getDisplay().syncExec(new ConfigurationApplication(config, this));
    }

    protected TransformationState getStateForRuleSpecification(Pair<RuleSpecification<?>, EventFilter<?>> spec) {
        for (AdaptableEVM evm : transformationStateMap.keySet()) {
            TransformationState transformationState = transformationStateMap.get(evm);
            if (transformationState != null && transformationState.getRules().contains(spec)) {
                return transformationState;
            }
        }
        return null;
    }

    protected TransformationState getStateForActivation(Activation<?> act) {
        for (AdaptableEVM evm : transformationStateMap.keySet()) {
            TransformationState transformationState = transformationStateMap.get(evm);
            if (transformationState != null && transformationState.getConflictingActivations().contains(act)) {
                return transformationState;
            }
        }
        return null;
    }

    protected Map<AdaptableEVM, TransformationState> getTransformationStateMap() {
        return transformationStateMap;
    }

    private final class ConfigurationApplication implements Runnable {
        private final TransformationViewConfiguration config;
        private final AdaptableTransformationBrowser view;

        private ConfigurationApplication(TransformationViewConfiguration config, AdaptableTransformationBrowser view) {
            this.config = config;
            this.view = view;
        }

        @Override
        public void run() {
            switch (config) {
            case RULE_BROWSER:
                saveExpandedElements();
                treeViewer.setContentProvider(new RuleBrowserContentProvider(view));
                treeViewer.setLabelProvider(new RuleBrowserLabelProvider(view));
                treeViewer.refresh();
                treeViewer.setExpandedElements(loadExpandedElements());
                break;

            case CONFLICTSET_BROWSER:
                saveExpandedElements();
                treeViewer.setContentProvider(new ConflictSetContentProvider(view));
                treeViewer.setLabelProvider(new ConflictSetLabelProvider(view));
                treeViewer.refresh();
                treeViewer.setExpandedElements(loadExpandedElements());
                break;
            default:
                break;
            }
        }

        private void saveExpandedElements() {
            Object[] expandedElements = treeViewer.getExpandedElements();
            IContentProvider contentProvider = treeViewer.getContentProvider();
            for (Object element : expandedElements) {
                expandedElementsMap.put(contentProvider.getClass(), element);
            }

        }

        private Object[] loadExpandedElements() {
            IContentProvider contentProvider = treeViewer.getContentProvider();
            Collection<Object> elements = expandedElementsMap.get(contentProvider.getClass());

            return elements.toArray(new Object[elements.size()]);

        }
    }
}
