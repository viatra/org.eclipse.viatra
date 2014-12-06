/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Internal Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getAutomata <em>Automata</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getLatestEvent <em>Latest Event</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getContext <em>Context</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getInternalModel()
 * @model
 * @generated
 */
public interface InternalModel extends EObject {
    /**
     * Returns the value of the '<em><b>Automata</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Automata</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Automata</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getInternalModel_Automata()
     * @model containment="true"
     * @generated
     */
    EList<Automaton> getAutomata();

    /**
     * Returns the value of the '<em><b>Latest Event</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Latest Event</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Latest Event</em>' containment reference.
     * @see #setLatestEvent(Event)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getInternalModel_LatestEvent()
     * @model containment="true"
     * @generated
     */
    Event getLatestEvent();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getLatestEvent <em>Latest Event</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Latest Event</em>' containment reference.
     * @see #getLatestEvent()
     * @generated
     */
    void setLatestEvent(Event value);

    /**
     * Returns the value of the '<em><b>Context</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.viatra.cep.core.metamodels.automaton.EventContext}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Context</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Context</em>' attribute.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
     * @see #setContext(EventContext)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getInternalModel_Context()
     * @model required="true"
     * @generated
     */
    EventContext getContext();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getContext <em>Context</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Context</em>' attribute.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
     * @see #getContext()
     * @generated
     */
    void setContext(EventContext value);

} // InternalModel
