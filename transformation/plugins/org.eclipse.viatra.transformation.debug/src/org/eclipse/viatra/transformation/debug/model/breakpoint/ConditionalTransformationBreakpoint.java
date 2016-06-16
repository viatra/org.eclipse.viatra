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

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Modifiers;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugElement;
import org.eclipse.viatra.transformation.debug.util.patternparser.PatternParser;
import org.eclipse.viatra.transformation.debug.util.patternparser.PatternParsingResults;
import org.eclipse.viatra.transformation.evm.api.Activation;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * Class that can be used to specify breakpoint rules via query specifications. It is mainly used by the VIATRA
 * {@link org.eclipse.viatra.transformation.debug.TransformationDebugListener} class.
 * 
 * @author Peter Lunk
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConditionalTransformationBreakpoint extends Breakpoint implements ITransformationBreakpoint, IMatchUpdateListener {
    private String patternString;
    private String stringRep;
    private boolean matcherChanged = false;
    
    
    
    public ConditionalTransformationBreakpoint() {
        super();
    }
    
    public ConditionalTransformationBreakpoint(String patternString) {
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
    public String getModelIdentifier() {
        return TransformationDebugElement.MODEL_ID;
    }

    @Override
    public String getMarkerIdentifier() {
        return CONDITIONAL;
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof ConditionalTransformationBreakpoint) {
            return ((ConditionalTransformationBreakpoint) item).patternString.equals(patternString);
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
    
    @Override
    public void setMarker(IMarker marker) throws CoreException {
        super.setMarker(marker);
            if(patternString != null){
                marker.setAttribute("pattern", patternString);
            }else{
                patternString = marker.getAttribute("pattern", "");
            }
    }
    
    public void setEngine(ViatraQueryEngine engine) {
        AdvancedViatraQueryEngine advancedEngine = AdvancedViatraQueryEngine.from(engine);
        stringRep = "Conditional Transformation Breakpoint - ";
        ViatraQueryMatcher<? extends IPatternMatch> matcher;
        try {
            List<IQuerySpecification<?>> parsePatterns = parsePatterns();
            for (IQuerySpecification<?> iQuerySpecification : parsePatterns) {
                stringRep += "Query specification name: "+iQuerySpecification.getFullyQualifiedName();
                matcher = advancedEngine.getMatcher(iQuerySpecification);
                advancedEngine.addMatchUpdateListener(matcher, this, false);
            }
        } catch (ViatraQueryException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
        }
    }
    
    private List<IQuerySpecification<?>> parsePatterns(){
        Injector injector = EMFPatternLanguageActivator.getInstance().getInjector(EMFPatternLanguageActivator.ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE);
        PatternParser parser = injector.getInstance(PatternParser.class);
        PatternParsingResults results = parser.parse(patternString);
        if(!results.hasError()){
            SpecificationBuilder builder = new SpecificationBuilder();
            List<Pattern> patterns = results.getPatterns();
            List<IQuerySpecification<?>> specList = Lists.newArrayList();
            for (Pattern pattern : patterns) {
                boolean isPrivate = false;
                EList<Modifiers> modifiers = pattern.getModifiers();
                for (Modifiers modifier : modifiers) {
                    isPrivate = modifier.isPrivate();
                }
                try {
                    IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> spec = builder.getOrCreateSpecification(pattern);
                    if(!isPrivate){
                        specList.add(spec);
                    }
                } catch (ViatraQueryException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                }
            }
            return specList;
        }
        return Lists.newArrayList();
    }
    
    @Override
    public String toString() {
        return stringRep;
    }
    
    @Override
    public int hashCode() {
        return patternString.hashCode();
    }

}
