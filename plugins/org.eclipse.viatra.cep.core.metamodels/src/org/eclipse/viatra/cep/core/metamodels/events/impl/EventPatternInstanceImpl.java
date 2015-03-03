/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;

import org.eclipse.viatra.cep.core.metamodels.events.EventPatternInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event Pattern Instance</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternInstanceImpl#getEventToken <em>Event Token</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventPatternInstanceImpl extends MinimalEObjectImpl.Container implements EventPatternInstance {
    /**
     * The cached value of the '{@link #getEventToken() <em>Event Token</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventToken()
     * @generated
     * @ordered
     */
    protected EventToken eventToken;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EventPatternInstanceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.EVENT_PATTERN_INSTANCE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventToken getEventToken() {
        if (eventToken != null && eventToken.eIsProxy()) {
            InternalEObject oldEventToken = (InternalEObject)eventToken;
            eventToken = (EventToken)eResolveProxy(oldEventToken);
            if (eventToken != oldEventToken) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN, oldEventToken, eventToken));
            }
        }
        return eventToken;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventToken basicGetEventToken() {
        return eventToken;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEventToken(EventToken newEventToken, NotificationChain msgs) {
        EventToken oldEventToken = eventToken;
        eventToken = newEventToken;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN, oldEventToken, newEventToken);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEventToken(EventToken newEventToken) {
        if (newEventToken != eventToken) {
            NotificationChain msgs = null;
            if (eventToken != null)
                msgs = ((InternalEObject)eventToken).eInverseRemove(this, AutomatonPackage.EVENT_TOKEN__EVENT_PATTERN_INSTANCE, EventToken.class, msgs);
            if (newEventToken != null)
                msgs = ((InternalEObject)newEventToken).eInverseAdd(this, AutomatonPackage.EVENT_TOKEN__EVENT_PATTERN_INSTANCE, EventToken.class, msgs);
            msgs = basicSetEventToken(newEventToken, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN, newEventToken, newEventToken));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                if (eventToken != null)
                    msgs = ((InternalEObject)eventToken).eInverseRemove(this, AutomatonPackage.EVENT_TOKEN__EVENT_PATTERN_INSTANCE, EventToken.class, msgs);
                return basicSetEventToken((EventToken)otherEnd, msgs);
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
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                return basicSetEventToken(null, msgs);
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
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                if (resolve) return getEventToken();
                return basicGetEventToken();
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
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                setEventToken((EventToken)newValue);
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
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                setEventToken((EventToken)null);
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
            case EventsPackage.EVENT_PATTERN_INSTANCE__EVENT_TOKEN:
                return eventToken != null;
        }
        return super.eIsSet(featureID);
    }

} //EventPatternInstanceImpl
