/**
 */
package org.eclipse.incquery.xcore.model.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.emf.ecore.xcore.impl.XPackageImpl;

import org.eclipse.incquery.xcore.model.XIncQueryImport;
import org.eclipse.incquery.xcore.model.XIncQueryPackage;
import org.eclipse.incquery.xcore.model.XcorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XInc Query Package</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.incquery.xcore.model.impl.XIncQueryPackageImpl#getImportedIncQueries <em>Imported Inc Queries</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XIncQueryPackageImpl extends XPackageImpl implements XIncQueryPackage {
	/**
	 * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected int eFlags = 0;

	/**
	 * The cached value of the '{@link #getImportedIncQueries() <em>Imported Inc Queries</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportedIncQueries()
	 * @generated
	 * @ordered
	 */
	protected EList<XIncQueryImport> importedIncQueries;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected XIncQueryPackageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return XcorePackage.Literals.XINC_QUERY_PACKAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<XIncQueryImport> getImportedIncQueries() {
		if (importedIncQueries == null) {
			importedIncQueries = new EObjectContainmentEList<XIncQueryImport>(XIncQueryImport.class, this, XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES);
		}
		return importedIncQueries;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES:
				return ((InternalEList<?>)getImportedIncQueries()).basicRemove(otherEnd, msgs);
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
			case XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES:
				return getImportedIncQueries();
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
			case XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES:
				getImportedIncQueries().clear();
				getImportedIncQueries().addAll((Collection<? extends XIncQueryImport>)newValue);
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
			case XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES:
				getImportedIncQueries().clear();
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
			case XcorePackage.XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES:
				return importedIncQueries != null && !importedIncQueries.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //XIncQueryPackageImpl
