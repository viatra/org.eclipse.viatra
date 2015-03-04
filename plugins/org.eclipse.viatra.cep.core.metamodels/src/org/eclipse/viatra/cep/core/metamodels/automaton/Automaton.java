/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Automaton</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getStates <em>States</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventTokens <em>Event Tokens</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getTimedZones <em>Timed Zones</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getInitialState <em>Initial State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getFinalStates <em>Final States</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getTrapState <em>Trap State</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton()
 * @model
 * @generated
 */
public interface Automaton extends EObject {
    /**
     * Returns the value of the '<em><b>States</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.State}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>States</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>States</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_States()
     * @model containment="true"
     * @generated
     */
    EList<State> getStates();

    /**
     * Returns the value of the '<em><b>Event Pattern</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton <em>Automaton</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Pattern</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Pattern</em>' reference.
     * @see #setEventPattern(EventPattern)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_EventPattern()
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton
     * @model opposite="automaton" required="true"
     * @generated
     */
    EventPattern getEventPattern();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern <em>Event Pattern</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Event Pattern</em>' reference.
     * @see #getEventPattern()
     * @generated
     */
    void setEventPattern(EventPattern value);

    /**
     * Returns the value of the '<em><b>Event Tokens</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Tokens</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Tokens</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_EventTokens()
     * @model containment="true"
     * @generated
     */
    EList<EventToken> getEventTokens();

    /**
     * Returns the value of the '<em><b>Timed Zones</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timed Zones</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Timed Zones</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_TimedZones()
     * @model containment="true"
     * @generated
     */
    EList<TimedZone> getTimedZones();

    /**
     * Returns the value of the '<em><b>Initial State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initial State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initial State</em>' reference.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_InitialState()
     * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="org.eclipse.incquery.querybasedfeature patternFQN='org.eclipse.viatra.cep.core.metamodels.derived.initialState'"
     * @generated
     */
    InitState getInitialState();

    /**
     * Returns the value of the '<em><b>Final States</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.FinalState}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Final States</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Final States</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_FinalStates()
     * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="org.eclipse.incquery.querybasedfeature patternFQN='org.eclipse.viatra.cep.core.metamodels.derived.finalStates'"
     * @generated
     */
    EList<FinalState> getFinalStates();

    /**
     * Returns the value of the '<em><b>Trap State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Trap State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Trap State</em>' reference.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getAutomaton_TrapState()
     * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="org.eclipse.incquery.querybasedfeature patternFQN='org.eclipse.viatra.cep.core.metamodels.derived.trapState'"
     * @generated
     */
    TrapState getTrapState();

} // Automaton
