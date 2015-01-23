/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event Pattern Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventPatternReferenceImpl extends MinimalEObjectImpl.Container implements EventPatternReference {
    /**
     * The cached value of the '{@link #getEventPattern() <em>Event Pattern</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventPattern()
     * @generated
     * @ordered
     */
    protected EventPattern eventPattern;

    /**
     * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMultiplicity()
     * @generated
     * @ordered
     */
    protected AbstractMultiplicity multiplicity;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EventPatternReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.EVENT_PATTERN_REFERENCE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventPattern getEventPattern() {
        return eventPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEventPattern(EventPattern newEventPattern, NotificationChain msgs) {
        EventPattern oldEventPattern = eventPattern;
        eventPattern = newEventPattern;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN, oldEventPattern, newEventPattern);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEventPattern(EventPattern newEventPattern) {
        if (newEventPattern != eventPattern) {
            NotificationChain msgs = null;
            if (eventPattern != null)
                msgs = ((InternalEObject)eventPattern).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN, null, msgs);
            if (newEventPattern != null)
                msgs = ((InternalEObject)newEventPattern).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN, null, msgs);
            msgs = basicSetEventPattern(newEventPattern, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN, newEventPattern, newEventPattern));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AbstractMultiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMultiplicity(AbstractMultiplicity newMultiplicity, NotificationChain msgs) {
        AbstractMultiplicity oldMultiplicity = multiplicity;
        multiplicity = newMultiplicity;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY, oldMultiplicity, newMultiplicity);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMultiplicity(AbstractMultiplicity newMultiplicity) {
        if (newMultiplicity != multiplicity) {
            NotificationChain msgs = null;
            if (multiplicity != null)
                msgs = ((InternalEObject)multiplicity).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY, null, msgs);
            if (newMultiplicity != null)
                msgs = ((InternalEObject)newMultiplicity).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY, null, msgs);
            msgs = basicSetMultiplicity(newMultiplicity, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY, newMultiplicity, newMultiplicity));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN:
                return basicSetEventPattern(null, msgs);
            case EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY:
                return basicSetMultiplicity(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN:
                return getEventPattern();
            case EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY:
                return getMultiplicity();
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
            case EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN:
                setEventPattern((EventPattern)newValue);
                return;
            case EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY:
                setMultiplicity((AbstractMultiplicity)newValue);
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
            case EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN:
                setEventPattern((EventPattern)null);
                return;
            case EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY:
                setMultiplicity((AbstractMultiplicity)null);
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
            case EventsPackage.EVENT_PATTERN_REFERENCE__EVENT_PATTERN:
                return eventPattern != null;
            case EventsPackage.EVENT_PATTERN_REFERENCE__MULTIPLICITY:
                return multiplicity != null;
        }
        return super.eIsSet(featureID);
    }

} //EventPatternReferenceImpl
