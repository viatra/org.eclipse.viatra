/**
 */
package org.eclipse.viatra.dse.emf.designspace.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.dse.emf.designspace.DesignSpace;
import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Design Space</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl#getStates <em>States</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl#getRootStates <em>Root States</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DesignSpaceImpl extends MinimalEObjectImpl.Container implements DesignSpace {
    /**
     * The cached value of the '{@link #getStates() <em>States</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStates()
     * @generated
     * @ordered
     */
    protected EList<State> states;

    /**
     * The cached value of the '{@link #getTransitions() <em>Transitions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTransitions()
     * @generated
     * @ordered
     */
    protected EList<Transition> transitions;

    /**
     * The cached value of the '{@link #getRootStates() <em>Root States</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRootStates()
     * @generated
     * @ordered
     */
    protected EList<State> rootStates;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DesignSpaceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EMFDesignSpacePackage.Literals.DESIGN_SPACE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<State> getStates() {
        if (states == null) {
            states = new EObjectContainmentEList<State>(State.class, this, EMFDesignSpacePackage.DESIGN_SPACE__STATES);
        }
        return states;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Transition> getTransitions() {
        if (transitions == null) {
            transitions = new EObjectContainmentEList<Transition>(Transition.class, this, EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS);
        }
        return transitions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<State> getRootStates() {
        if (rootStates == null) {
            rootStates = new EObjectResolvingEList<State>(State.class, this, EMFDesignSpacePackage.DESIGN_SPACE__ROOT_STATES);
        }
        return rootStates;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EMFDesignSpacePackage.DESIGN_SPACE__STATES:
                return ((InternalEList<?>)getStates()).basicRemove(otherEnd, msgs);
            case EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS:
                return ((InternalEList<?>)getTransitions()).basicRemove(otherEnd, msgs);
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
            case EMFDesignSpacePackage.DESIGN_SPACE__STATES:
                return getStates();
            case EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS:
                return getTransitions();
            case EMFDesignSpacePackage.DESIGN_SPACE__ROOT_STATES:
                return getRootStates();
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
            case EMFDesignSpacePackage.DESIGN_SPACE__STATES:
                getStates().clear();
                getStates().addAll((Collection<? extends State>)newValue);
                return;
            case EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS:
                getTransitions().clear();
                getTransitions().addAll((Collection<? extends Transition>)newValue);
                return;
            case EMFDesignSpacePackage.DESIGN_SPACE__ROOT_STATES:
                getRootStates().clear();
                getRootStates().addAll((Collection<? extends State>)newValue);
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
            case EMFDesignSpacePackage.DESIGN_SPACE__STATES:
                getStates().clear();
                return;
            case EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS:
                getTransitions().clear();
                return;
            case EMFDesignSpacePackage.DESIGN_SPACE__ROOT_STATES:
                getRootStates().clear();
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
            case EMFDesignSpacePackage.DESIGN_SPACE__STATES:
                return states != null && !states.isEmpty();
            case EMFDesignSpacePackage.DESIGN_SPACE__TRANSITIONS:
                return transitions != null && !transitions.isEmpty();
            case EMFDesignSpacePackage.DESIGN_SPACE__ROOT_STATES:
                return rootStates != null && !rootStates.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //DesignSpaceImpl
