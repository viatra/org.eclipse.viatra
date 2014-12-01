/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timed Zone</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState <em>In State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState <em>Out State</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getTime <em>Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTimedZone()
 * @model abstract="true"
 * @generated
 */
public interface TimedZone extends EObject {
    /**
     * Returns the value of the '<em><b>In State</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInStateOf <em>In State Of</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In State</em>' reference.
     * @see #setInState(State)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTimedZone_InState()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getInStateOf
     * @model opposite="inStateOf" required="true"
     * @generated
     */
    State getInState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState <em>In State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>In State</em>' reference.
     * @see #getInState()
     * @generated
     */
    void setInState(State value);

    /**
     * Returns the value of the '<em><b>Out State</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutStateOf <em>Out State Of</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out State</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out State</em>' reference.
     * @see #setOutState(State)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTimedZone_OutState()
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutStateOf
     * @model opposite="outStateOf" required="true"
     * @generated
     */
    State getOutState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState <em>Out State</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Out State</em>' reference.
     * @see #getOutState()
     * @generated
     */
    void setOutState(State value);

    /**
     * Returns the value of the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time</em>' attribute.
     * @see #setTime(long)
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage#getTimedZone_Time()
     * @model required="true"
     * @generated
     */
    long getTime();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getTime <em>Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time</em>' attribute.
     * @see #getTime()
     * @generated
     */
    void setTime(long value);

} // TimedZone
