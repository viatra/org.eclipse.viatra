/**
 */
package org.eclipse.viatra.query.testing.snapshot.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.query.testing.snapshot.MatchRecord;
import org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord;
import org.eclipse.viatra.query.testing.snapshot.RecordRole;
import org.eclipse.viatra.query.testing.snapshot.SnapshotPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Match Record</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl#getSubstitutions <em>Substitutions</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl#getRole <em>Role</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MatchRecordImpl extends MinimalEObjectImpl.Container implements MatchRecord {
	/**
	 * The cached value of the '{@link #getSubstitutions() <em>Substitutions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubstitutions()
	 * @generated
	 * @ordered
	 */
	protected EList<MatchSubstitutionRecord> substitutions;

	/**
	 * The cached setting delegate for the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate ROLE__ESETTING_DELEGATE = ((EStructuralFeature.Internal)SnapshotPackage.Literals.MATCH_RECORD__ROLE).getSettingDelegate();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MatchRecordImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnapshotPackage.Literals.MATCH_RECORD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MatchSubstitutionRecord> getSubstitutions() {
		if (substitutions == null) {
			substitutions = new EObjectContainmentEList<MatchSubstitutionRecord>(MatchSubstitutionRecord.class, this, SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS);
		}
		return substitutions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecordRole getRole() {
		return (RecordRole)ROLE__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return ((InternalEList<?>)getSubstitutions()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return getSubstitutions();
			case SnapshotPackage.MATCH_RECORD__ROLE:
				return getRole();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				getSubstitutions().clear();
				getSubstitutions().addAll((Collection<? extends MatchSubstitutionRecord>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				getSubstitutions().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case SnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return substitutions != null && !substitutions.isEmpty();
			case SnapshotPackage.MATCH_RECORD__ROLE:
				return ROLE__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
		}
		return super.eIsSet(featureID);
	}

} //MatchRecordImpl
