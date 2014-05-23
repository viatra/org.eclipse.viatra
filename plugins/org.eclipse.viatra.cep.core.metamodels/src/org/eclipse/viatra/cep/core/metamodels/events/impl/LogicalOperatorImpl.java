/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator;
import org.eclipse.viatra.cep.core.metamodels.events.TimingOperator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Logical Operator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl#getTiming <em>Timing</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl#getEventPattern <em>Event Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class LogicalOperatorImpl extends ComplexEventOperatorImpl implements LogicalOperator {
    /**
     * The cached value of the '{@link #getTiming() <em>Timing</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTiming()
     * @generated
     * @ordered
     */
    protected TimingOperator timing;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LogicalOperatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.LOGICAL_OPERATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimingOperator getTiming() {
        return timing;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTiming(TimingOperator newTiming, NotificationChain msgs) {
        TimingOperator oldTiming = timing;
        timing = newTiming;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.LOGICAL_OPERATOR__TIMING, oldTiming, newTiming);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTiming(TimingOperator newTiming) {
        if (newTiming != timing) {
            NotificationChain msgs = null;
            if (timing != null)
                msgs = ((InternalEObject)timing).eInverseRemove(this, EventsPackage.TIMING_OPERATOR__OPERATOR, TimingOperator.class, msgs);
            if (newTiming != null)
                msgs = ((InternalEObject)newTiming).eInverseAdd(this, EventsPackage.TIMING_OPERATOR__OPERATOR, TimingOperator.class, msgs);
            msgs = basicSetTiming(newTiming, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.LOGICAL_OPERATOR__TIMING, newTiming, newTiming));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ComplexEventPattern getEventPattern() {
        if (eContainerFeatureID() != EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN) return null;
        return (ComplexEventPattern)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEventPattern(ComplexEventPattern newEventPattern, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newEventPattern, EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEventPattern(ComplexEventPattern newEventPattern) {
        if (newEventPattern != eInternalContainer() || (eContainerFeatureID() != EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN && newEventPattern != null)) {
            if (EcoreUtil.isAncestor(this, newEventPattern))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newEventPattern != null)
                msgs = ((InternalEObject)newEventPattern).eInverseAdd(this, EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, ComplexEventPattern.class, msgs);
            msgs = basicSetEventPattern(newEventPattern, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN, newEventPattern, newEventPattern));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                if (timing != null)
                    msgs = ((InternalEObject)timing).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.LOGICAL_OPERATOR__TIMING, null, msgs);
                return basicSetTiming((TimingOperator)otherEnd, msgs);
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetEventPattern((ComplexEventPattern)otherEnd, msgs);
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
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                return basicSetTiming(null, msgs);
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                return basicSetEventPattern(null, msgs);
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
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                return eInternalContainer().eInverseRemove(this, EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, ComplexEventPattern.class, msgs);
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
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                return getTiming();
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                return getEventPattern();
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
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                setTiming((TimingOperator)newValue);
                return;
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                setEventPattern((ComplexEventPattern)newValue);
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
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                setTiming((TimingOperator)null);
                return;
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                setEventPattern((ComplexEventPattern)null);
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
            case EventsPackage.LOGICAL_OPERATOR__TIMING:
                return timing != null;
            case EventsPackage.LOGICAL_OPERATOR__EVENT_PATTERN:
                return getEventPattern() != null;
        }
        return super.eIsSet(featureID);
    }

} //LogicalOperatorImpl
