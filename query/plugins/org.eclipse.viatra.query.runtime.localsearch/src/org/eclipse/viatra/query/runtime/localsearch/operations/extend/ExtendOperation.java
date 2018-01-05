/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Iterator;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

/**
 * An operation that can be used to enumerate all possible values for a single position based on a constraint
 * @author Zoltan Ujhelyi, Akos Horvath
 * 
 */
public abstract class ExtendOperation implements ISearchOperation {

    protected int position;
    protected Iterator<?> it;

    /**
     * @param position the frame position all values are to be added
     */
    public ExtendOperation(int position) {
        super();
        this.position = position;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) {
        frame.setValue(position, null);
        it = null;

    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) {
        if (it.hasNext()) {
            Object next = it.next();
            frame.setValue(position, next);
            return true;
        } else {
            return false;
        }
    }

}
