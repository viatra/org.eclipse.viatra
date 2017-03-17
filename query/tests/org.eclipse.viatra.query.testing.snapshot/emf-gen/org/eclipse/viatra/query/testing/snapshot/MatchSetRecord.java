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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Match Set Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getPatternQualifiedName <em>Pattern Qualified Name</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getMatches <em>Matches</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getFilter <em>Filter</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSetRecord()
 * @model
 * @generated
 */
public interface MatchSetRecord extends EObject {
    /**
     * Returns the value of the '<em><b>Pattern Qualified Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pattern Qualified Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pattern Qualified Name</em>' attribute.
     * @see #setPatternQualifiedName(String)
     * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSetRecord_PatternQualifiedName()
     * @model
     * @generated
     */
    String getPatternQualifiedName();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getPatternQualifiedName <em>Pattern Qualified Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pattern Qualified Name</em>' attribute.
     * @see #getPatternQualifiedName()
     * @generated
     */
    void setPatternQualifiedName(String value);

    /**
     * Returns the value of the '<em><b>Matches</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.query.testing.snapshot.MatchRecord}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Matches</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Matches</em>' containment reference list.
     * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSetRecord_Matches()
     * @model containment="true" ordered="false"
     * @generated
     */
    EList<MatchRecord> getMatches();

    /**
     * Returns the value of the '<em><b>Filter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Filter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Filter</em>' containment reference.
     * @see #setFilter(MatchRecord)
     * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSetRecord_Filter()
     * @model containment="true"
     * @generated
     */
    MatchRecord getFilter();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getFilter <em>Filter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Filter</em>' containment reference.
     * @see #getFilter()
     * @generated
     */
    void setFilter(MatchRecord value);

} // MatchSetRecord
