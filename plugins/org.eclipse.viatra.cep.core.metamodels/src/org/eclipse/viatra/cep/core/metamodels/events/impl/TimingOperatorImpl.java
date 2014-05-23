/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator;
import org.eclipse.viatra.cep.core.metamodels.events.Time;
import org.eclipse.viatra.cep.core.metamodels.events.TimingOperator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Timing Operator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl#getTime <em>Time</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl#getOperator <em>Operator</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TimingOperatorImpl extends ComplexEventOperatorImpl implements TimingOperator {
    /**
     * The cached value of the '{@link #getTime() <em>Time</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTime()
     * @generated
     * @ordered
     */
    protected Time time;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TimingOperatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.TIMING_OPERATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Time getTime() {
        return time;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTime(Time newTime, NotificationChain msgs) {
        Time oldTime = time;
        time = newTime;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.TIMING_OPERATOR__TIME, oldTime, newTime);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTime(Time newTime) {
        if (newTime != time) {
            NotificationChain msgs = null;
            if (time != null)
                msgs = ((InternalEObject)time).eInverseRemove(this, EventsPackage.TIME__OPERATOR, Time.class, msgs);
            if (newTime != null)
                msgs = ((InternalEObject)newTime).eInverseAdd(this, EventsPackage.TIME__OPERATOR, Time.class, msgs);
            msgs = basicSetTime(newTime, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.TIMING_OPERATOR__TIME, newTime, newTime));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LogicalOperator getOperator() {
        if (eContainerFeatureID() != EventsPackage.TIMING_OPERATOR__OPERATOR) return null;
        return (LogicalOperator)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOperator(LogicalOperator newOperator, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newOperator, EventsPackage.TIMING_OPERATOR__OPERATOR, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperator(LogicalOperator newOperator) {
        if (newOperator != eInternalContainer() || (eContainerFeatureID() != EventsPackage.TIMING_OPERATOR__OPERATOR && newOperator != null)) {
            if (EcoreUtil.isAncestor(this, newOperator))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperator != null)
                msgs = ((InternalEObject)newOperator).eInverseAdd(this, EventsPackage.LOGICAL_OPERATOR__TIMING, LogicalOperator.class, msgs);
            msgs = basicSetOperator(newOperator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.TIMING_OPERATOR__OPERATOR, newOperator, newOperator));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.TIMING_OPERATOR__TIME:
                if (time != null)
                    msgs = ((InternalEObject)time).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.TIMING_OPERATOR__TIME, null, msgs);
                return basicSetTime((Time)otherEnd, msgs);
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetOperator((LogicalOperator)otherEnd, msgs);
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
            case EventsPackage.TIMING_OPERATOR__TIME:
                return basicSetTime(null, msgs);
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
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
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
                return eInternalContainer().eInverseRemove(this, EventsPackage.LOGICAL_OPERATOR__TIMING, LogicalOperator.class, msgs);
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
            case EventsPackage.TIMING_OPERATOR__TIME:
                return getTime();
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
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
            case EventsPackage.TIMING_OPERATOR__TIME:
                setTime((Time)newValue);
                return;
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
                setOperator((LogicalOperator)newValue);
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
            case EventsPackage.TIMING_OPERATOR__TIME:
                setTime((Time)null);
                return;
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
                setOperator((LogicalOperator)null);
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
            case EventsPackage.TIMING_OPERATOR__TIME:
                return time != null;
            case EventsPackage.TIMING_OPERATOR__OPERATOR:
                return getOperator() != null;
        }
        return super.eIsSet(featureID);
    }

} //TimingOperatorImpl
