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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.incquery.patternlanguage.patternLanguage.RelationType;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.typing.AbstractTypeSystem;
import org.eclipse.incquery.runtime.emf.EMFQueryMetaContext;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.emf.types.JavaTransitiveInstancesKey;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
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
    public IInputKey extractTypeDescriptor(Type type) {
        Preconditions.checkArgument(type instanceof ClassType, NON_EMF_TYPE_ENCOUNTERED, type.getClass());
        if (type instanceof ClassType) {
            final EClassifier classifier = ((ClassType) type).getClassname();
			return classifierToInputKey(classifier);
        }
        // Never executed
        throw new UnsupportedOperationException();
    }

	public static IInputKey classifierToInputKey(final EClassifier classifier) {
		return classifier == null ? null : (
			(classifier instanceof EClass) ? 
				new EClassTransitiveInstancesKey((EClass) classifier) :
					new EDataTypeInSlotsKey((EDataType) classifier)
		);
	}
    
    @Override
    public IInputKey extractColumnDescriptor(RelationType type, int columnIndex) {
        Preconditions.checkArgument(type instanceof ReferenceType, NON_EMF_TYPE_ENCOUNTERED, type.getClass());
        if (type instanceof ReferenceType) {
            final EStructuralFeature feature = ((ReferenceType) type).getRefname();
            return extractColumnDescriptor(feature, columnIndex);
        }
        // Never executed
        throw new UnsupportedOperationException();
    }

	private IInputKey extractColumnDescriptor(final EStructuralFeature feature, int columnIndex) {
		if (0 == columnIndex) {
			return new EClassTransitiveInstancesKey(feature.getEContainingClass());
		} else {
			if (feature instanceof EReference) {
		    	return new EClassTransitiveInstancesKey(((EReference) feature).getEReferenceType());
			} else {
		    	return new EDataTypeInSlotsKey(((EAttribute) feature).getEAttributeType());
			}
		}
	}

    @Override
    public boolean isConformant(IInputKey expectedType, IInputKey actualType) {
        if (expectedType instanceof EClassTransitiveInstancesKey) {
        	if (actualType instanceof EClassTransitiveInstancesKey)
        		return isConform(
        				((EClassTransitiveInstancesKey) expectedType).getEmfKey(), 
        				((EClassTransitiveInstancesKey) actualType).getEmfKey());
        } else if (expectedType instanceof EDataTypeInSlotsKey) {
        	if (actualType instanceof EDataTypeInSlotsKey)
        		return isConform(
        				((EDataTypeInSlotsKey) expectedType).getEmfKey(), 
        				((EDataTypeInSlotsKey) actualType).getEmfKey());
        } else if (expectedType instanceof JavaTransitiveInstancesKey) {
        	if (actualType instanceof JavaTransitiveInstancesKey)
        		return 
        			(((JavaTransitiveInstancesKey) expectedType).getEmfKey()).isAssignableFrom( 
        				((JavaTransitiveInstancesKey) actualType).getEmfKey()
        			);
        }

        //This means inconsistent type settings that is reported elsewhere
        return false;
    }
    
//    @Override
//    public boolean isConformant(Object expectedType, Object actualType) {
//        if (expectedType instanceof EClassifier && actualType instanceof EClassifier) {
//            return isConform((EClassifier) expectedType, (EClassifier) actualType);
//        } else {
//            //This means inconsistent type settings that is reported elsewhere
//            return false;
//        }
//    }
//
    public boolean isConformant(ClassType expectedType, ClassType actualType) {
        final IInputKey expectedClassifier = extractTypeDescriptor(expectedType);
        final IInputKey actualClassifier = extractTypeDescriptor(actualType);
        return isConformant(expectedClassifier, actualClassifier);
    }

    private boolean isConform(final EClassifier expectedClassifier, final EClassifier actualClassifier) {
        if (actualClassifier instanceof EClass) {
            return EcoreUtil2.getCompatibleTypesOf((EClass) actualClassifier).contains(expectedClassifier);
        } else {
            // TODO fix EDataType conformance
            return expectedClassifier.equals(actualClassifier);
        }
    }

    @Override
    public boolean isConformToRelationColumn(IInputKey relationType, int columnIndex, IInputKey columnType) {
    	if (relationType instanceof EStructuralFeatureInstancesKey) {
    		final EStructuralFeature feature = ((EStructuralFeatureInstancesKey) relationType).getEmfKey();
			return isConformant(extractColumnDescriptor(feature, columnIndex), columnType);
    	} else {
    		return false;
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
    public JvmTypeReference toJvmTypeReference(IInputKey type, EObject context) {
        if (type instanceof EClassTransitiveInstancesKey) {
            return emfTypeProvider.getJvmType(((EClassTransitiveInstancesKey) type).getEmfKey(), context);
        } else if (type instanceof EDataTypeInSlotsKey) {
            return emfTypeProvider.getJvmType(((EDataTypeInSlotsKey) type).getEmfKey(), context);
        } else if (type instanceof JavaTransitiveInstancesKey) {
            return typeReferences.getTypeForName(((JavaTransitiveInstancesKey) type).getEmfKey(), context);
        }

        return typeReferences.getTypeForName(Object.class, context);
    }

}
