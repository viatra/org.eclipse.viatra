/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.mappings;

import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XClassifier;
import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.incquery.xcore.model.XIncQueryDerivedFeature;

public class IncQueryXcoreMapper extends XcoreMapper {

    public XIncQueryDerivedFeatureMapping getMapping(XIncQueryDerivedFeature xIncQueryDerivedFeature) {
        return lazyCreateMapping(xIncQueryDerivedFeature, XIncQueryDerivedFeatureMapping.class);
    }

    public void unsetMapping(XPackage xPackage) {
        for (XClassifier xClassifier : xPackage.getClassifiers()) {
            if (xClassifier instanceof XClass) {
                XClass xClass = (XClass) xClassifier;
                for (XMember xMember : xClass.getMembers()) {
                    if (xMember instanceof XIncQueryDerivedFeature) {
                        XIncQueryDerivedFeature xIncQueryDerivedFeature = (XIncQueryDerivedFeature) xMember;
                        remove(xIncQueryDerivedFeature.eAdapters(), XIncQueryDerivedFeatureMapping.class);
                    }
                }
            }
        }

        super.unsetMapping(xPackage);
    }
}