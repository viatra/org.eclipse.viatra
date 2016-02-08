/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.debug.notification;

import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

/**
 * Classes that implement this interface can register themselves in the 
 * {@link IncQueryDebugEventSetProcessor} to receive notifications 
 * when the program has been suspended or a step command has ended. 
 * Upon these events, all the registered listeners will be notified 
 * with the corresponding {@link JDIStackFrame}.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public interface IncQueryDebugEventSetListener {

    public void update(JDIStackFrame frame);
    
}
