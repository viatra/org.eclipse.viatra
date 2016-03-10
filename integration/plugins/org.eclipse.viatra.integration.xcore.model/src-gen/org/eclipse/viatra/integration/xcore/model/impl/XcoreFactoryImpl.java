/**
 */
package org.eclipse.viatra.integration.xcore.model.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra.integration.xcore.model.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XcoreFactoryImpl extends EFactoryImpl implements XcoreFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static XcoreFactory init() {
        try {
            XcoreFactory theXcoreFactory = (XcoreFactory)EPackage.Registry.INSTANCE.getEFactory(XcorePackage.eNS_URI);
            if (theXcoreFactory != null) {
                return theXcoreFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new XcoreFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XcoreFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE: return createXViatraQueryDerivedFeature();
            case XcorePackage.XVIATRA_QUERY_PACKAGE: return createXViatraQueryPackage();
            case XcorePackage.XVIATRA_QUERY_IMPORT: return createXViatraQueryImport();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XViatraQueryDerivedFeature createXViatraQueryDerivedFeature() {
        XViatraQueryDerivedFeatureImpl xViatraQueryDerivedFeature = new XViatraQueryDerivedFeatureImpl();
        return xViatraQueryDerivedFeature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XViatraQueryPackage createXViatraQueryPackage() {
        XViatraQueryPackageImpl xViatraQueryPackage = new XViatraQueryPackageImpl();
        return xViatraQueryPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XViatraQueryImport createXViatraQueryImport() {
        XViatraQueryImportImpl xViatraQueryImport = new XViatraQueryImportImpl();
        return xViatraQueryImport;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XcorePackage getXcorePackage() {
        return (XcorePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static XcorePackage getPackage() {
        return XcorePackage.eINSTANCE;
    }

} //XcoreFactoryImpl
