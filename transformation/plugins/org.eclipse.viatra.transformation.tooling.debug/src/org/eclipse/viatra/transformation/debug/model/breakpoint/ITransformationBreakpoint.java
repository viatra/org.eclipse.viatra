/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import java.io.Serializable;

import org.eclipse.debug.core.model.IBreakpoint;

/**
 * Interface that defines transformation breakpoints
 * @author Peter Lunk
 *
 */
public interface ITransformationBreakpoint extends IBreakpoint, Serializable{
    public static final String MODEL_ID = "org.eclipse.viatra.transformation.debug.model";
    public static final String NON_PERSISTENT = MODEL_ID;
    public static final String RULE = "org.eclipse.viatra.transformation.debug.model.rule";
    public static final String CONDITIONAL = "org.eclipse.viatra.transformation.debug.model.conditional";
    
    public String getMarkerIdentifier();   
    public ITransformationBreakpointHandler getHandler();
}
