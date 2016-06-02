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
package org.eclipse.viatra.transformation.debug.util;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.viatra.transformation.debug.activator.TransformationDebugActivator;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ConditionalTransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.breakpoint.RuleBreakpoint;

import com.google.common.collect.Lists;

public class BreakpointCacheUtil {
    
    public static String getBreakpointCacheLocation(){
        TransformationDebugActivator activator = new TransformationDebugActivator();
        IPath stateLocation = activator.getStateLocation();
        String location = stateLocation.toString();
        String fileLocation = location+"/persistentbreakpoints.brpkt";
        
        return fileLocation;
    }
    
    public static boolean breakpointCacheExists(){
        File file = new File(getBreakpointCacheLocation());
        return file.exists();
    }
    
    public static IBreakpoint[] filterBreakpoints(IBreakpoint[] iBreakpoints){
        List<IBreakpoint> ret = Lists.newArrayList();
        for (IBreakpoint breakpoint : iBreakpoints) {
            if(breakpoint instanceof RuleBreakpoint || breakpoint instanceof ConditionalTransformationBreakpoint){
                ret.add(breakpoint);
            }
        }
        return ret.toArray(new IBreakpoint[ret.size()]);
        
    }
}
