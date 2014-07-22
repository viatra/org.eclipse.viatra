/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.helper;

import java.util.List;

import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.XImportSection;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Helper functions for dealing with the EMF Pattern Language models.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageHelper {

    /**
     * Initializes a new list of package imports defined in a selected pattern model
     * 
     * @param model
     * @return
     */
    public static List<PackageImport> getAllPackageImports(PatternModel model) {
        return Lists.newArrayList(getPackageImportsIterable(model));
    }

    /**
     * Returns an iterable of package imports in a selected pattern model. If an import package is an unresolvable
     * proxy, it is omitted.
     * 
     * @param model
     * @return
     */
    public static Iterable<PackageImport> getPackageImportsIterable(PatternModel model) {
        XImportSection imports = model.getImportPackages();
        if (imports == null) {
        	return ImmutableList.of();
        }
		return Iterables.filter(imports.getPackageImport(),
                new Predicate<PackageImport>() {

                    @Override
                    public boolean apply(PackageImport pImport) {
                        return !pImport.eIsProxy();
                    }
                });
    }
}
