/**
 */
package operation.impl;

import java.util.Collection;

import operation.Checklist;
import operation.ChecklistEntry;
import operation.MenuItem;
import operation.OperationPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Checklist</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link operation.impl.ChecklistImpl#getEntries <em>Entries</em>}</li>
 *   <li>{@link operation.impl.ChecklistImpl#getMenu <em>Menu</em>}</li>
 *   <li>{@link operation.impl.ChecklistImpl#getProcess <em>Process</em>}</li>
 *   <li>{@link operation.impl.ChecklistImpl#getProcessId <em>Process Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChecklistImpl extends OperationElementImpl implements Checklist {
	/**
	 * The cached value of the '{@link #getEntries() <em>Entries</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntries()
	 * @generated
	 * @ordered
	 */
	protected EList<ChecklistEntry> entries;

	/**
	 * The cached value of the '{@link #getMenu() <em>Menu</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMenu()
	 * @generated
	 * @ordered
	 */
	protected EList<MenuItem> menu;

	/**
	 * The cached setting delegate for the '{@link #getProcess() <em>Process</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcess()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate PROCESS__ESETTING_DELEGATE = ((EStructuralFeature.Internal)OperationPackage.Literals.CHECKLIST__PROCESS).getSettingDelegate();

	/**
	 * The default value of the '{@link #getProcessId() <em>Process Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessId()
	 * @generated
	 * @ordered
	 */
	protected static final String PROCESS_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProcessId() <em>Process Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessId()
	 * @generated
	 * @ordered
	 */
	protected String processId = PROCESS_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChecklistImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OperationPackage.Literals.CHECKLIST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ChecklistEntry> getEntries() {
		if (entries == null) {
			entries = new EObjectContainmentEList<ChecklistEntry>(ChecklistEntry.class, this, OperationPackage.CHECKLIST__ENTRIES);
		}
		return entries;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MenuItem> getMenu() {
		if (menu == null) {
			menu = new EObjectContainmentEList<MenuItem>(MenuItem.class, this, OperationPackage.CHECKLIST__MENU);
		}
		return menu;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public process.Process getProcess() {
		return (process.Process)PROCESS__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public process.Process basicGetProcess() {
		return (process.Process)PROCESS__ESETTING_DELEGATE.dynamicGet(this, null, 0, false, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcessId(String newProcessId) {
		String oldProcessId = processId;
		processId = newProcessId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationPackage.CHECKLIST__PROCESS_ID, oldProcessId, processId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OperationPackage.CHECKLIST__ENTRIES:
				return ((InternalEList<?>)getEntries()).basicRemove(otherEnd, msgs);
			case OperationPackage.CHECKLIST__MENU:
				return ((InternalEList<?>)getMenu()).basicRemove(otherEnd, msgs);
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
			case OperationPackage.CHECKLIST__ENTRIES:
				return getEntries();
			case OperationPackage.CHECKLIST__MENU:
				return getMenu();
			case OperationPackage.CHECKLIST__PROCESS:
				if (resolve) return getProcess();
				return basicGetProcess();
			case OperationPackage.CHECKLIST__PROCESS_ID:
				return getProcessId();
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
			case OperationPackage.CHECKLIST__ENTRIES:
				getEntries().clear();
				getEntries().addAll((Collection<? extends ChecklistEntry>)newValue);
				return;
			case OperationPackage.CHECKLIST__MENU:
				getMenu().clear();
				getMenu().addAll((Collection<? extends MenuItem>)newValue);
				return;
			case OperationPackage.CHECKLIST__PROCESS_ID:
				setProcessId((String)newValue);
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
			case OperationPackage.CHECKLIST__ENTRIES:
				getEntries().clear();
				return;
			case OperationPackage.CHECKLIST__MENU:
				getMenu().clear();
				return;
			case OperationPackage.CHECKLIST__PROCESS_ID:
				setProcessId(PROCESS_ID_EDEFAULT);
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
			case OperationPackage.CHECKLIST__ENTRIES:
				return entries != null && !entries.isEmpty();
			case OperationPackage.CHECKLIST__MENU:
				return menu != null && !menu.isEmpty();
			case OperationPackage.CHECKLIST__PROCESS:
				return PROCESS__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
			case OperationPackage.CHECKLIST__PROCESS_ID:
				return PROCESS_ID_EDEFAULT == null ? processId != null : !PROCESS_ID_EDEFAULT.equals(processId);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (processId: ");
		result.append(processId);
		result.append(')');
		return result.toString();
	}

} //ChecklistImpl
