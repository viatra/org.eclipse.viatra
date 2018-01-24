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
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type;

/**
 * @since 2.0
 */
public final class EMFPatternLanguageScopeHelper {

    public static final String NOT_AN_ENUMERATION_REFERENCE_ERROR = "Not an enumeration reference";

    private EMFPatternLanguageScopeHelper() {
    }

    /**
     * @since 2.0
     */
    public static EEnum calculateEnumerationType(PathExpressionHead head) throws ResolutionException {
        if (head.getTail() == null) {
            throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
        }
        return calculateEnumerationType(head.getTail());
    }

    /**
     * @since 2.0
     */
    public static EEnum calculateEnumerationType(PathExpressionTail tail) throws ResolutionException {
        EClassifier classifier = calculateExpressionType(tail);
        if (classifier instanceof EEnum) {
            return (EEnum) classifier;
        }
        throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
    }

    /**
     * @since 2.0
     */
    public static EClassifier calculateExpressionType(PathExpressionHead head) throws ResolutionException {
        if (head.getTail() == null) {
            throw new ResolutionException(NOT_AN_ENUMERATION_REFERENCE_ERROR);
        }
        return calculateExpressionType(head.getTail());
    }

    /**
     * @since 2.0
     */
    public static EClassifier calculateExpressionType(PathExpressionTail tail) throws ResolutionException {
        if (tail.getTail() == null) {
            Type type = tail.getType();
            return ((ReferenceType) type).getRefname().getEType();
        } else {
            return calculateEnumerationType(tail.getTail());
        }
    }

    /**
     * @since 2.0
     */
    public static PathExpressionHead getExpressionHead(PathExpressionTail tail) {
        if (tail.eContainer() instanceof PathExpressionHead) {
            return (PathExpressionHead) tail.eContainer();
        } else {
            return getExpressionHead((PathExpressionTail) tail.eContainer());
        }
    }
}
