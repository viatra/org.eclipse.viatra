/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.types;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.incquery.patternlanguage.patternLanguage.RelationType;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.typing.AbstractTypeSystem;
import org.eclipse.incquery.runtime.emf.EMFQueryMetaContext;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFTypeSystem extends AbstractTypeSystem {

    private static final String NON_EMF_TYPE_ENCOUNTERED = "EMF Type System only supports EMF Types but %s found.";

    @Inject
    private IEMFTypeProvider emfTypeProvider;
    @Inject
    private TypeReferences typeReferences;

    @Inject
    public EMFTypeSystem(Logger logger) {
        super(EMFQueryMetaContext.INSTANCE);
    }

    @Override
    public EClassifier extractTypeDescriptor(Type type) {
        Preconditions.checkArgument(type instanceof ClassType, NON_EMF_TYPE_ENCOUNTERED, type.getClass());
        if (type instanceof ClassType) {
            return ((ClassType) type).getClassname();
        }
        // Never executed
        throw new UnsupportedOperationException();
    }

    @Override
    public EClassifier extractSourceTypeDescriptor(RelationType type) {
        Preconditions.checkArgument(type instanceof ReferenceType, NON_EMF_TYPE_ENCOUNTERED, type.getClass());
        if (type instanceof ReferenceType) {
            return ((ReferenceType) type).getRefname().getEContainingClass();
        }
        // Never executed
        throw new UnsupportedOperationException();
    }

    @Override
    public EClassifier extractTargetTypeDescriptor(RelationType type) {
        Preconditions.checkArgument(type instanceof ReferenceType, NON_EMF_TYPE_ENCOUNTERED, type.getClass());
        if (type instanceof ReferenceType) {
            return ((ReferenceType) type).getRefname().getEType();
        }
        // Never executed
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConformant(Object expectedType, Object actualType) {
        if (expectedType instanceof EClassifier && actualType instanceof EClassifier) {
            return isConform((EClassifier) expectedType, (EClassifier) actualType);
        } else {
            //This means inconsistent type settings that is reported elsewhere
            return false;
        }
    }

    public boolean isConformant(ClassType expectedType, ClassType actualType) {
        final EClassifier expectedClassifier = extractTypeDescriptor(expectedType);
        final EClassifier actualClassifier = extractTypeDescriptor(actualType);
        return isConform(expectedClassifier, actualClassifier);
    }

    private boolean isConform(final EClassifier expectedClassifier, final EClassifier actualClassifier) {
        if (actualClassifier instanceof EClass) {
            return EcoreUtil2.getCompatibleTypesOf((EClass) actualClassifier).contains(expectedClassifier);
        } else {
            // TODO fix EDataType conformance
            return expectedClassifier.equals(actualClassifier);
        }
    }

    public boolean isConformToRelationSource(ReferenceType relationType, ClassType sourceType) {
        final EStructuralFeature featureType = relationType.getRefname();
        final EClassifier classifier = sourceType.getClassname();
        final EClass sourceClass = featureType.getEContainingClass();
        return isConform(sourceClass, classifier);
    }

    public boolean isConformToRelationTarget(ReferenceType relationType, ClassType targetType) {
        final EStructuralFeature featureType = relationType.getRefname();
        final EClassifier classifier = targetType.getClassname();
        final EClassifier targetClassifier = featureType.getEType();

        return isConform(targetClassifier, classifier);
    }

    @Override
    public JvmTypeReference toJvmTypeReference(Object type, EObject context) {
        if (type instanceof EClassifier) {
            return emfTypeProvider.getJvmType(((EClassifier) type), context);
        }
        return typeReferences.getTypeForName(Object.class, context);
    }

}
