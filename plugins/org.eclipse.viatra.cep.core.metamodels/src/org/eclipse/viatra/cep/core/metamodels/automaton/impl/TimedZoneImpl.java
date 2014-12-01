/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Timed Zone</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl#getInState <em>In State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl#getOutState <em>Out State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl#getTime <em>Time</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TimedZoneImpl extends MinimalEObjectImpl.Container implements TimedZone {
    /**
     * The cached value of the '{@link #getInState() <em>In State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInState()
     * @generated
     * @ordered
     */
    protected State inState;

    /**
     * The cached value of the '{@link #getOutState() <em>Out State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutState()
     * @generated
     * @ordered
     */
    protected State outState;

    /**
     * The default value of the '{@link #getTime() <em>Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTime()
     * @generated
     * @ordered
     */
    protected static final long TIME_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getTime() <em>Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTime()
     * @generated
     * @ordered
     */
    protected long time = TIME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TimedZoneImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return AutomatonPackage.Literals.TIMED_ZONE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State getInState() {
        if (inState != null && inState.eIsProxy()) {
            InternalEObject oldInState = (InternalEObject)inState;
            inState = (State)eResolveProxy(oldInState);
            if (inState != oldInState) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.TIMED_ZONE__IN_STATE, oldInState, inState));
            }
        }
        return inState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State basicGetInState() {
        return inState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetInState(State newInState, NotificationChain msgs) {
        State oldInState = inState;
        inState = newInState;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AutomatonPackage.TIMED_ZONE__IN_STATE, oldInState, newInState);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInState(State newInState) {
        if (newInState != inState) {
            NotificationChain msgs = null;
            if (inState != null)
                msgs = ((InternalEObject)inState).eInverseRemove(this, AutomatonPackage.STATE__IN_STATE_OF, State.class, msgs);
            if (newInState != null)
                msgs = ((InternalEObject)newInState).eInverseAdd(this, AutomatonPackage.STATE__IN_STATE_OF, State.class, msgs);
            msgs = basicSetInState(newInState, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.TIMED_ZONE__IN_STATE, newInState, newInState));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State getOutState() {
        if (outState != null && outState.eIsProxy()) {
            InternalEObject oldOutState = (InternalEObject)outState;
            outState = (State)eResolveProxy(oldOutState);
            if (outState != oldOutState) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.TIMED_ZONE__OUT_STATE, oldOutState, outState));
            }
        }
        return outState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State basicGetOutState() {
        return outState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOutState(State newOutState, NotificationChain msgs) {
        State oldOutState = outState;
        outState = newOutState;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AutomatonPackage.TIMED_ZONE__OUT_STATE, oldOutState, newOutState);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutState(State newOutState) {
        if (newOutState != outState) {
            NotificationChain msgs = null;
            if (outState != null)
                msgs = ((InternalEObject)outState).eInverseRemove(this, AutomatonPackage.STATE__OUT_STATE_OF, State.class, msgs);
            if (newOutState != null)
                msgs = ((InternalEObject)newOutState).eInverseAdd(this, AutomatonPackage.STATE__OUT_STATE_OF, State.class, msgs);
            msgs = basicSetOutState(newOutState, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.TIMED_ZONE__OUT_STATE, newOutState, newOutState));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getTime() {
        return time;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTime(long newTime) {
        long oldTime = time;
        time = newTime;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.TIMED_ZONE__TIME, oldTime, time));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                if (inState != null)
                    msgs = ((InternalEObject)inState).eInverseRemove(this, AutomatonPackage.STATE__IN_STATE_OF, State.class, msgs);
                return basicSetInState((State)otherEnd, msgs);
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                if (outState != null)
                    msgs = ((InternalEObject)outState).eInverseRemove(this, AutomatonPackage.STATE__OUT_STATE_OF, State.class, msgs);
                return basicSetOutState((State)otherEnd, msgs);
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
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                return basicSetInState(null, msgs);
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                return basicSetOutState(null, msgs);
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
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                if (resolve) return getInState();
                return basicGetInState();
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                if (resolve) return getOutState();
                return basicGetOutState();
            case AutomatonPackage.TIMED_ZONE__TIME:
                return getTime();
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
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                setInState((State)newValue);
                return;
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                setOutState((State)newValue);
                return;
            case AutomatonPackage.TIMED_ZONE__TIME:
                setTime((Long)newValue);
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
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                setInState((State)null);
                return;
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                setOutState((State)null);
                return;
            case AutomatonPackage.TIMED_ZONE__TIME:
                setTime(TIME_EDEFAULT);
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
            case AutomatonPackage.TIMED_ZONE__IN_STATE:
                return inState != null;
            case AutomatonPackage.TIMED_ZONE__OUT_STATE:
                return outState != null;
            case AutomatonPackage.TIMED_ZONE__TIME:
                return time != TIME_EDEFAULT;
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
        result.append(" (time: ");
        result.append(time);
        result.append(')');
        return result.toString();
    }

} //TimedZoneImpl
