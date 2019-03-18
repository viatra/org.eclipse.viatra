/**
9 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import java.io.Serializable;

import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Interface that defines transformation breakpoints
 * @author Peter Lunk
 *
 */
public interface ITransformationBreakpointHandler extends Serializable{

    public boolean isEnabled();

    public boolean shouldBreak(Activation<?> activation);

    public void setEnabled(boolean b);
}
