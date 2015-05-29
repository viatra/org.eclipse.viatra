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
package org.eclipse.viatra.emf.mwe2integration;

import java.util.List;

/**
 * This interface contains the functions of a composite transformation step. these composite steps contain other transformation steps
 * that are executed in a serialized fashion. These transformation steps send information among each other using different specific parameterized messages. 
 * 
 * @author Peter Lunk
 *
 */
public interface ICompositeStep {

    /**
     * Via this method <link>ITransformationStep</link> objects can be added to the composite step. These transformation steps
     * represent the different simple and complex (loop, sequence) steps in a complex transformation chain (For example: Model to model transformation, code
     * generation...)
     * 
     * @param step
     */
    public void addStep(ITransformationStep step);

    /**
     * Via this method <link>ITransformationStep</link> objects can be queried. These transformation steps
     * represent the different simple and complex (loop, sequence) steps in a complex transformation chain (For example: Model to model transformation, code
     * generation...).
     * 
     * @param step
     */
    public List<ITransformationStep> getStep();


}
