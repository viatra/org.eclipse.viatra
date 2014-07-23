/**
 */
package org.eclipse.viatra.dse.emf.designspace.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage;
import org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>State</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl#getState <em>State</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl#getOutTransitions <em>Out Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl#getInTransitions <em>In Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl#getThreadsafeFacade <em>Threadsafe Facade</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StateImpl extends MinimalEObjectImpl.Container implements State {
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
     * The default value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected static final EMFInternalTraversalState STATE_EDEFAULT = EMFInternalTraversalState.NOT_YET_PROCESSED;

    /**
     * The cached value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected EMFInternalTraversalState state = STATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getOutTransitions() <em>Out Transitions</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutTransitions()
     * @generated
     * @ordered
     */
    protected EList<Transition> outTransitions;

    /**
     * The cached value of the '{@link #getInTransitions() <em>In Transitions</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInTransitions()
     * @generated
     * @ordered
     */
    protected EList<Transition> inTransitions;

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
    protected StateImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EMFDesignSpacePackage.Literals.STATE;
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
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.STATE__ID, oldId, id));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFInternalTraversalState getState() {
        return state;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setState(EMFInternalTraversalState newState) {
        EMFInternalTraversalState oldState = state;
        state = newState == null ? STATE_EDEFAULT : newState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.STATE__STATE, oldState, state));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Transition> getOutTransitions() {
        if (outTransitions == null) {
            outTransitions = new EObjectWithInverseResolvingEList<Transition>(Transition.class, this, EMFDesignSpacePackage.STATE__OUT_TRANSITIONS, EMFDesignSpacePackage.TRANSITION__FIRED_FROM);
        }
        return outTransitions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Transition> getInTransitions() {
        if (inTransitions == null) {
            inTransitions = new EObjectWithInverseResolvingEList<Transition>(Transition.class, this, EMFDesignSpacePackage.STATE__IN_TRANSITIONS, EMFDesignSpacePackage.TRANSITION__RESULTS_IN);
        }
        return inTransitions;
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
            eNotify(new ENotificationImpl(this, Notification.SET, EMFDesignSpacePackage.STATE__THREADSAFE_FACADE, oldThreadsafeFacade, threadsafeFacade));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutTransitions()).basicAdd(otherEnd, msgs);
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getInTransitions()).basicAdd(otherEnd, msgs);
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
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                return ((InternalEList<?>)getOutTransitions()).basicRemove(otherEnd, msgs);
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                return ((InternalEList<?>)getInTransitions()).basicRemove(otherEnd, msgs);
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
            case EMFDesignSpacePackage.STATE__ID:
                return getId();
            case EMFDesignSpacePackage.STATE__STATE:
                return getState();
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                return getOutTransitions();
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                return getInTransitions();
            case EMFDesignSpacePackage.STATE__THREADSAFE_FACADE:
                return getThreadsafeFacade();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case EMFDesignSpacePackage.STATE__ID:
                setId(newValue);
                return;
            case EMFDesignSpacePackage.STATE__STATE:
                setState((EMFInternalTraversalState)newValue);
                return;
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                getOutTransitions().clear();
                getOutTransitions().addAll((Collection<? extends Transition>)newValue);
                return;
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                getInTransitions().clear();
                getInTransitions().addAll((Collection<? extends Transition>)newValue);
                return;
            case EMFDesignSpacePackage.STATE__THREADSAFE_FACADE:
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
            case EMFDesignSpacePackage.STATE__ID:
                setId(ID_EDEFAULT);
                return;
            case EMFDesignSpacePackage.STATE__STATE:
                setState(STATE_EDEFAULT);
                return;
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                getOutTransitions().clear();
                return;
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                getInTransitions().clear();
                return;
            case EMFDesignSpacePackage.STATE__THREADSAFE_FACADE:
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
            case EMFDesignSpacePackage.STATE__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case EMFDesignSpacePackage.STATE__STATE:
                return state != STATE_EDEFAULT;
            case EMFDesignSpacePackage.STATE__OUT_TRANSITIONS:
                return outTransitions != null && !outTransitions.isEmpty();
            case EMFDesignSpacePackage.STATE__IN_TRANSITIONS:
                return inTransitions != null && !inTransitions.isEmpty();
            case EMFDesignSpacePackage.STATE__THREADSAFE_FACADE:
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
        result.append(", state: ");
        result.append(state);
        result.append(", threadsafeFacade: ");
        result.append(threadsafeFacade);
        result.append(')');
        return result.toString();
    }

} //StateImpl
