/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Epsilon Transition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class EpsilonTransitionImpl extends TransitionImpl implements EpsilonTransition {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EpsilonTransitionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return AutomatonPackage.Literals.EPSILON_TRANSITION;
    }

} //EpsilonTransitionImpl
