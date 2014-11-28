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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;
import org.eclipse.viatra.dse.statecode.incrementalgraph.impl.IncrementalGraphHasher;
import org.eclipse.viatra.dse.util.EMFHelper;

public class IncrementalGraphHasherFactory implements IStateSerializerFactory {

    private Collection<EClass> classes;
    private Collection<EStructuralFeature> features;

    private static final int DEFAULT_MAX_UNFOLDING_DEPTH = 5;

    private final int maxUnfoldingDepth;

    public IncrementalGraphHasherFactory(Collection<EPackage> metaModelPackages) {
        this(DEFAULT_MAX_UNFOLDING_DEPTH);
        Collection<EModelElement> modelElements = EMFHelper.getClassesAndReferences(metaModelPackages);
        classes = new ArrayList<EClass>();
        features = new ArrayList<EStructuralFeature>();
        for (EModelElement modelElement : modelElements) {
            if (modelElement instanceof EClassImpl) {
                EClassImpl eClass = (EClassImpl) modelElement;
                classes.add(eClass);
            }
            if (modelElement instanceof EStructuralFeature) {
                EStructuralFeature eStructuralFeature = (EStructuralFeature) modelElement;
                features.add(eStructuralFeature);
            }
        }
    }

    IncrementalGraphHasherFactory(int maxDepth) {
        maxUnfoldingDepth = maxDepth;
    }

    @Override
    public IStateSerializer createStateSerializer(Notifier modelRoot) {
        IncrementalGraphHasher gh;
        try {
            gh = new IncrementalGraphHasher(modelRoot, classes, features);
            return gh;
        } catch (IncQueryException e) {
            e.printStackTrace();
            throw new DSEException("Failed to create incremental graph hasher", e);
        }
    }

    @Override
    public String toString() {
        return "IncGraph";
    }
}
