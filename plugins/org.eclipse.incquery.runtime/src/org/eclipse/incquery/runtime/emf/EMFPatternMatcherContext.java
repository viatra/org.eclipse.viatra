/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.emf;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherContext;

/**
 * TODO generics? TODO TODO no subtyping between EDataTypes? no EDataTypes metainfo at all?
 *
 * @author Bergmann Gábor
 */
public class EMFPatternMatcherContext implements IPatternMatcherContext {
	
    public static final EMFPatternMatcherContext STATIC_INSTANCE = new EMFPatternMatcherContext();

    /**
     * 
     */
    private static final String INVALID_TYPE_ERROR = "typeObject has invalid type ";
    private Logger logger = Logger.getLogger(EMFPatternMatcherContext.class);

    /**
     * Initializes an {@link EMFPatternMatcherContext} instance with a default logger
     */
    public EMFPatternMatcherContext() {
        super();
    }

    public EMFPatternMatcherContext(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public EdgeInterpretation edgeInterpretation() {
        return EdgeInterpretation.BINARY;
    }

    @Override
    public GeneralizationQueryDirection allowedGeneralizationQueryDirection() {
        return GeneralizationQueryDirection.SUPERTYPE_ONLY_SMART_NOTIFICATIONS;
    }

    @Override
    public Collection<? extends Object> enumerateDirectSupertypes(Object typeObject) {
        if (typeObject == null)
            return null;
        if (typeObject instanceof EClass || typeObject instanceof EDataType)
            return enumerateDirectUnarySupertypes(typeObject);
        if (typeObject instanceof EStructuralFeature)
            return enumerateDirectBinaryEdgeSupertypes(typeObject);
        else
            throw new IllegalArgumentException(INVALID_TYPE_ERROR + typeObject.getClass().getName());
    }

    @Override
    public Collection<? extends Object> enumerateDirectSubtypes(Object typeObject) {
        if (typeObject == null)
            return null;
        if (typeObject instanceof EClass || typeObject instanceof EDataType)
            return enumerateDirectUnarySubtypes(typeObject);
        if (typeObject instanceof EStructuralFeature)
            return enumerateDirectBinaryEdgeSubtypes(typeObject);
        else
            throw new IllegalArgumentException(INVALID_TYPE_ERROR + typeObject.getClass().getName());
    }

    @Override
    public boolean isUnaryType(Object typeObject) {
        return typeObject instanceof EClassifier;
    }

    @Override
    public Collection<? extends Object> enumerateDirectUnarySubtypes(Object typeObject) {
        if (typeObject instanceof EClass)
            throw new UnsupportedOperationException(
                    "EMF patternmatcher context only supports querying of supertypes, not subtypes.");
        // return contextMapping.retrieveSubtypes((EClass)typeObject);
        else if (typeObject instanceof EDataType) {
            return Collections.emptyList();// no subtyping between EDataTypes?
        } else
            throw new IllegalArgumentException(INVALID_TYPE_ERROR + typeObject.getClass().getName());
    }

    @Override
    public Collection<? extends Object> enumerateDirectUnarySupertypes(Object typeObject) {
        if (typeObject instanceof EClass)
            return ((EClass) typeObject).getESuperTypes();
        else if (typeObject instanceof EDataType) {
            return Collections.emptyList();// no subtyping between EDataTypes?
        } else
            throw new IllegalArgumentException(INVALID_TYPE_ERROR + typeObject.getClass().getName());
    }

    @Override
    public boolean isBinaryEdgeType(Object typeObject) {
        return typeObject instanceof EStructuralFeature;
    }

    @Override
    public boolean isBinaryEdgeMultiplicityToOne(Object typeObject) {
    	if (typeObject instanceof EStructuralFeature) {
	    	final EStructuralFeature feature = (EStructuralFeature)typeObject;
			return !feature.isMany();
    	} else return false;
    }

    @Override
    public boolean isBinaryEdgeMultiplicityOneTo(Object typeObject) {
    	if (typeObject instanceof EReference) {
	    	final EReference feature = (EReference)typeObject;
	    	final EReference eOpposite = feature.getEOpposite();
			return feature.isContainment() || (eOpposite != null && !eOpposite.isMany());
    	} else return false;
    }


    @Override
    public Collection<? extends Object> enumerateDirectBinaryEdgeSubtypes(Object typeObject) {
        return Collections.emptyList();// no subtyping between structural features
    }

    @Override
    public Collection<? extends Object> enumerateDirectBinaryEdgeSupertypes(Object typeObject) {
        return Collections.emptyList();// no subtyping between structural features
    }

    @Override
    public Collection<? extends Object> enumerateDirectTernaryEdgeSubtypes(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTernaryEdgeType(Object typeObject) {
        return false;
    }

    @Override
    public boolean isTernaryEdgeMultiplicityOneTo(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTernaryEdgeMultiplicityToOne(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Object> enumerateDirectTernaryEdgeSupertypes(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object binaryEdgeSourceType(Object typeObject) {
        EStructuralFeature feature = (EStructuralFeature) typeObject;
        return feature.getEContainingClass();
    }

    @Override
    public Object binaryEdgeTargetType(Object typeObject) {
        if (typeObject instanceof EAttribute) {
            EAttribute attribute = (EAttribute) typeObject;
            return attribute.getEAttributeType();
        } else if (typeObject instanceof EReference) {
            EReference reference = (EReference) typeObject;
            return reference.getEReferenceType();
        } else
            throw new IllegalArgumentException(INVALID_TYPE_ERROR + typeObject.getClass().getName());
    }

    @Override
    public Object ternaryEdgeSourceType(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object ternaryEdgeTargetType(Object typeObject) {
        throw new UnsupportedOperationException();
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void logDebug(String message) {
        if (getLogger() != null)
            getLogger().debug(message);
    }

    @Override
    public void logError(String message) {
        if (getLogger() != null)
            getLogger().error(message);
    }

    @Override
    public void logError(String message, Throwable cause) {
        if (getLogger() != null)
            getLogger().error(message, cause);
    }

    @Override
    public void logFatal(String message) {
        if (getLogger() != null)
            getLogger().fatal(message);
    }

    @Override
    public void logFatal(String message, Throwable cause) {
        if (getLogger() != null)
            getLogger().fatal(message, cause);
    }

    @Override
    public void logWarning(String message) {
        if (getLogger() != null)
            getLogger().warn(message);
    }

    @Override
    public void logWarning(String message, Throwable cause) {
        if (getLogger() != null)
            getLogger().warn(message, cause);
    }

    @Override
    public String printType(Object typeObject) {
        if (typeObject == null) {
            return "(null)";
        } else if (typeObject instanceof EClassifier) {
            final EClassifier eClassifier = (EClassifier) typeObject;
            final EPackage ePackage = eClassifier.getEPackage();
            final String nsURI = ePackage == null ? null : ePackage.getNsURI();
            final String typeName = eClassifier.getName();
            return "" + nsURI + "/" + typeName;
        } else if (typeObject instanceof EStructuralFeature) {
            final EStructuralFeature feature = (EStructuralFeature) typeObject;
            return printType(feature.getEContainingClass()) + "." + feature.getName();
        } else
            return typeObject.toString();
    }

}
