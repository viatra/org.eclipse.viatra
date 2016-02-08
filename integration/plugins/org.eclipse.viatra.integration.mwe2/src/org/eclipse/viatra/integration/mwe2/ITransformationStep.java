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
package org.eclipse.viatra.integration.mwe2;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;

/**
 * Interface, which represents a step in a transformation chain. These transformation steps can contain
 *  various batch natured functionalities.
 * 
 * @author Peter Lunk
 *
 */
public interface ITransformationStep{

    /**
     * In this method the transformation step can be initialized. The workflow context is handed to the transformation
     * step
     * 
     * @param ctx
     *            The context of the workflow, which can contain various workflow-specific resources.
     */
    public void initialize(IWorkflowContext ctx);

    /**
     * Defines the added functionality of the <link>ITransformationStep</link> object
     */
    public void execute();

    /**
     * If the transformation step contains any resources that need to be disposed of, it can be done here.
     */
    public void dispose();
}
