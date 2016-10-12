/**
9 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
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
