/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * An error feedback implementation that creates diagnostics in EMF resources.
 * @author Zoltan Ujhelyi
 * @since 1.2
 *
 */
public final class ResourceDiagnosticFeedback implements IErrorFeedback {

    @Override
    public void clearMarkers(Resource resource, final String markerType) {
        if (resource != null) {
            EList<Diagnostic> errors = resource.getErrors();
            Collection<Diagnostic> filter = Collections2.filter(errors, new Predicate<Diagnostic>() {

                @Override
                public boolean apply(Diagnostic input) {
                    return input instanceof EObjectDiagnosticImpl
                            && markerType.contentEquals(((EObjectDiagnosticImpl)input).getCode());
                }
            });
            errors.removeAll(filter);
        }
    }

    @Override
    public void reportError(EObject ctx, String message, String errorCode, Severity severity, String markerType) {
        Resource resource = ctx.eResource();
        if (resource != null) {
            EList<Diagnostic> errors = resource.getErrors();
            errors.add(new EObjectDiagnosticImpl(Severity.ERROR, markerType, message, ctx, null, -1, null));
        }
    }

    @Override
    public void reportErrorNoLocation(EObject ctx, String message, String errorCode, Severity severity,
            String markerType) {
        Resource resource = ctx.eResource();
        if (resource != null) {
            EList<Diagnostic> errors = resource.getErrors();
            errors.add(new EObjectDiagnosticImpl(Severity.ERROR, markerType, message, ctx, null, -1, null));
        }

    }

    @Override
    public void reportError(Resource file, String message, String errorCode, Severity severity, String markerType) {
        EList<Diagnostic> errors = file.getErrors();
        errors.add(new EObjectDiagnosticImpl(Severity.ERROR, markerType, message, null, null, -1, null));
    }

}