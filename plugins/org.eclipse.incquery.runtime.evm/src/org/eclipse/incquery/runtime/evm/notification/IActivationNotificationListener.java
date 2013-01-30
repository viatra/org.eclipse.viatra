/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.evm.notification;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.evm.api.ActivationState;

/**
 * The interface is used to observe the changes in the collection of activations. <br/>
 * <br/>
 * An implementing class is for example the Agenda which is called back by the RuleInstance
 * when those have updated the activations after an EMF operation.
 * 
 * @author Tamas Szabo
 * 
 */
public interface IActivationNotificationListener {

    void activationChanged(final Activation<? extends IPatternMatch> activation, final ActivationState oldState,
            final ActivationLifeCycleEvent event);

}
