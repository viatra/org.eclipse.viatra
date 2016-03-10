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
package org.eclipse.viatra.integration.xcore.mappings;

import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XClassifier;
import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.viatra.integration.xcore.model.XViatraQueryDerivedFeature;

/**
 * An {@link XcoreMapper} extended with the functionality to handle the {@link XViatraQueryDerivedFeature}s too. 
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
public class ViatraQueryXcoreMapper extends XcoreMapper {

    public XViatraQueryDerivedFeatureMapping getMapping(XViatraQueryDerivedFeature xViatraQueryDerivedFeature) {
        return lazyCreateMapping(xViatraQueryDerivedFeature, XViatraQueryDerivedFeatureMapping.class);
    }

    public void unsetMapping(XPackage xPackage) {
        for (XClassifier xClassifier : xPackage.getClassifiers()) {
            if (xClassifier instanceof XClass) {
                XClass xClass = (XClass) xClassifier;
                for (XMember xMember : xClass.getMembers()) {
                    if (xMember instanceof XViatraQueryDerivedFeature) {
                        XViatraQueryDerivedFeature xViatraQueryDerivedFeature = (XViatraQueryDerivedFeature) xMember;
                        remove(xViatraQueryDerivedFeature.eAdapters(), XViatraQueryDerivedFeatureMapping.class);
                    }
                }
            }
        }

        super.unsetMapping(xPackage);
    }
}