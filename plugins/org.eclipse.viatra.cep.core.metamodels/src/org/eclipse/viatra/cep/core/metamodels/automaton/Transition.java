/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState <em>Pre State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState <em>Post State</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTransition()
 * @model abstract="true"
 * @generated
 */
public interface Transition extends EObject {
    /**
     * Returns the value of the '<em><b>Pre State</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutTransitions <em>Out Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pre State</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pre State</em>' container reference.
     * @see #setPreState(State)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTransition_PreState()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutTransitions
     * @model opposite="outTransitions" transient="false"
     * @generated
     */
    State getPreState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState <em>Pre State</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pre State</em>' container reference.
     * @see #getPreState()
     * @generated
     */
    void setPreState(State value);

    /**
     * Returns the value of the '<em><b>Post State</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInTransitions <em>In Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Post State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Post State</em>' reference.
     * @see #setPostState(State)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTransition_PostState()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getInTransitions
     * @model opposite="inTransitions"
     * @generated
     */
    State getPostState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState <em>Post State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Post State</em>' reference.
     * @see #getPostState()
     * @generated
     */
    void setPostState(State value);

} // Transition
