/*******************************************************************************
 * Copyright (c) 2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.maven.querybuilder.setup;

import org.eclipse.xtext.xbase.resource.BatchLinkableResource;

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
