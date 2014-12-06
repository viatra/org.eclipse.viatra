/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Typed Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard <em>Guard</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTypedTransition()
 * @model
 * @generated
 */
public interface TypedTransition extends Transition {
    /**
     * Returns the value of the '<em><b>Guard</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Guard</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Guard</em>' containment reference.
     * @see #setGuard(Guard)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTypedTransition_Guard()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition
     * @model opposite="transition" containment="true" required="true"
     * @generated
     */
    Guard getGuard();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard <em>Guard</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Guard</em>' containment reference.
     * @see #getGuard()
     * @generated
     */
    void setGuard(Guard value);

} // TypedTransition
