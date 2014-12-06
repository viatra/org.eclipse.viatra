/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Guard</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getEventType <em>Event Type</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition <em>Transition</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getGuard()
 * @model
 * @generated
 */
public interface Guard extends EObject {
    /**
     * Returns the value of the '<em><b>Event Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Type</em>' reference.
     * @see #setEventType(AtomicEventPattern)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getGuard_EventType()
     * @model required="true"
     * @generated
     */
    AtomicEventPattern getEventType();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getEventType <em>Event Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Event Type</em>' reference.
     * @see #getEventType()
     * @generated
     */
    void setEventType(AtomicEventPattern value);

    /**
     * Returns the value of the '<em><b>Transition</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard <em>Guard</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Transition</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Transition</em>' container reference.
     * @see #setTransition(TypedTransition)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getGuard_Transition()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard
     * @model opposite="guard" required="true" transient="false"
     * @generated
     */
    TypedTransition getTransition();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition <em>Transition</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Transition</em>' container reference.
     * @see #getTransition()
     * @generated
     */
    void setTransition(TypedTransition value);

} // Guard
