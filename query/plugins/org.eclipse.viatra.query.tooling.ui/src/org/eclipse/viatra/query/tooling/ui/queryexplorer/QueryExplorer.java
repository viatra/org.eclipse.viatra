/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.queryexplorer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.detail.DetailsViewerUtil;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.flyout.FlyoutPreferences;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatchContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContentKey;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.QueryExplorerLabelProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.QueryExplorerObservableFactory;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.QueryExplorerTreeStructureAdvisor;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.RootContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternsViewerFlatContentProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternsViewerFlatLabelProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalContentProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalLabelProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternsViewerInput;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.preference.PreferenceConstants;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.CheckStateListener;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.CheckStateProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.DoubleClickListener;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.QueryExplorerPatternRegistry;
import org.eclipse.viatra.query.tooling.ui.registry.QueryBackendRegistry;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * The Query Explorer can be used to observe the contents of the pattern matchers on given EMF models. The patterns and
 * models can be loaded into the explorer and it provides a tree viewer to browse the contents. Additional functionality
 * involves the possibility to filter match sets, navigate to source model elements, select which patterns are active,
 * etc.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class QueryExplorer extends ViewPart {

    private static final String PACKAGE_PRESENTATION_STATE = "packagePresentationState";
    private static final String PATTERNS_VIEWER_FLYOUT_STATE = "patternsViewerFlyoutState";
    private static final String DETAILS_VIEW_FLYOUT_STATE = "detailsViewFlyoutState";
    
    public static final String QUERY_EXPLORER_ANNOTATION = "QueryExplorer";
    public static final String QUERY_EXPLORER_CHECKED_PARAMETER = "checked";
    
    public static final String ID = "org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer";

    private final Map<PatternMatcherRootContentKey, IModelConnector> modelConnectorMap;
    private final Map<IModelConnector, PatternMatcherRootContentKey> modelConnectorMapReversed;

    private TableViewer detailsTableViewer;
    private CheckboxTreeViewer patternsTreeViewer;
    private TreeViewer matcherTreeViewer;

    private final RootContent treeViewerRootContent;

    public static PatternsViewerInput patternsViewerInput;

    private FlyoutControlComposite patternsViewerFlyout;
    private FlyoutControlComposite detailsViewerFlyout;

    private IFlyoutPreferences detailsViewerFlyoutPreferences;
    private IFlyoutPreferences patternsViewerFlyoutPreferences;

    private final PatternsViewerFlatContentProvider flatCP;
    private final PatternsViewerFlatLabelProvider flatLP;
    private final PatternsViewerHierarchicalContentProvider hierarchicalCP;
    private final PatternsViewerHierarchicalLabelProvider hierarchicalLP;

    @Inject
    private Injector injector;

    @Inject
    private DetailsViewerUtil tableViewerUtil;

    private String mementoPackagePresentation = "flat";

    private QueryEvaluationHint hints;
    
    public QueryExplorer() {
        modelConnectorMap = new HashMap<PatternMatcherRootContentKey, IModelConnector>();
        modelConnectorMapReversed = new HashMap<IModelConnector, PatternMatcherRootContentKey>();
        patternsViewerInput = new PatternsViewerInput();
        treeViewerRootContent = new RootContent();
        flatCP = new PatternsViewerFlatContentProvider();
        flatLP = new PatternsViewerFlatLabelProvider(patternsViewerInput);
        hierarchicalCP = new PatternsViewerHierarchicalContentProvider();
        hierarchicalLP = new PatternsViewerHierarchicalLabelProvider(patternsViewerInput);
        hints = new QueryEvaluationHint(QueryBackendRegistry.getInstance().getDefaultBackend(), new HashMap<String, Object>());
    }
    
    /**
     * @return the {@link QueryEvaluationHint} instance used when creating matchers
     */
    public QueryEvaluationHint getHints() {
        return hints;
    }
    
    /**
     * @param hints the hints to set
     * @throws NullPointerException if the given hint instance is null
     */
    public void setHints(QueryEvaluationHint hints) {
        Preconditions.checkNotNull(hints);
        this.hints = hints;
    }

    public RootContent getRootContent() {
        return treeViewerRootContent;
    }

    public void load(PatternMatcherRootContentKey key, IModelConnector modelConnector) {
        if (!this.modelConnectorMap.containsKey(key)) {
            this.modelConnectorMap.put(key, modelConnector);
            this.modelConnectorMapReversed.put(modelConnector, key);
            treeViewerRootContent.addPatternMatcherRoot(key, getHints());
        }
    }

    private void unload(PatternMatcherRootContentKey key, IModelConnector modelConnector) {
        this.modelConnectorMapReversed.remove(modelConnector);
        this.modelConnectorMap.remove(key);
        treeViewerRootContent.removePatternMatcherRoot(key);
        modelConnector.unloadModel();
    }

    public IModelConnector getModelConnector(PatternMatcherRootContentKey key) {
        return this.modelConnectorMap.get(key);
    }
    
    public Collection<PatternMatcherRootContentKey> getPatternMatcherRootContentKeys() {
        Set<PatternMatcherRootContentKey> keys = new HashSet<PatternMatcherRootContentKey>();
        keys.addAll(this.modelConnectorMap.keySet());
        return Collections.unmodifiableSet(keys);
    }

    public void unload(IModelConnector modelConnector) {
        if (this.modelConnectorMapReversed.containsKey(modelConnector)) {
            this.unload(modelConnectorMapReversed.get(modelConnector), modelConnector);
        }
    }

    public void unload(PatternMatcherRootContentKey key) {
        if (this.modelConnectorMap.containsKey(key)) {
            this.unload(key, modelConnectorMap.get(key));
        }
    }

    public static QueryExplorer getInstance() {
        // In Juno activeWorkbenchWindow will be null when Eclipse is closing
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return getInstance(activeWorkbenchWindow);
    }

    /**
     * @since 1.0
     */
    public static QueryExplorer getInstance(IWorkbenchWindow activeWorkbenchWindow) {
    	IWorkbenchPart instance = null;
        if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
        	instance = activeWorkbenchWindow.getActivePage().getActivePart();
            if (!(instance instanceof QueryExplorer)) {
            	instance = activeWorkbenchWindow.getActivePage().findView(ID);
            }
        }
        return (QueryExplorer) instance;
    }

    public TreeViewer getMatcherTreeViewer() {
        return matcherTreeViewer;
    }

    public PatternsViewerFlatContentProvider getFlatContentProvider() {
        return flatCP;
    }

    public PatternsViewerFlatLabelProvider getFlatLabelProvider() {
        return flatLP;
    }

    public PatternsViewerHierarchicalContentProvider getHierarchicalContentProvider() {
        return hierarchicalCP;
    }

    public PatternsViewerHierarchicalLabelProvider getHierarchicalLabelProvider() {
        return hierarchicalLP;
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        int detailsState = IFlyoutPreferences.STATE_OPEN;
        int patternsState = IFlyoutPreferences.STATE_COLLAPSED;
        if (memento != null) {
            if (memento.getInteger(DETAILS_VIEW_FLYOUT_STATE) != null) {
                detailsState = memento.getInteger(DETAILS_VIEW_FLYOUT_STATE);
            }
            if (memento.getInteger(PATTERNS_VIEWER_FLYOUT_STATE) != null) {
                patternsState = memento.getInteger(DETAILS_VIEW_FLYOUT_STATE);
            }
            if (memento.getString(PACKAGE_PRESENTATION_STATE) != null) {
                mementoPackagePresentation = memento.getString(PACKAGE_PRESENTATION_STATE);
            }
        }
        detailsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, detailsState, 300);
        patternsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, patternsState, 100);

        ViatraQueryGUIPlugin.getDefault().getPreferenceStore().setDefault(PreferenceConstants.WILDCARD_MODE, true);
    }

    public void clearTableViewer() {
        if (detailsTableViewer.getContentProvider() != null) {
            detailsTableViewer.setInput(null);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        detailsViewerFlyout = new FlyoutControlComposite(parent, SWT.NONE, detailsViewerFlyoutPreferences);
        detailsViewerFlyout.setTitleText("Details / Filters");
        detailsViewerFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_EAST);

        patternsViewerFlyout = new FlyoutControlComposite(detailsViewerFlyout.getClientParent(), SWT.NONE,
                patternsViewerFlyoutPreferences);
        patternsViewerFlyout.setTitleText("Pattern registry");
        patternsViewerFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_WEST);

        matcherTreeViewer = new TreeViewer(patternsViewerFlyout.getClientParent());
        detailsTableViewer = new TableViewer(detailsViewerFlyout.getFlyoutParent(), SWT.FULL_SELECTION);

        // matcherTreeViewer configuration
        matcherTreeViewer.setContentProvider(new ObservableListTreeContentProvider(
                new QueryExplorerObservableFactory(), new QueryExplorerTreeStructureAdvisor()));
        matcherTreeViewer.setLabelProvider(new QueryExplorerLabelProvider());
        matcherTreeViewer.setInput(treeViewerRootContent);
        treeViewerRootContent.setViewer(matcherTreeViewer);
        ColumnViewerToolTipSupport.enableFor(matcherTreeViewer);
        matcherTreeViewer.setComparator(null);

        IObservableValue selection = ViewersObservables.observeSingleSelection(matcherTreeViewer);
        selection.addValueChangeListener(new RootContentSelectionChangeListener());

        DoubleClickListener listener = new DoubleClickListener();
        injector.injectMembers(listener);
        matcherTreeViewer.addDoubleClickListener(listener);

        // patternsViewer configuration
        patternsTreeViewer = new CheckboxTreeViewer(patternsViewerFlyout.getFlyoutParent(), SWT.CHECK | SWT.BORDER
                | SWT.MULTI);
        patternsTreeViewer.setCheckStateProvider(new CheckStateProvider());
        patternsTreeViewer.addCheckStateListener(new CheckStateListener(this));
        setPackagePresentation(mementoPackagePresentation, false);
        patternsTreeViewer.setInput(patternsViewerInput);

        // Create menu manager.
        MenuManager matcherTreeViewerMenuManager = new MenuManager();
        matcherTreeViewerMenuManager.setRemoveAllWhenShown(true);
        matcherTreeViewerMenuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });
        // Create menu for tree viewer
        Menu matcherTreeViewerMenu = matcherTreeViewerMenuManager.createContextMenu(matcherTreeViewer.getControl());
        matcherTreeViewer.getControl().setMenu(matcherTreeViewerMenu);
        getSite().registerContextMenu("org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer.treeViewerMenu",
                matcherTreeViewerMenuManager, matcherTreeViewer);

        MenuManager patternsViewerMenuManager = new MenuManager();
        patternsViewerMenuManager.setRemoveAllWhenShown(true);
        patternsViewerMenuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });
        // Create menu for patterns viewer
        Menu patternsViewerMenu = patternsViewerMenuManager.createContextMenu(patternsTreeViewer.getControl());
        patternsTreeViewer.getControl().setMenu(patternsViewerMenu);
        getSite().registerContextMenu("org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer.patternsViewerMenu",
                patternsViewerMenuManager, patternsTreeViewer);

        // tableView configuration
        Table table = detailsTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        detailsTableViewer.getControl().setLayoutData(gridData);

        // Focus listening and selection providing
        getSite().setSelectionProvider(matcherTreeViewer);

        // removed listener due to new attach feature in https://bugs.eclipse.org/bugs/show_bug.cgi?id=429858
        // initFileListener();
        initPatternsViewerWithGeneratedPatterns();
    }

    private void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    @Override
    public void setFocus() {
        matcherTreeViewer.getControl().setFocus();
    }

    private class RootContentSelectionChangeListener implements IValueChangeListener {

        @Override
        public void handleValueChange(ValueChangeEvent event) {
            Object value = event.getObservableValue().getValue();

            clearTableViewer();

            if (value instanceof PatternMatcherContent) {
                PatternMatcherContent observableMatcher = (PatternMatcherContent) value;
                if (observableMatcher.getMatcher() != null) {
                    tableViewerUtil.prepareFor(observableMatcher, detailsTableViewer);
                    String patternFqn = observableMatcher.getMatcher().getSpecification().getFullyQualifiedName();
                    IQuerySpecification<?> pattern = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(
                            patternFqn);
                    List<PatternComponent> components = null;
                    if (QueryExplorerPatternRegistry.getInstance().isGenerated(pattern)) {
                        components = patternsViewerInput.getGeneratedPatternsRoot().find(patternFqn);
                        components.add(0, patternsViewerInput.getGeneratedPatternsRoot());
                    } else {
                        components = patternsViewerInput.getGenericPatternsRoot().find(patternFqn);
                        components.add(0, patternsViewerInput.getGenericPatternsRoot());
                    }

                    if (components != null) {
                        patternsTreeViewer.setSelection(new TreeSelection(new TreePath(components.toArray())));
                    }
                } else {
                    clearTableViewer();
                }
            } else if (value instanceof PatternMatchContent) {
                PatternMatchContent match = (PatternMatchContent) value;
                tableViewerUtil.prepareFor(match, detailsTableViewer);
            }
        }
    }

    private void initPatternsViewerWithGeneratedPatterns() {
        for (IQuerySpecification<?> pattern : QueryExplorerPatternRegistry.getGeneratedQuerySpecifications()) {
            String patternFqn = pattern.getFullyQualifiedName();
            QueryExplorerPatternRegistry.getInstance().addGeneratedPattern(pattern);

            // check for QE annotation https://bugs.eclipse.org/bugs/show_bug.cgi?id=412700
            Optional<Boolean> checkedValue = QueryExplorerPatternRegistry.getQueryExplorerCheckedValue(pattern);
            Boolean computedCheckedValue = checkedValue.or(false);
            // add to active patterns only if explicitly 
            if (computedCheckedValue) {
                QueryExplorerPatternRegistry.getInstance().addActivePattern(pattern);
            }

            PatternComponent component = patternsViewerInput.getGeneratedPatternsRoot().addComponent(patternFqn);
            component.setCheckedState(computedCheckedValue);
        }

        patternsTreeViewer.refresh();
        patternsViewerInput.getGeneratedPatternsRoot().updateHasChildren();
        patternsViewerInput.getGenericPatternsRoot().setCheckedState(false);
    }
    
    /**
     * Calls refresh on the patterns tree viewer and ensures that "has children" state 
     * on the root elements are correct. 
     */
    public void refreshPatternsViewer() {
    	patternsTreeViewer.refresh();
        patternsViewerInput.getGeneratedPatternsRoot().updateHasChildren();
        patternsViewerInput.getGenericPatternsRoot().updateHasChildren();
    }

    public PatternsViewerInput getPatternsViewerRoot() {
        return patternsViewerInput;
    }

    public CheckboxTreeViewer getPatternsViewer() {
        return patternsTreeViewer;
    }

    /**
     * 
     * @noreference This method is only intended to be used inside query explorer
     */
    public FlyoutControlComposite getPatternsViewerFlyout() {
        return patternsViewerFlyout;
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putInteger(DETAILS_VIEW_FLYOUT_STATE, detailsViewerFlyout.getPreferences().getState());
        memento.putInteger(PATTERNS_VIEWER_FLYOUT_STATE, patternsViewerFlyout.getPreferences().getState());
        memento.putString(PACKAGE_PRESENTATION_STATE, (patternsTreeViewer.getContentProvider() == flatCP) ? "flat"
                : "hierarchical");
    }

    public void setPackagePresentation(String command, boolean update) {

        if (command.contains("flat")) {
            patternsTreeViewer.setContentProvider(flatCP);
            patternsTreeViewer.setLabelProvider(flatLP);
        } else {
            patternsTreeViewer.setContentProvider(hierarchicalCP);
            patternsTreeViewer.setLabelProvider(hierarchicalLP);
        }

        if (update) {
            patternsViewerInput.getGeneratedPatternsRoot().updateHasChildren();
            patternsViewerInput.getGenericPatternsRoot().updateHasChildren();
        }
    }
}
