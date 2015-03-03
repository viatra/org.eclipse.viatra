/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Pattern Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternInstance#getEventToken <em>Event Token</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPatternInstance()
 * @model
 * @generated
 */
public interface EventPatternInstance extends EObject {
    /**
     * Returns the value of the '<em><b>Event Token</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getEventPatternInstance <em>Event Pattern Instance</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Token</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Token</em>' reference.
     * @see #setEventToken(EventToken)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPatternInstance_EventToken()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getEventPatternInstance
     * @model opposite="eventPatternInstance" required="true"
     * @generated
     */
    EventToken getEventToken();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternInstance#getEventToken <em>Event Token</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Event Token</em>' reference.
     * @see #getEventToken()
     * @generated
     */
    void setEventToken(EventToken value);

} // EventPatternInstance
