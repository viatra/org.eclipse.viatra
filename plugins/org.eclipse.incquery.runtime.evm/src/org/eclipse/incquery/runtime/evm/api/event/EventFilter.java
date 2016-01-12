/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.api.event;

/**
 * Interface for filters. Decides whether an event atom should be processed by EVM. The filters are also used as keys
 * for rule instances, so it is important to make sure to make filters comparable using
 * {@linkplain Object#equals(Object)}} and {@linkplain Object#hashCode()}.
 * 
 * @author Abel Hegedus
 *
 */
public interface EventFilter<EventAtom> {

    boolean isProcessable(EventAtom eventAtom);
}
