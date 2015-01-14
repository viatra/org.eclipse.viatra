/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Event#isIsProcessed <em>Is Processed</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEvent()
 * @model
 * @generated
 */
public interface Event extends EObject {
    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEvent_Type()
     * @model required="true"
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

    /**
     * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timestamp</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Timestamp</em>' attribute.
     * @see #setTimestamp(long)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEvent_Timestamp()
     * @model required="true"
     * @generated
     */
    long getTimestamp();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getTimestamp <em>Timestamp</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Timestamp</em>' attribute.
     * @see #getTimestamp()
     * @generated
     */
    void setTimestamp(long value);

    /**
     * Returns the value of the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source</em>' reference.
     * @see #setSource(EventSource)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEvent_Source()
     * @model
     * @generated
     */
    EventSource getSource();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getSource <em>Source</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' reference.
     * @see #getSource()
     * @generated
     */
    void setSource(EventSource value);

    /**
     * Returns the value of the '<em><b>Is Processed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Is Processed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Processed</em>' attribute.
     * @see #setIsProcessed(boolean)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getEvent_IsProcessed()
     * @model required="true"
     * @generated
     */
    boolean isIsProcessed();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#isIsProcessed <em>Is Processed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Processed</em>' attribute.
     * @see #isIsProcessed()
     * @generated
     */
    void setIsProcessed(boolean value);

} // Event
