/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.Time;
import org.eclipse.viatra.cep.core.metamodels.events.TimingOperator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Time</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl#getLength <em>Length</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl#getOperator <em>Operator</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TimeImpl extends MinimalEObjectImpl.Container implements Time {
    /**
     * The default value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected static final long LENGTH_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected long length = LENGTH_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TimeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.TIME;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getLength() {
        return length;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLength(long newLength) {
        long oldLength = length;
        length = newLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.TIME__LENGTH, oldLength, length));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimingOperator getOperator() {
        if (eContainerFeatureID() != EventsPackage.TIME__OPERATOR) return null;
        return (TimingOperator)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOperator(TimingOperator newOperator, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newOperator, EventsPackage.TIME__OPERATOR, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperator(TimingOperator newOperator) {
        if (newOperator != eInternalContainer() || (eContainerFeatureID() != EventsPackage.TIME__OPERATOR && newOperator != null)) {
            if (EcoreUtil.isAncestor(this, newOperator))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperator != null)
                msgs = ((InternalEObject)newOperator).eInverseAdd(this, EventsPackage.TIMING_OPERATOR__TIME, TimingOperator.class, msgs);
            msgs = basicSetOperator(newOperator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.TIME__OPERATOR, newOperator, newOperator));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.TIME__OPERATOR:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetOperator((TimingOperator)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.TIME__OPERATOR:
                return basicSetOperator(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case EventsPackage.TIME__OPERATOR:
                return eInternalContainer().eInverseRemove(this, EventsPackage.TIMING_OPERATOR__TIME, TimingOperator.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case EventsPackage.TIME__LENGTH:
                return getLength();
            case EventsPackage.TIME__OPERATOR:
                return getOperator();
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
            case EventsPackage.TIME__LENGTH:
                setLength((Long)newValue);
                return;
            case EventsPackage.TIME__OPERATOR:
                setOperator((TimingOperator)newValue);
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
            case EventsPackage.TIME__LENGTH:
                setLength(LENGTH_EDEFAULT);
                return;
            case EventsPackage.TIME__OPERATOR:
                setOperator((TimingOperator)null);
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
            case EventsPackage.TIME__LENGTH:
                return length != LENGTH_EDEFAULT;
            case EventsPackage.TIME__OPERATOR:
                return getOperator() != null;
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
        result.append(" (length: ");
        result.append(length);
        result.append(')');
        return result.toString();
    }

} //TimeImpl
