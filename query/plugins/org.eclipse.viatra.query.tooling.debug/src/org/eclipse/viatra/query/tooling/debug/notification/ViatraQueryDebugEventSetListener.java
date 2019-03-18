/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.debug.notification;

import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

/**
 * Classes that implement this interface can register themselves in the 
 * {@link ViatraDebugEventSetProcessor} to receive notifications 
 * when the program has been suspended or a step command has ended. 
 * Upon these events, all the registered listeners will be notified 
 * with the corresponding {@link JDIStackFrame}.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
@SuppressWarnings("restriction")
public interface ViatraQueryDebugEventSetListener {

    public void update(JDIStackFrame frame);
    
}
