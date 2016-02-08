/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

/**
 * A single metamodel provider instance, used by the {@link CompoundMetamodelProviderService} to delegate the selection
 * of metamodels. The interface extends {@link IMetamodelProvider} by priority and name definition.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.0
 */
public interface IMetamodelProviderInstance extends IMetamodelProvider {

    /**
     * Get a textual identifier for the metamodel provider instance
     * 
     * @return a non-null textual identifier; should be unique over all possible implementations
     */
    public String getIdentifier();

    /**
     * A default priority for the provider instance; a lower number means a higher priority. The priority may be
     * overriden through {@link CompoundMetamodelProviderService}.
     * 
     * @return a non-negative priority number
     */
    public int getPriority();
}
