/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecode.incrementalgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.statecode.incrementalgraph.impl.IncrementalGraphHasher;
import org.eclipse.viatra.dse.util.EMFHelper;
import org.eclipse.viatra.dse.util.EMFHelper.MetaModelElements;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public class IncrementalGraphHasherFactory implements IStateCoderFactory {

    private Collection<EClass> classes;
    private Collection<EStructuralFeature> features;

    private static final int DEFAULT_MAX_UNFOLDING_DEPTH = 5;

    private final int maxUnfoldingDepth;

    public IncrementalGraphHasherFactory(Collection<EPackage> metaModelPackages) {
        this(DEFAULT_MAX_UNFOLDING_DEPTH);
        MetaModelElements metaModelElements = EMFHelper.getAllMetaModelElements(new HashSet<EPackage>(metaModelPackages));
        classes = metaModelElements.classes;
        features = new ArrayList<EStructuralFeature>(metaModelElements.references);
    }

    IncrementalGraphHasherFactory(int maxDepth) {
        maxUnfoldingDepth = maxDepth;
    }

    @Override
    public IStateCoder createStateCoder() {
        try {
            return new IncrementalGraphHasher(classes, features);
        } catch (ViatraQueryException e) {
            throw new DSEException("Failed to create incremental graph hasher", e);
        }
    }

    @Override
    public String toString() {
        return "IncGraph";
    }
}
