/**
 * Copyright (c) 2010-2017, Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi, Peter Lunk, Istvan Rath, Daniel Varro, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *   Peter Lunk - SerializedJavaObjectSubstitution
 */
package org.eclipse.viatra.query.testing.snapshot;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage
 * @generated
 */
public interface SnapshotFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    SnapshotFactory eINSTANCE = org.eclipse.viatra.query.testing.snapshot.impl.SnapshotFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Match Set Record</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Match Set Record</em>'.
     * @generated
     */
    MatchSetRecord createMatchSetRecord();

    /**
     * Returns a new object of class '<em>Match Record</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Match Record</em>'.
     * @generated
     */
    MatchRecord createMatchRecord();

    /**
     * Returns a new object of class '<em>EMF Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Substitution</em>'.
     * @generated
     */
    EMFSubstitution createEMFSubstitution();

    /**
     * Returns a new object of class '<em>Int Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Int Substitution</em>'.
     * @generated
     */
    IntSubstitution createIntSubstitution();

    /**
     * Returns a new object of class '<em>Long Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Long Substitution</em>'.
     * @generated
     */
    LongSubstitution createLongSubstitution();

    /**
     * Returns a new object of class '<em>Double Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Double Substitution</em>'.
     * @generated
     */
    DoubleSubstitution createDoubleSubstitution();

    /**
     * Returns a new object of class '<em>Float Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Float Substitution</em>'.
     * @generated
     */
    FloatSubstitution createFloatSubstitution();

    /**
     * Returns a new object of class '<em>Boolean Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Boolean Substitution</em>'.
     * @generated
     */
    BooleanSubstitution createBooleanSubstitution();

    /**
     * Returns a new object of class '<em>String Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>String Substitution</em>'.
     * @generated
     */
    StringSubstitution createStringSubstitution();

    /**
     * Returns a new object of class '<em>Date Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Date Substitution</em>'.
     * @generated
     */
    DateSubstitution createDateSubstitution();

    /**
     * Returns a new object of class '<em>Enum Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Enum Substitution</em>'.
     * @generated
     */
    EnumSubstitution createEnumSubstitution();

    /**
     * Returns a new object of class '<em>Miscellaneous Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Miscellaneous Substitution</em>'.
     * @generated
     */
    MiscellaneousSubstitution createMiscellaneousSubstitution();

    /**
     * Returns a new object of class '<em>Query Snapshot</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Query Snapshot</em>'.
     * @generated
     */
    QuerySnapshot createQuerySnapshot();

    /**
     * Returns a new object of class '<em>Serialized Java Object Substitution</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Serialized Java Object Substitution</em>'.
     * @generated
     */
    SerializedJavaObjectSubstitution createSerializedJavaObjectSubstitution();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    SnapshotPackage getSnapshotPackage();

} //SnapshotFactory
