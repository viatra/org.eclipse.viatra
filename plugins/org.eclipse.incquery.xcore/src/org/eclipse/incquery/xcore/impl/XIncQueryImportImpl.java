/**
 */
package org.eclipse.incquery.xcore.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;

import org.eclipse.incquery.xcore.XIncQueryImport;
import org.eclipse.incquery.xcore.XcorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XInc Query Import</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.incquery.xcore.impl.XIncQueryImportImpl#getImportedPatternModel <em>Imported Pattern Model</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XIncQueryImportImpl extends MinimalEObjectImpl.Container implements XIncQueryImport {
    /**
     * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected int eFlags = 0;

    /**
     * The cached value of the '{@link #getImportedPatternModel() <em>Imported Pattern Model</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getImportedPatternModel()
     * @generated
     * @ordered
     */
    protected PatternModel importedPatternModel;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XIncQueryImportImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XcorePackage.Literals.XINC_QUERY_IMPORT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PatternModel getImportedPatternModel() {
        if (importedPatternModel != null && importedPatternModel.eIsProxy()) {
            InternalEObject oldImportedPatternModel = (InternalEObject)importedPatternModel;
            importedPatternModel = (PatternModel)eResolveProxy(oldImportedPatternModel);
            if (importedPatternModel != oldImportedPatternModel) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL, oldImportedPatternModel, importedPatternModel));
            }
        }
        return importedPatternModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PatternModel basicGetImportedPatternModel() {
        return importedPatternModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setImportedPatternModel(PatternModel newImportedPatternModel) {
        PatternModel oldImportedPatternModel = importedPatternModel;
        importedPatternModel = newImportedPatternModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL, oldImportedPatternModel, importedPatternModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL:
                if (resolve) return getImportedPatternModel();
                return basicGetImportedPatternModel();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL:
                setImportedPatternModel((PatternModel)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL:
                setImportedPatternModel((PatternModel)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case XcorePackage.XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL:
                return importedPatternModel != null;
        }
        return super.eIsSet(featureID);
    }

} //XIncQueryImportImpl
