/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.ui.impl

import com.google.common.collect.ImmutableSet
import java.util.Set
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm
import org.eclipse.gef4.zest.core.viewers.GraphViewer
import org.eclipse.gef4.zest.core.widgets.ZestStyles
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.IncQueryEngine
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.addon.viewers.runtime.model.IncQueryViewerDataModel
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Text
import org.eclipse.viatra.transformation.debug.TransformationDebugger.DebuggerActions
import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.addon.viewers.runtime.zest.IncQueryGraphViewers

/**
 * Debugger UI implementation that utilizes the EMF IncQuery viewers framework
 * 
 * @author Peter Lunk
 */
class ViewersDebugger implements IDebugController{
	IncQueryEngine engine
	Set<IQuerySpecification<?>> queries
	DebuggerUI uiRunnable
	boolean actionSet = false
	DebuggerActions action

	new(IncQueryEngine engine, Set<IQuerySpecification<?>> queries) {
		this.engine = engine
		this.queries = queries

		uiRunnable = new DebuggerUI(engine, queries, this)
		new Thread(uiRunnable).start
		
		while(!uiRunnable.initialized){
			Thread.sleep(10);
		}
		
	}
	
	override displayConflictingActivations(Set<Activation<?>> activations) {}

	override displayTransformationContext(Activation<?> act) {
		uiRunnable.d.syncExec(new Runnable(){
			override run() {
				uiRunnable.activationText.text = act.toString
			}
		})
	}

	override getDebuggerAction() {
		actionSet = false
		
		uiRunnable.d.syncExec(new Runnable(){
			override run() {
				uiRunnable.stepButton.enabled = true
				uiRunnable.stepButton.background = uiRunnable.d.getSystemColor(SWT.COLOR_GREEN)
				uiRunnable.continueButton.enabled = true
				uiRunnable.continueButton.background = uiRunnable.d.getSystemColor(SWT.COLOR_GREEN)
			}
		})
		
		
		while(!actionSet){
			Thread.sleep(10)
		}
		
		action
	}

	override getSelectedActivation() {}
	
	static class DebuggerUI implements Runnable{
		IncQueryEngine engine
		Set<IQuerySpecification<?>> queries
		Display d
		Shell shell
		Button stepButton
		Button continueButton
		Text activationText
		ViewersDebugger parent
		public boolean initialized = false
		
		new(IncQueryEngine engine, Set<IQuerySpecification<?>> queries, ViewersDebugger parent){
			this.engine = engine
			this.queries = queries
			this.parent = parent
		}
		
		override run() {
			d = new Display
			shell = new Shell(d)
			shell.text = "VIATRA Debugger Example"
			val layout = new GridLayout(4, false)
			shell.setLayout(layout);
			
			stepButton = new Button(shell, SWT.PUSH)
			stepButton.text = "Step"
			stepButton.toolTipText = "Step to the next transformation rule activation."
			stepButton.layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1)
			stepButton.enabled = false
			stepButton.addSelectionListener(new SelectionAdapter() {
			    override widgetSelected(SelectionEvent e) {
			        parent.action = DebuggerActions.Step
			        parent.actionSet = true
			        stepButton.enabled = false
			        continueButton.enabled = false
			    }
			})
			
			
			continueButton = new Button(shell, SWT.PUSH)
			continueButton.text = "Continue"
			continueButton.toolTipText = "Continue the transformation till the next breakpoint."
			continueButton.enabled = false
			continueButton.layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1) 
			continueButton.addSelectionListener(new SelectionAdapter() {
			    override widgetSelected(SelectionEvent e) {
			        parent.action = DebuggerActions.Continue
			        parent.actionSet = true
			        stepButton.enabled = false
			        continueButton.enabled = false
			    }
			})
			
			val label = new Label(shell, SWT.BORDER);
			label.setText("Next activation:");	
			label.layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1)
			activationText = new Text(shell, SWT.NONE)
			activationText.setText("");
			activationText.layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1) 
			
			val viewer = new GraphViewer(shell, ZestStyles.NONE)
			viewer.control.layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1) 
			viewer.setLayoutAlgorithm(new GridLayoutAlgorithm())
			
			
			val state = IncQueryViewerDataModel.newViewerState(engine, queries, ViewerDataFilter.UNFILTERED,
				ImmutableSet.of(ViewerStateFeature.EDGE))
			IncQueryGraphViewers.bind(viewer, state);
			shell.open();
			initialized = true
			while (!shell.isDisposed()) {
				if (!d.readAndDispatch()) {
					d.sleep();
				}
			}
		}
		
	}

	
}