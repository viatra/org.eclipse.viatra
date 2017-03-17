/**
 * Copyright (c) 2010-2017, Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi, Peter Lunk, Istvan Rath, Daniel Varro, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *   Peter Lunk - SerializedJavaObjectSubstitution
 */
package org.eclipse.viatra.query.testing.snapshot.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.viatra.query.testing.snapshot.EnumSubstitution;
import org.eclipse.viatra.query.testing.snapshot.SnapshotPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Enum Substitution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl#getValueLiteral <em>Value Literal</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl#getEnumType <em>Enum Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EnumSubstitutionImpl extends MatchSubstitutionRecordImpl implements EnumSubstitution {
    /**
     * The default value of the '{@link #getValueLiteral() <em>Value Literal</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueLiteral()
     * @generated
     * @ordered
     */
    protected static final String VALUE_LITERAL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValueLiteral() <em>Value Literal</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueLiteral()
     * @generated
     * @ordered
     */
    protected String valueLiteral = VALUE_LITERAL_EDEFAULT;

    /**
     * The cached value of the '{@link #getEnumType() <em>Enum Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEnumType()
     * @generated
     * @ordered
     */
    protected EEnum enumType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EnumSubstitutionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SnapshotPackage.Literals.ENUM_SUBSTITUTION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getValueLiteral() {
        return valueLiteral;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValueLiteral(String newValueLiteral) {
        String oldValueLiteral = valueLiteral;
        valueLiteral = newValueLiteral;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SnapshotPackage.ENUM_SUBSTITUTION__VALUE_LITERAL, oldValueLiteral, valueLiteral));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getEnumType() {
        if (enumType != null && enumType.eIsProxy()) {
            InternalEObject oldEnumType = (InternalEObject)enumType;
            enumType = (EEnum)eResolveProxy(oldEnumType);
            if (enumType != oldEnumType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE, oldEnumType, enumType));
            }
        }
        return enumType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum basicGetEnumType() {
        return enumType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEnumType(EEnum newEnumType) {
        EEnum oldEnumType = enumType;
        enumType = newEnumType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE, oldEnumType, enumType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case SnapshotPackage.ENUM_SUBSTITUTION__VALUE_LITERAL:
                return getValueLiteral();
            case SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE:
                if (resolve) return getEnumType();
                return basicGetEnumType();
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
            case SnapshotPackage.ENUM_SUBSTITUTION__VALUE_LITERAL:
                setValueLiteral((String)newValue);
                return;
            case SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE:
                setEnumType((EEnum)newValue);
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
            case SnapshotPackage.ENUM_SUBSTITUTION__VALUE_LITERAL:
                setValueLiteral(VALUE_LITERAL_EDEFAULT);
                return;
            case SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE:
                setEnumType((EEnum)null);
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
            case SnapshotPackage.ENUM_SUBSTITUTION__VALUE_LITERAL:
                return VALUE_LITERAL_EDEFAULT == null ? valueLiteral != null : !VALUE_LITERAL_EDEFAULT.equals(valueLiteral);
            case SnapshotPackage.ENUM_SUBSTITUTION__ENUM_TYPE:
                return enumType != null;
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
        result.append(" (valueLiteral: ");
        result.append(valueLiteral);
        result.append(')');
        return result.toString();
    }

} //EnumSubstitutionImpl
