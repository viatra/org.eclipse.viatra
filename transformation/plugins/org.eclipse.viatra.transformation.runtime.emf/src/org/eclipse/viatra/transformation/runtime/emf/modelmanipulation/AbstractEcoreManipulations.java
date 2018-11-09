/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * @author Gabor Bergmann
 * @since 2.1
 *
 */
public abstract class AbstractEcoreManipulations<RootContainer, ModelObject> implements IEcoreManipulations<RootContainer, ModelObject> {

    private static final EClass E_OBJECT_CLASS = 
            EcorePackage.eINSTANCE.getEObject();
    
    protected boolean isEObjectClass(EClass type) {
        // TODO does this have to be generalized for dynamic EMF? probably not
        return type == E_OBJECT_CLASS;
    }
}
