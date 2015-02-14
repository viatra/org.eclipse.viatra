/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInTransitions <em>In Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutTransitions <em>Out Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getEventTokens <em>Event Tokens</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLastProcessedEvent <em>Last Processed Event</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInStateOf <em>In State Of</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutStateOf <em>Out State Of</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState()
 * @model
 * @generated
 */
public interface State extends EObject {
    /**
     * Returns the value of the '<em><b>In Transitions</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState <em>Post State</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Transitions</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Transitions</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_InTransitions()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState
     * @model opposite="postState"
     * @generated
     */
    EList<Transition> getInTransitions();

    /**
     * Returns the value of the '<em><b>Out Transitions</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState <em>Pre State</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Transitions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Transitions</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_OutTransitions()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState
     * @model opposite="preState" containment="true"
     * @generated
     */
    EList<Transition> getOutTransitions();

    /**
     * Returns the value of the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Label</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label</em>' attribute.
     * @see #setLabel(String)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_Label()
     * @model
     * @generated
     */
    String getLabel();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLabel <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Label</em>' attribute.
     * @see #getLabel()
     * @generated
     */
    void setLabel(String value);

    /**
     * Returns the value of the '<em><b>Event Tokens</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState <em>Current State</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Tokens</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Tokens</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_EventTokens()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState
     * @model opposite="currentState"
     * @generated
     */
    EList<EventToken> getEventTokens();

    /**
     * Returns the value of the '<em><b>Last Processed Event</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Last Processed Event</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Last Processed Event</em>' reference.
     * @see #setLastProcessedEvent(Event)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_LastProcessedEvent()
     * @model
     * @generated
     */
    Event getLastProcessedEvent();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLastProcessedEvent <em>Last Processed Event</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Last Processed Event</em>' reference.
     * @see #getLastProcessedEvent()
     * @generated
     */
    void setLastProcessedEvent(Event value);

    /**
     * Returns the value of the '<em><b>In State Of</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState <em>In State</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In State Of</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In State Of</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_InStateOf()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState
     * @model opposite="inState"
     * @generated
     */
    EList<TimedZone> getInStateOf();

    /**
     * Returns the value of the '<em><b>Out State Of</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState <em>Out State</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out State Of</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out State Of</em>' reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getState_OutStateOf()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState
     * @model opposite="outState"
     * @generated
     */
    EList<TimedZone> getOutStateOf();

} // State
