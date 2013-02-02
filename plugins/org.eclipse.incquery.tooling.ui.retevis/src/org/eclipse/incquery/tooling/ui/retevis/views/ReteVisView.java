/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.retevis.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.incquery.tooling.ui.retevis.Activator;
import org.eclipse.incquery.tooling.ui.retevis.theme.ColorTheme;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author istvanrath
 * 
 */
public class ReteVisView extends ViewPart implements IZoomableWorkbenchPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.eclipse.incquery.tooling.ui.retevis.views.ReteVisView";

    private GraphViewer graphViewer;
    private ColorTheme theme;

    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return graphViewer;
    }

    /**
     * The constructor.
     */
    public ReteVisView() {
    }

    
    private Action refreshGraph = new Action("Refresh Graph") {
        @Override
        public void run() {
            redraw();
        }
    };
    
    private Action clearGraph = new Action("Clear Graph") {
        @Override
        public void run() {
            graphViewer.setInput(null);
        }
    };
    
//    private Action saveFile = new Action("Save Graph as Image") {
//        @Override
//        public void run() {
//            FileDialog save = new FileDialog(PlatformUI.getWorkbench()
//                    .getDisplay().getActiveShell(), SWT.SAVE);
//            save.setFilterExtensions(new String[] { "*.png" });
//            save.setFilterNames(new String[] { "PNG File" });
//            save.setFileName("rete.png");
//            String filename = save.open();
//            graphViewer.saveImage(filename, SWT.IMAGE_PNG);
//        }
//    };
    
    public void redraw() {
        if (graphViewer != null) {
            graphViewer.applyLayout();
        }
    }
    
    private void initializeActions() {
        refreshGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/refresh.gif"));
        clearGraph.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/clear.gif"));
    }
    
    private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.removeAll();
        mgr.add(refreshGraph);
        mgr.add(clearGraph);
        mgr.update(true);
        
        IMenuManager mmgr = getViewSite().getActionBars().getMenuManager();
        mmgr.removeAll();
        mmgr.add(createLayoutMenu());
        
    }
    
    private MenuManager createLayoutMenu() {
        MenuManager mgr = new MenuManager("Layout");
        mgr.add(createLayoutAction("Tree", new TreeLayoutAlgorithm()));
        mgr.add(createLayoutAction("Spring", new SpringLayoutAlgorithm()));
        mgr.add(createLayoutAction("Radial", new RadialLayoutAlgorithm()));
        mgr.add(createLayoutAction("SpaceTree", new SpaceTreeLayoutAlgorithm()));
        SugiyamaLayoutAlgorithm sugiyamaAlgorithm = new SugiyamaLayoutAlgorithm();
        HorizontalShiftAlgorithm shiftAlgorithm = new HorizontalShiftAlgorithm();
        mgr.add(createLayoutAction("Sugiyama (unstable)",new CompositeLayoutAlgorithm(new LayoutAlgorithm[] {sugiyamaAlgorithm, shiftAlgorithm })));
        return mgr;
    }
    
    private Action createLayoutAction(final String name, final LayoutAlgorithm lay) {
        return new Action(name) {
          @Override
          public void run() {
            graphViewer.setLayoutAlgorithm(lay);
            redraw();
          }  
        };
    }
    
    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        // initialize Zest viewer
        graphViewer = new GraphViewer(parent, SWT.BORDER);
        graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        graphViewer.setContentProvider(new ZestReteContentProvider());
        ZestReteLabelProvider labelProvider = new ZestReteLabelProvider();
        Display display = parent.getDisplay();
        theme = new ColorTheme(display);
        labelProvider.setColors(theme);
        graphViewer.setLabelProvider(labelProvider);
        
        initializeActions();
        createToolbar();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        site.getPage().addSelectionListener(new ISelectionListener() {

            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (selection instanceof IStructuredSelection) {
                    IStructuredSelection sel = (IStructuredSelection) selection;
                    Object o = sel.getFirstElement();
                    if (o instanceof ObservablePatternMatcher) {
                        ObservablePatternMatcher pm = (ObservablePatternMatcher) o;
                        // String patternFqn = pl.getFullPatternNamePrefix()+"."+pl.getPatternNameFragment();
                        try {
                            ReteBoundary<Pattern> rb = pm.getMatcher().getEngine().getReteEngine().getBoundary();
                            ((ZestReteLabelProvider) graphViewer.getLabelProvider()).setRb(rb);
                            graphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm());
                            graphViewer.setInput(rb.getHeadContainer());
                        } catch (IncQueryException e) {
                            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
                        }
                    }
                }
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {

    }

    @Override
    public void dispose() {
        if (theme != null) {
            theme.dispose();
        }
        super.dispose();
    }
}