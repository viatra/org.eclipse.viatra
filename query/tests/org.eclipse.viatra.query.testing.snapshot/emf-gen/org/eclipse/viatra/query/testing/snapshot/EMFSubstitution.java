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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EMF Substitution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.EMFSubstitution#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getEMFSubstitution()
 * @model
 * @generated
 */
public interface EMFSubstitution extends MatchSubstitutionRecord {
    /**
     * Returns the value of the '<em><b>Value</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' reference.
     * @see #setValue(EObject)
     * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getEMFSubstitution_Value()
     * @model
     * @generated
     */
    EObject getValue();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.EMFSubstitution#getValue <em>Value</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' reference.
     * @see #getValue()
     * @generated
     */
    void setValue(EObject value);

} // EMFSubstitution
