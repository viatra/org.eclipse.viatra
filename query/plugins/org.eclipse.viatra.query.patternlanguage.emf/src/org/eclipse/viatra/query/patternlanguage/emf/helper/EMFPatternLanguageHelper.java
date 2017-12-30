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
package org.eclipse.viatra.query.patternlanguage.emf.helper;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.VQLImportSection;

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

    private EMFPatternLanguageHelper() {}
    
    /**
     * Initializes a new list of package imports defined in a selected pattern model
     * 
     * @param model
     */
    public static List<PackageImport> getAllPackageImports(PatternModel model) {
        return Lists.newArrayList(getPackageImportsIterable(model));
    }

    /**
     * Returns an iterable of package imports in a selected pattern model. If an import package is an unresolvable
     * proxy, it is omitted.
     */
    public static Iterable<PackageImport> getPackageImportsIterable(PatternModel model) {
        VQLImportSection imports = model.getImportPackages();
        if (imports == null) {
            return ImmutableList.of();
        }
        return Iterables.filter(imports.getPackageImport(), pImport -> !pImport.eIsProxy());
    }
    
    /**
     * Returns an iterable of imported EPackages in a selected pattern model. If an import package is an unresolvable
     * proxy, it is omitted.
     *
     * @since 1.3
     */
    public static Iterable<EPackage> getEPackageImportsIterable(PatternModel model) {
        return Iterables.transform(getPackageImportsIterable(model), PackageImport::getEPackage);
    }
}
