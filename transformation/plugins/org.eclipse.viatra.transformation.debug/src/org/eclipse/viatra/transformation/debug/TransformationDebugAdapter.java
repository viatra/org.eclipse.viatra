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
package org.eclipse.viatra.transformation.debug;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.viatra.transformation.debug.breakpoints.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.breakpoints.impl.TransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.transformation.debug.controller.impl.ConsoleDebugger;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractTransformationAdapter;

import com.google.common.collect.Lists;

/**
 * Adapter implementation that enables the user to define breakpoints in a VIATRA based event driven transformation.
 * Once one of these breakpoints is reached, the execution of rule activations is suspended. Then the user can either
 * continue the execution or advance the transformation to the next activation.
 * 
 * @author Peter Lunk
 *
 */
public class TransformationDebugAdapter extends AbstractTransformationAdapter {
    private IDebugController ui;
    protected List<ITransformationBreakpoint> breakPoints;
    protected DebuggerActions action = DebuggerActions.Continue;

    public TransformationDebugAdapter(IDebugController usedUI) {
        breakPoints = Lists.newArrayList();
        ui = usedUI;
    }

    public TransformationDebugAdapter(IDebugController usedUI, ITransformationBreakpoint... breakpoints) {
        this.breakPoints = Arrays.asList(breakpoints);
        ui = usedUI;
    }

    public TransformationDebugAdapter(ITransformationBreakpoint... breakpoints) {
        this.breakPoints = Arrays.asList(breakpoints);
        ui = new ConsoleDebugger();
    }

    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        return new TransformationDebuggerIterator(iterator);
    };

    public class TransformationDebuggerIterator implements Iterator<Activation<?>> {
        private final Iterator<Activation<?>> delegatedIterator;

        public TransformationDebuggerIterator(Iterator<Activation<?>> delegatedIterator) {
            this.delegatedIterator = delegatedIterator;
        }

        @Override
        public boolean hasNext() {
            return delegatedIterator.hasNext();
        }

        @Override
        public Activation<?> next() {
            Activation<?> activation = delegatedIterator.next();
            if (activation != null && (hasBreakpoint(activation) || action == DebuggerActions.Step)) {
                action = ui.getDebuggerAction();
            }
            return activation;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Deletion from this iterator is not supported.");

        }

    }

    private boolean hasBreakpoint(Activation<?> activation) {
        for (ITransformationBreakpoint breakpoint : breakPoints) {
            if (breakpoint.shouldBreak(activation)) {
                return true;
            }
        }
        return false;
    }

    public void addBreakPoint(TransformationBreakpoint breakpoint) {
        breakPoints.add(breakpoint);
    }

    public void clearBreakPoints() {
        breakPoints.clear();
    }

    public void removeBreakPoint(TransformationBreakpoint breakPoint) {
        breakPoints.remove(breakPoint);
    }

    public List<ITransformationBreakpoint> getBreakPoints() {
        return breakPoints;
    }
}
