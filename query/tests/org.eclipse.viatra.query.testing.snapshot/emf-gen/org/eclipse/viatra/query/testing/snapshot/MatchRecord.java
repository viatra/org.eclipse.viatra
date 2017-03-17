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
 * A representation of the model object '<em><b>Match Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchRecord#getSubstitutions <em>Substitutions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchRecord()
 * @model
 * @generated
 */
public interface MatchRecord extends EObject {
    /**
     * Returns the value of the '<em><b>Substitutions</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Substitutions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Substitutions</em>' containment reference list.
     * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchRecord_Substitutions()
     * @model containment="true"
     * @generated
     */
    EList<MatchSubstitutionRecord> getSubstitutions();

} // MatchRecord
