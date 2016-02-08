/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.emf;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.emf.types.BaseEMFTypeKey;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.InputKeyImplication;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;

/**
 * The singleton meta context information for EMF scopes.
 * 
 * <p> TODO generics? TODO TODO no subtyping between EDataTypes? no EDataTypes metainfo at all?
 * @author Bergmann Gabor
 *
 */
public enum EMFQueryMetaContext implements IQueryMetaContext {
	
	INSTANCE;

	@Override
	public boolean isEnumerable(IInputKey key) {		
		ensureValidKey(key);
		return key.isEnumerable();
//		if (key instanceof JavaTransitiveInstancesKey) 
//			return false;
//		else
//			return true;
	}
	
	@Override
	public boolean isStateless(IInputKey key) {
		ensureValidKey(key);
		if (key instanceof JavaTransitiveInstancesKey) 
			return true;
		else
			return false;
	}

	@Override
	public Map<Set<Integer>, Set<Integer>> getFunctionalDependencies(IInputKey key) {
		ensureValidKey(key);
		if (key instanceof EStructuralFeatureInstancesKey) {
			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) key).getEmfKey();
	    	final Map<Set<Integer>, Set<Integer>> result = 
	    			new HashMap<Set<Integer>, Set<Integer>>();
	    	if (isFeatureMultiplicityToOne(feature))
	    		result.put(Collections.singleton(0), Collections.singleton(1));
	    	if (isFeatureMultiplicityOneTo(feature))
	    		result.put(Collections.singleton(1), Collections.singleton(0));
			return result;
		} else {
			return Collections.emptyMap();
		}
	}
	
	@Override
	public Collection<InputKeyImplication> getImplications(IInputKey implyingKey) {
		ensureValidKey(implyingKey);
		Collection<InputKeyImplication> result = new HashSet<InputKeyImplication>();
		
		if (implyingKey instanceof EClassTransitiveInstancesKey) {
			EClass eClass = ((EClassTransitiveInstancesKey) implyingKey).getEmfKey();
			
			// direct eSuperClasses
			EList<EClass> directSuperTypes = eClass.getESuperTypes();
			for (EClass superType : directSuperTypes) {
				final EClassTransitiveInstancesKey implied = new EClassTransitiveInstancesKey(superType);
				result.add(new InputKeyImplication(implyingKey, implied, Arrays.asList(0)));
			}
		} else if (implyingKey instanceof JavaTransitiveInstancesKey) {
			Class<?> instanceClass = ((JavaTransitiveInstancesKey) implyingKey).getInstanceClass();
			if (instanceClass != null) { // resolution successful
				// direct Java superClass
				Class<?> superclass = instanceClass.getSuperclass();
				if (superclass != null) {
					JavaTransitiveInstancesKey impliedSuper = new JavaTransitiveInstancesKey(superclass);
					result.add(new InputKeyImplication(implyingKey, impliedSuper, Arrays.asList(0)));
				}
				
				// direct Java superInterfaces
				for (Class<?> superInterface : instanceClass.getInterfaces()) {
					if (superInterface != null) {
						JavaTransitiveInstancesKey impliedInterface = new JavaTransitiveInstancesKey(superInterface);
						result.add(new InputKeyImplication(implyingKey, impliedInterface, Arrays.asList(0)));
					}
				}
			}
			
		} else if (implyingKey instanceof EStructuralFeatureInstancesKey) {
			EStructuralFeature feature = ((EStructuralFeatureInstancesKey) implyingKey).getEmfKey();
			
			// source and target type
			final EClass sourceType = featureSourceType(feature);
			final EClassTransitiveInstancesKey impliedSource = new EClassTransitiveInstancesKey(sourceType);
			final EClassifier targetType = featureTargetType(feature);
			final BaseEMFTypeKey<?> impliedTarget = (targetType instanceof EClass) ? 
					new EClassTransitiveInstancesKey((EClass) targetType) :
						new EDataTypeInSlotsKey((EDataType) targetType);
			result.add(new InputKeyImplication(implyingKey, impliedSource, Arrays.asList(0)));
			result.add(new InputKeyImplication(implyingKey, impliedTarget, Arrays.asList(1)));
			
			// opposite
			EReference opposite = featureOpposite(feature);
			if (opposite != null) {
				EStructuralFeatureInstancesKey impliedOpposite = new EStructuralFeatureInstancesKey(opposite);
				result.add(new InputKeyImplication(implyingKey, impliedOpposite, Arrays.asList(1, 0)));
			}
			
			// containment
			// TODO
		} else if (implyingKey instanceof EDataTypeInSlotsKey) {
			EDataType dataType = ((EDataTypeInSlotsKey) implyingKey).getEmfKey();
			
			// instance class of datatype
			// TODO this can have a generation gap! (could be some dynamic EMF impl or whatever)
			Class<?> instanceClass = dataType.getInstanceClass();
			if (instanceClass != null) {
				JavaTransitiveInstancesKey implied = new JavaTransitiveInstancesKey(instanceClass);
				result.add(new InputKeyImplication(implyingKey, implied, Arrays.asList(0)));
			}
		} else {
			illegalInputKey(implyingKey);
		}
		
		return result;
	}

	public void ensureValidKey(IInputKey key) {
		if (! (key instanceof BaseEMFTypeKey<?>) && ! (key instanceof JavaTransitiveInstancesKey))
			illegalInputKey(key);
	}

	public void illegalInputKey(IInputKey key) {
		throw new IllegalArgumentException("The input key " + key + " is not a valid EMF input key.");
	}
	
    public boolean isFeatureMultiplicityToOne(EStructuralFeature feature) {
		return !feature.isMany();
    }

    public boolean isFeatureMultiplicityOneTo(EStructuralFeature typeObject) {
    	if (typeObject instanceof EReference) {
	    	final EReference feature = (EReference)typeObject;
	    	final EReference eOpposite = feature.getEOpposite();
			return feature.isContainment() || (eOpposite != null && !eOpposite.isMany());
    	} else return false;
    }
    
    public EClass featureSourceType(EStructuralFeature feature) {
        return feature.getEContainingClass();
    }
    public EClassifier featureTargetType(EStructuralFeature typeObject) {
        if (typeObject instanceof EAttribute) {
            EAttribute attribute = (EAttribute) typeObject;
            return attribute.getEAttributeType();
        } else if (typeObject instanceof EReference) {
            EReference reference = (EReference) typeObject;
            return reference.getEReferenceType();
        } else
            throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
    }
    public EReference featureOpposite(EStructuralFeature typeObject) {
        if (typeObject instanceof EReference) {
            EReference reference = (EReference) typeObject;
            return reference.getEOpposite();
        } else return null;
    }
	
}
