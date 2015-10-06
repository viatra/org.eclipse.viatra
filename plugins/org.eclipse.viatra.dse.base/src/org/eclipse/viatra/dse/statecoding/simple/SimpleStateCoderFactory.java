/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecoding.simple;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.util.EMFHelper;
import org.eclipse.viatra.dse.util.EMFHelper.MetaModelElements;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SimpleStateCoderFactory implements IStateCoderFactory {

    private MetaModelElements metaModelElements;

    public SimpleStateCoderFactory(Collection<EPackage> metaModelPackages) {
        metaModelElements = EMFHelper.getAllMetaModelElements(new HashSet<EPackage>(metaModelPackages));
    }

    @Override
    public IStateCoder createStateCoder() {
        return new SimpleStateCoder(metaModelElements);
    }

}
