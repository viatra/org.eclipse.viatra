/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.transactions.specific.job;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;

/**
 * @author Abel Hegedus
 *
 */
public interface EventAtomEditingDomainProvider<EventAtom> {

    EditingDomain findEditingDomain(Activation<? extends EventAtom> activation, Context context);
}
