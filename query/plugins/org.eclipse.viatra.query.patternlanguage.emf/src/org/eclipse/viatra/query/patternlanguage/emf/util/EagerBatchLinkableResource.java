/*******************************************************************************
 * Copyright (c) 2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.eclipse.xtext.xbase.resource.BatchLinkableResource;

/**
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class EagerBatchLinkableResource extends BatchLinkableResource {

    public EagerBatchLinkableResource() {
        super();
        setEagerLinking(true);
    }

    @Override
    public boolean isEagerLinking() {
        return true;
    }

}
