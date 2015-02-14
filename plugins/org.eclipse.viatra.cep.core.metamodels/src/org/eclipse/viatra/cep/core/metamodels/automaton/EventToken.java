/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Token</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState <em>Current State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getRecordedEvents <em>Recorded Events</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getLastProcessed <em>Last Processed</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getTimedZones <em>Timed Zones</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getEventToken()
 * @model
 * @generated
 */
public interface EventToken extends EObject {
    /**
     * Returns the value of the '<em><b>Current State</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getEventTokens <em>Event Tokens</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Current State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Current State</em>' reference.
     * @see #setCurrentState(State)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getEventToken_CurrentState()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getEventTokens
     * @model opposite="eventTokens"
     * @generated
     */
    State getCurrentState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState <em>Current State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Current State</em>' reference.
     * @see #getCurrentState()
     * @generated
     */
    void setCurrentState(State value);

    /**
     * Returns the value of the '<em><b>Recorded Events</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.events.Event}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recorded Events</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recorded Events</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getEventToken_RecordedEvents()
     * @model
     * @generated
     */
    EList<Event> getRecordedEvents();

    /**
     * Returns the value of the '<em><b>Last Processed</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Last Processed</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Last Processed</em>' reference.
     * @see #setLastProcessed(Event)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getEventToken_LastProcessed()
     * @model
     * @generated
     */
    Event getLastProcessed();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getLastProcessed <em>Last Processed</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Last Processed</em>' reference.
     * @see #getLastProcessed()
     * @generated
     */
    void setLastProcessed(Event value);

    /**
     * Returns the value of the '<em><b>Timed Zones</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timed Zones</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Timed Zones</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getEventToken_TimedZones()
     * @model
     * @generated
     */
    EList<TimedZone> getTimedZones();

} // EventToken
