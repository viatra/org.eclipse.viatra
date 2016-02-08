/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.mwe2.eventdriven;

import org.eclipse.viatra.transformation.evm.api.Scheduler;

/**
 * Interface that defines functions which are used when an <link>IController</link> object also wraps an EVM
 * <link>Scheduler</link>.
 * 
 * @author Peter Lunk
 *
 * @param <T>
 */
public interface ISchedulerController<T extends Scheduler> extends IController {
    public void setScheduler(T scheduler);
}
