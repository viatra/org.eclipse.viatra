/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

/**
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class NACOperation extends CheckOperation implements IPatternMatcherOperation {

    private final CallInformation information;
    private final VolatileModifiableMaskedTuple maskedTuple;
    private IQueryResultProvider matcher;

    /**
     * @since 1.7
     */
    public NACOperation(CallInformation information) {
        super();
        this.information = information;
        this.maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        super.onInitialize(frame, context);
        maskedTuple.updateTuple(frame);
        matcher = context.getMatcher(information.getReference());
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) {
        return matcher.getOneArbitraryMatch(information.getParameterMask(), maskedTuple) == null;
    }
    
    
    @Override
    public String toString() {
        return "check     neg find "+information.toString();
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return information.getVariablePositions();
    }


}
