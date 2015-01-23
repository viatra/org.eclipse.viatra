/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage
 * @generated
 */
public interface EventsFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EventsFactory eINSTANCE = org.eclipse.viatra.cep.core.metamodels.events.impl.EventsFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Atomic Event Pattern</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Atomic Event Pattern</em>'.
     * @generated
     */
    AtomicEventPattern createAtomicEventPattern();

    /**
     * Returns a new object of class '<em>Complex Event Pattern</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Complex Event Pattern</em>'.
     * @generated
     */
    ComplexEventPattern createComplexEventPattern();

    /**
     * Returns a new object of class '<em>Event Pattern Reference</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Event Pattern Reference</em>'.
     * @generated
     */
    EventPatternReference createEventPatternReference();

    /**
     * Returns a new object of class '<em>Event</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Event</em>'.
     * @generated
     */
    Event createEvent();

    /**
     * Returns a new object of class '<em>OR</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>OR</em>'.
     * @generated
     */
    OR createOR();

    /**
     * Returns a new object of class '<em>NEG</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>NEG</em>'.
     * @generated
     */
    NEG createNEG();

    /**
     * Returns a new object of class '<em>FOLLOWS</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>FOLLOWS</em>'.
     * @generated
     */
    FOLLOWS createFOLLOWS();

    /**
     * Returns a new object of class '<em>AND</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>AND</em>'.
     * @generated
     */
    AND createAND();

    /**
     * Returns a new object of class '<em>Timewindow</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Timewindow</em>'.
     * @generated
     */
    Timewindow createTimewindow();

    /**
     * Returns a new object of class '<em>Multiplicity</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Multiplicity</em>'.
     * @generated
     */
    Multiplicity createMultiplicity();

    /**
     * Returns a new object of class '<em>Infinite</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Infinite</em>'.
     * @generated
     */
    Infinite createInfinite();

    /**
     * Returns a new object of class '<em>At Least One</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>At Least One</em>'.
     * @generated
     */
    AtLeastOne createAtLeastOne();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    EventsPackage getEventsPackage();

} //EventsFactory
