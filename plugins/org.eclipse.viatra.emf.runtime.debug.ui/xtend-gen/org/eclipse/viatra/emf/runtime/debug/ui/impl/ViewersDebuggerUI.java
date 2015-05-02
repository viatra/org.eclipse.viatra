/**
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.emf.runtime.debug.ui.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.zest.IncQueryGraphViewers;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.viatra.emf.runtime.debug.TransformationDebugger;
import org.eclipse.viatra.emf.runtime.debug.ui.IDebuggerUI;
import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * Debugger UI implementation that utilizes the EMF IncQuery viewers framework
 */
@SuppressWarnings("all")
public class ViewersDebuggerUI implements IDebuggerUI {
  public static class DebuggerUI implements Runnable {
    private IncQueryEngine engine;
    
    private Set<IQuerySpecification<?>> queries;
    
    private Display d;
    
    private Shell shell;
    
    private Button stepButton;
    
    private Button continueButton;
    
    private Text activationText;
    
    private ViewersDebuggerUI parent;
    
    public boolean initialized = false;
    
    public DebuggerUI(final IncQueryEngine engine, final Set<IQuerySpecification<?>> queries, final ViewersDebuggerUI parent) {
      this.engine = engine;
      this.queries = queries;
      this.parent = parent;
    }
    
    @Override
    public void run() {
      Display _display = new Display();
      this.d = _display;
      Shell _shell = new Shell(this.d);
      this.shell = _shell;
      this.shell.setText("VIATRA Debugger Example");
      final GridLayout layout = new GridLayout(4, false);
      this.shell.setLayout(layout);
      Button _button = new Button(this.shell, SWT.PUSH);
      this.stepButton = _button;
      this.stepButton.setText("Step");
      this.stepButton.setToolTipText("Step to the next transformation rule activation.");
      GridData _gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
      this.stepButton.setLayoutData(_gridData);
      this.stepButton.setEnabled(false);
      this.stepButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(final SelectionEvent e) {
          DebuggerUI.this.parent.action = TransformationDebugger.DebuggerActions.Step;
          DebuggerUI.this.parent.actionSet = true;
          DebuggerUI.this.stepButton.setEnabled(false);
          DebuggerUI.this.continueButton.setEnabled(false);
        }
      });
      Button _button_1 = new Button(this.shell, SWT.PUSH);
      this.continueButton = _button_1;
      this.continueButton.setText("Continue");
      this.continueButton.setToolTipText("Continue the transformation till the next breakpoint.");
      this.continueButton.setEnabled(false);
      GridData _gridData_1 = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
      this.continueButton.setLayoutData(_gridData_1);
      this.continueButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(final SelectionEvent e) {
          DebuggerUI.this.parent.action = TransformationDebugger.DebuggerActions.Continue;
          DebuggerUI.this.parent.actionSet = true;
          DebuggerUI.this.stepButton.setEnabled(false);
          DebuggerUI.this.continueButton.setEnabled(false);
        }
      });
      final Label label = new Label(this.shell, SWT.BORDER);
      label.setText("Next activation:");
      GridData _gridData_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
      label.setLayoutData(_gridData_2);
      Text _text = new Text(this.shell, SWT.NONE);
      this.activationText = _text;
      this.activationText.setText("");
      GridData _gridData_3 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
      this.activationText.setLayoutData(_gridData_3);
      final GraphViewer viewer = new GraphViewer(this.shell, ZestStyles.NONE);
      Control _control = viewer.getControl();
      GridData _gridData_4 = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
      _control.setLayoutData(_gridData_4);
      GridLayoutAlgorithm _gridLayoutAlgorithm = new GridLayoutAlgorithm();
      viewer.setLayoutAlgorithm(_gridLayoutAlgorithm);
      ImmutableSet<ViewerState.ViewerStateFeature> _of = ImmutableSet.<ViewerState.ViewerStateFeature>of(ViewerState.ViewerStateFeature.EDGE);
      final ViewerState state = IncQueryViewerDataModel.newViewerState(this.engine, this.queries, ViewerDataFilter.UNFILTERED, _of);
      IncQueryGraphViewers.bind(viewer, state);
      this.shell.open();
      this.initialized = true;
      while ((!this.shell.isDisposed())) {
        boolean _readAndDispatch = this.d.readAndDispatch();
        boolean _not = (!_readAndDispatch);
        if (_not) {
          this.d.sleep();
        }
      }
    }
  }
  
  private IncQueryEngine engine;
  
  private Set<IQuerySpecification<?>> queries;
  
  private ViewersDebuggerUI.DebuggerUI uiRunnable;
  
  private boolean actionSet = false;
  
  private TransformationDebugger.DebuggerActions action;
  
  public ViewersDebuggerUI(final IncQueryEngine engine, final Set<IQuerySpecification<?>> queries) {
    try {
      this.engine = engine;
      this.queries = queries;
      ViewersDebuggerUI.DebuggerUI _debuggerUI = new ViewersDebuggerUI.DebuggerUI(engine, queries, this);
      this.uiRunnable = _debuggerUI;
      Thread _thread = new Thread(this.uiRunnable);
      _thread.start();
      while ((!this.uiRunnable.initialized)) {
        Thread.sleep(10);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void displayConflictingActivations(final Set<Activation<?>> activations) {
  }
  
  @Override
  public void displayTransformationContext(final Activation<?> act) {
    this.uiRunnable.d.syncExec(new Runnable() {
      @Override
      public void run() {
        String _string = act.toString();
        ViewersDebuggerUI.this.uiRunnable.activationText.setText(_string);
      }
    });
  }
  
  @Override
  public TransformationDebugger.DebuggerActions getDebuggerAction() {
    try {
      TransformationDebugger.DebuggerActions _xblockexpression = null;
      {
        this.actionSet = false;
        this.uiRunnable.d.syncExec(new Runnable() {
          @Override
          public void run() {
            ViewersDebuggerUI.this.uiRunnable.stepButton.setEnabled(true);
            Color _systemColor = ViewersDebuggerUI.this.uiRunnable.d.getSystemColor(SWT.COLOR_GREEN);
            ViewersDebuggerUI.this.uiRunnable.stepButton.setBackground(_systemColor);
            ViewersDebuggerUI.this.uiRunnable.continueButton.setEnabled(true);
            Color _systemColor_1 = ViewersDebuggerUI.this.uiRunnable.d.getSystemColor(SWT.COLOR_GREEN);
            ViewersDebuggerUI.this.uiRunnable.continueButton.setBackground(_systemColor_1);
          }
        });
        while ((!this.actionSet)) {
          Thread.sleep(10);
        }
        _xblockexpression = this.action;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public Activation<?> getSelectedActivation() {
    return null;
  }
}
