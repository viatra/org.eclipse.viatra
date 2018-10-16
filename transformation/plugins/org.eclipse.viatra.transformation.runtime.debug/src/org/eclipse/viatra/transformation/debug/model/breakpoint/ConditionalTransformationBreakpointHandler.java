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
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.debug.communication.ViatraDebuggerException;
import org.eclipse.viatra.transformation.evm.api.Activation;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConditionalTransformationBreakpointHandler implements ITransformationBreakpointHandler, IMatchUpdateListener {
    private static final long serialVersionUID = -5566314274339651637L;
    
    private String patternString;
    private String stringRep = "";
    private boolean matcherChanged = false;
    private boolean enabled = true;
    
    public ConditionalTransformationBreakpointHandler(String patternString) {
        super();
        this.patternString = patternString;
    }

    @Override
    public boolean shouldBreak(Activation<?> a) {
        boolean retval = matcherChanged;
        matcherChanged = false;
        return retval;
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof ConditionalTransformationBreakpointHandler) {
            return ((ConditionalTransformationBreakpointHandler) item).patternString.equals(patternString);
        } else {
            return false;
        }

    }

    @Override
    public void notifyAppearance(IPatternMatch match) {
        matcherChanged = true;
        
    }

    @Override
    public void notifyDisappearance(IPatternMatch match) {
       matcherChanged = true;
    }
        
    public void setEngine(ViatraQueryEngine engine) throws ViatraDebuggerException {
        AdvancedViatraQueryEngine advancedEngine = AdvancedViatraQueryEngine.from(engine);
        
        ViatraQueryMatcher<? extends IPatternMatch> matcher;
        try {
            Iterable<IQuerySpecification<?>> parsePatterns = PatternParserBuilder.instance().parse(patternString).getQuerySpecifications();
            for (IQuerySpecification<?> iQuerySpecification : parsePatterns) {
                matcher = advancedEngine.getMatcher(iQuerySpecification);
                advancedEngine.addMatchUpdateListener(matcher, this, false);
            }
        } catch (ViatraQueryException e) {
            throw new ViatraDebuggerException(e.getMessage());
        }
    }
        
    @Override
    public String toString() {
        return stringRep;
    }
    
    @Override
    public int hashCode() {
        return patternString.hashCode();
    }
    
    @Override
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled(){
        return enabled;
    }

    public void setStringRep(String stringRep) {
        this.stringRep = stringRep;
    }

    public String getPatternString() {
        return patternString;
    }

    public void setPatternString(String patternString) {
        this.patternString = patternString;
        
    }
}
