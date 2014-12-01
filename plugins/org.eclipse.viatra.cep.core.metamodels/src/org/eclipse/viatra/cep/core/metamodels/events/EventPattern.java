/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton <em>Automaton</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPattern()
 * @model abstract="true"
 * @generated
 */
public interface EventPattern extends EObject {
    /**
     * Returns the value of the '<em><b>Automaton</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Automaton</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Automaton</em>' reference.
     * @see #setAutomaton(Automaton)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPattern_Automaton()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern
     * @model opposite="eventPattern"
     * @generated
     */
    Automaton getAutomaton();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton <em>Automaton</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Automaton</em>' reference.
     * @see #getAutomaton()
     * @generated
     */
    void setAutomaton(Automaton value);

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPattern_Id()
     * @model
     * @generated
     */
    String getId();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId(String value);

} // EventPattern
