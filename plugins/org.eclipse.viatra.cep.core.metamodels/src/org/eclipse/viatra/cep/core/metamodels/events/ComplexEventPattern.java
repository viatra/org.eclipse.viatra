/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Complex Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getCompositionEvents <em>Composition Events</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator <em>Operator</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getTimeWindow <em>Time Window</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getComplexEventPattern()
 * @model
 * @generated
 */
public interface ComplexEventPattern extends EventPattern {
    /**
     * Returns the value of the '<em><b>Composition Events</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Composition Events</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Composition Events</em>' containment reference list.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getComplexEventPattern_CompositionEvents()
     * @model containment="true"
     * @generated
     */
    EList<EventPattern> getCompositionEvents();

    /**
     * Returns the value of the '<em><b>Operator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operator</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operator</em>' containment reference.
     * @see #setOperator(ComplexEventOperator)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getComplexEventPattern_Operator()
     * @model containment="true" required="true"
     * @generated
     */
    ComplexEventOperator getOperator();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator <em>Operator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operator</em>' containment reference.
     * @see #getOperator()
     * @generated
     */
    void setOperator(ComplexEventOperator value);

    /**
     * Returns the value of the '<em><b>Time Window</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time Window</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time Window</em>' containment reference.
     * @see #setTimeWindow(TimeWindow)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getComplexEventPattern_TimeWindow()
     * @model containment="true"
     * @generated
     */
    TimeWindow getTimeWindow();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getTimeWindow <em>Time Window</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Window</em>' containment reference.
     * @see #getTimeWindow()
     * @generated
     */
    void setTimeWindow(TimeWindow value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model required="true" eventRequired="true"
     * @generated
     */
    boolean evaluateParameterBindings(Event event);

} // ComplexEventPattern
