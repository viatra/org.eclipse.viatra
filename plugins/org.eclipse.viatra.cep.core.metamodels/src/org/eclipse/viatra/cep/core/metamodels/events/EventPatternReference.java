/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Pattern Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPatternReference()
 * @model
 * @generated
 */
public interface EventPatternReference extends EObject {
    /**
     * Returns the value of the '<em><b>Event Pattern</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Pattern</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Pattern</em>' containment reference.
     * @see #setEventPattern(EventPattern)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPatternReference_EventPattern()
     * @model containment="true" required="true"
     * @generated
     */
    EventPattern getEventPattern();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getEventPattern <em>Event Pattern</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Event Pattern</em>' containment reference.
     * @see #getEventPattern()
     * @generated
     */
    void setEventPattern(EventPattern value);

    /**
     * Returns the value of the '<em><b>Multiplicity</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Multiplicity</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Multiplicity</em>' containment reference.
     * @see #setMultiplicity(AbstractMultiplicity)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEventPatternReference_Multiplicity()
     * @model containment="true" required="true"
     * @generated
     */
    AbstractMultiplicity getMultiplicity();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getMultiplicity <em>Multiplicity</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Multiplicity</em>' containment reference.
     * @see #getMultiplicity()
     * @generated
     */
    void setMultiplicity(AbstractMultiplicity value);

} // EventPatternReference
