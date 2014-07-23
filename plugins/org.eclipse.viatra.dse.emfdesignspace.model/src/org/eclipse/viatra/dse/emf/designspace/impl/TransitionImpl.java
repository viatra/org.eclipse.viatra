/**
 */
package org.eclipse.viatra.dse.emf.designspace.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl#getFiredFrom <em>Fired From</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl#getResultsIn <em>Results In</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl#getRuleData <em>Rule Data</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl#getThreadsafeFacade <em>Threadsafe Facade</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TransitionImpl extends MinimalEObjectImpl.Container implements Transition {
    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final Object ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected Object id = ID_EDEFAULT;

    /**
     * The cached value of the '{@link #getFiredFrom() <em>Fired From</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFiredFrom()
     * @generated
     * @ordered
     */
    protected State firedFrom;

    /**
     * The cached value of the '{@link #getResultsIn() <em>Results In</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResultsIn()
     * @generated
     * @ordered
     */
    protected State resultsIn;

    /**
     * The default value of the '{@link #getRuleData() <em>Rule Data</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRuleData()
     * @generated
     * @ordered
     */
    protected static final Object RULE_DATA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRuleData() <em>Rule Data</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRuleData()
     * @generated
     * @ordered
     */
    protected Object ruleData = RULE_DATA_EDEFAULT;

    /**
     * The default value of the '{@link #getThreadsafeFacade() <em>Threadsafe Facade</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getThreadsafeFacade()
     * @generated
     * @ordered
     */
    protected static final Object THREADSAFE_FACADE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getThreadsafeFacade() <em>Threadsafe Facade</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getThreadsafeFacade()
     * @generated
     * @ordered
     */
    protected Object threadsafeFacade = THREADSAFE_FACADE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TransitionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EMFDesignSpacePackage.Literals.TRANSITION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getId() {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setId(Object newId) {
        Object oldId = id;
        id = newId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__ID, oldId, id));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State getFiredFrom() {
        if (firedFrom != null && firedFrom.eIsProxy()) {
            InternalEObject oldFiredFrom = (InternalEObject)firedFrom;
            firedFrom = (State)eResolveProxy(oldFiredFrom);
            if (firedFrom != oldFiredFrom) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EMFDesignSpacePackage.TRANSITION__FIRED_FROM, oldFiredFrom, firedFrom));
            }
        }
        return firedFrom;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State basicGetFiredFrom() {
        return firedFrom;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetFiredFrom(State newFiredFrom, NotificationChain msgs) {
        State oldFiredFrom = firedFrom;
        firedFrom = newFiredFrom;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__FIRED_FROM, oldFiredFrom, newFiredFrom);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFiredFrom(State newFiredFrom) {
        if (newFiredFrom != firedFrom) {
            NotificationChain msgs = null;
            if (firedFrom != null)
                msgs = ((InternalEObject)firedFrom).eInverseRemove(this, EMFDesignSpacePackage.STATE__OUT_TRANSITIONS, State.class, msgs);
            if (newFiredFrom != null)
                msgs = ((InternalEObject)newFiredFrom).eInverseAdd(this, EMFDesignSpacePackage.STATE__OUT_TRANSITIONS, State.class, msgs);
            msgs = basicSetFiredFrom(newFiredFrom, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__FIRED_FROM, newFiredFrom, newFiredFrom));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State getResultsIn() {
        if (resultsIn != null && resultsIn.eIsProxy()) {
            InternalEObject oldResultsIn = (InternalEObject)resultsIn;
            resultsIn = (State)eResolveProxy(oldResultsIn);
            if (resultsIn != oldResultsIn) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EMFDesignSpacePackage.TRANSITION__RESULTS_IN, oldResultsIn, resultsIn));
            }
        }
        return resultsIn;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State basicGetResultsIn() {
        return resultsIn;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetResultsIn(State newResultsIn, NotificationChain msgs) {
        State oldResultsIn = resultsIn;
        resultsIn = newResultsIn;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__RESULTS_IN, oldResultsIn, newResultsIn);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResultsIn(State newResultsIn) {
        if (newResultsIn != resultsIn) {
            NotificationChain msgs = null;
            if (resultsIn != null)
                msgs = ((InternalEObject)resultsIn).eInverseRemove(this, EMFDesignSpacePackage.STATE__IN_TRANSITIONS, State.class, msgs);
            if (newResultsIn != null)
                msgs = ((InternalEObject)newResultsIn).eInverseAdd(this, EMFDesignSpacePackage.STATE__IN_TRANSITIONS, State.class, msgs);
            msgs = basicSetResultsIn(newResultsIn, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__RESULTS_IN, newResultsIn, newResultsIn));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getRuleData() {
        return ruleData;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRuleData(Object newRuleData) {
        Object oldRuleData = ruleData;
        ruleData = newRuleData;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__RULE_DATA, oldRuleData, ruleData));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getThreadsafeFacade() {
        return threadsafeFacade;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setThreadsafeFacade(Object newThreadsafeFacade) {
        Object oldThreadsafeFacade = threadsafeFacade;
        threadsafeFacade = newThreadsafeFacade;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.TRANSITION__THREADSAFE_FACADE, oldThreadsafeFacade, threadsafeFacade));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                if (firedFrom != null)
                    msgs = ((InternalEObject)firedFrom).eInverseRemove(this, EMFDesignSpacePackage.STATE__OUT_TRANSITIONS, State.class, msgs);
                return basicSetFiredFrom((State)otherEnd, msgs);
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                if (resultsIn != null)
                    msgs = ((InternalEObject)resultsIn).eInverseRemove(this, EMFDesignSpacePackage.STATE__IN_TRANSITIONS, State.class, msgs);
                return basicSetResultsIn((State)otherEnd, msgs);
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
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                return basicSetFiredFrom(null, msgs);
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                return basicSetResultsIn(null, msgs);
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
            case EMFDesignSpacePackage.TRANSITION__ID:
                return getId();
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                if (resolve) return getFiredFrom();
                return basicGetFiredFrom();
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                if (resolve) return getResultsIn();
                return basicGetResultsIn();
            case EMFDesignSpacePackage.TRANSITION__RULE_DATA:
                return getRuleData();
            case EMFDesignSpacePackage.TRANSITION__THREADSAFE_FACADE:
                return getThreadsafeFacade();
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
            case EMFDesignSpacePackage.TRANSITION__ID:
                setId(newValue);
                return;
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                setFiredFrom((State)newValue);
                return;
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                setResultsIn((State)newValue);
                return;
            case EMFDesignSpacePackage.TRANSITION__RULE_DATA:
                setRuleData(newValue);
                return;
            case EMFDesignSpacePackage.TRANSITION__THREADSAFE_FACADE:
                setThreadsafeFacade(newValue);
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
            case EMFDesignSpacePackage.TRANSITION__ID:
                setId(ID_EDEFAULT);
                return;
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                setFiredFrom((State)null);
                return;
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                setResultsIn((State)null);
                return;
            case EMFDesignSpacePackage.TRANSITION__RULE_DATA:
                setRuleData(RULE_DATA_EDEFAULT);
                return;
            case EMFDesignSpacePackage.TRANSITION__THREADSAFE_FACADE:
                setThreadsafeFacade(THREADSAFE_FACADE_EDEFAULT);
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
            case EMFDesignSpacePackage.TRANSITION__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case EMFDesignSpacePackage.TRANSITION__FIRED_FROM:
                return firedFrom != null;
            case EMFDesignSpacePackage.TRANSITION__RESULTS_IN:
                return resultsIn != null;
            case EMFDesignSpacePackage.TRANSITION__RULE_DATA:
                return RULE_DATA_EDEFAULT == null ? ruleData != null : !RULE_DATA_EDEFAULT.equals(ruleData);
            case EMFDesignSpacePackage.TRANSITION__THREADSAFE_FACADE:
                return THREADSAFE_FACADE_EDEFAULT == null ? threadsafeFacade != null : !THREADSAFE_FACADE_EDEFAULT.equals(threadsafeFacade);
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
        result.append(" (id: ");
        result.append(id);
        result.append(", ruleData: ");
        result.append(ruleData);
        result.append(", threadsafeFacade: ");
        result.append(threadsafeFacade);
        result.append(')');
        return result.toString();
    }

} //TransitionImpl
