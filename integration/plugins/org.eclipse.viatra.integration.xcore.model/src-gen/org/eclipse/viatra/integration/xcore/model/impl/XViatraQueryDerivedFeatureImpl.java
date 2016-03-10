/**
 */
package org.eclipse.viatra.integration.xcore.model.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.xcore.impl.XStructuralFeatureImpl;

import org.eclipse.viatra.integration.xcore.model.XViatraQueryDerivedFeature;
import org.eclipse.viatra.integration.xcore.model.XcorePackage;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XViatra Query Derived Feature</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.impl.XViatraQueryDerivedFeatureImpl#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.impl.XViatraQueryDerivedFeatureImpl#isReference <em>Reference</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XViatraQueryDerivedFeatureImpl extends XStructuralFeatureImpl implements XViatraQueryDerivedFeature {
    /**
     * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected int eFlags = 0;

    /**
     * The cached value of the '{@link #getPattern() <em>Pattern</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPattern()
     * @generated
     * @ordered
     */
    protected Pattern pattern;

    /**
     * The default value of the '{@link #isReference() <em>Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isReference()
     * @generated
     * @ordered
     */
    protected static final boolean REFERENCE_EDEFAULT = false;

    /**
     * The flag representing the value of the '{@link #isReference() <em>Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isReference()
     * @generated
     * @ordered
     */
    protected static final int REFERENCE_EFLAG = 1 << 8;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XViatraQueryDerivedFeatureImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XcorePackage.Literals.XVIATRA_QUERY_DERIVED_FEATURE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Pattern getPattern() {
        if (pattern != null && pattern.eIsProxy()) {
            InternalEObject oldPattern = (InternalEObject)pattern;
            pattern = (Pattern)eResolveProxy(oldPattern);
            if (pattern != oldPattern) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN, oldPattern, pattern));
            }
        }
        return pattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Pattern basicGetPattern() {
        return pattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPattern(Pattern newPattern) {
        Pattern oldPattern = pattern;
        pattern = newPattern;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN, oldPattern, pattern));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isReference() {
        return (eFlags & REFERENCE_EFLAG) != 0;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReference(boolean newReference) {
        boolean oldReference = (eFlags & REFERENCE_EFLAG) != 0;
        if (newReference) eFlags |= REFERENCE_EFLAG; else eFlags &= ~REFERENCE_EFLAG;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE, oldReference, newReference));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN:
                if (resolve) return getPattern();
                return basicGetPattern();
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE:
                return isReference();
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
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN:
                setPattern((Pattern)newValue);
                return;
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE:
                setReference((Boolean)newValue);
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
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN:
                setPattern((Pattern)null);
                return;
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE:
                setReference(REFERENCE_EDEFAULT);
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
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__PATTERN:
                return pattern != null;
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE:
                return ((eFlags & REFERENCE_EFLAG) != 0) != REFERENCE_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (reference: ");
        result.append((eFlags & REFERENCE_EFLAG) != 0);
        result.append(')');
        return result.toString();
    }

} //XViatraQueryDerivedFeatureImpl
