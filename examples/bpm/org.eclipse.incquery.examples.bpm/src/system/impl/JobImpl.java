/**
 */
package system.impl;

import java.util.Collection;

import operation.RuntimeInformation;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import process.Task;

import system.Interface;
import system.Job;
import system.SystemPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Job</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link system.impl.JobImpl#getRunsOn <em>Runs On</em>}</li>
 *   <li>{@link system.impl.JobImpl#getCalls <em>Calls</em>}</li>
 *   <li>{@link system.impl.JobImpl#getTaskIds <em>Task Ids</em>}</li>
 *   <li>{@link system.impl.JobImpl#getTasks <em>Tasks</em>}</li>
 *   <li>{@link system.impl.JobImpl#getInfo <em>Info</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JobImpl extends ResourceElementImpl implements Job {
	/**
	 * The cached value of the '{@link #getRunsOn() <em>Runs On</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRunsOn()
	 * @generated
	 * @ordered
	 */
	protected system.System runsOn;

	/**
	 * The cached value of the '{@link #getCalls() <em>Calls</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalls()
	 * @generated
	 * @ordered
	 */
	protected EList<Interface> calls;

	/**
	 * The cached value of the '{@link #getTaskIds() <em>Task Ids</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTaskIds()
	 * @generated
	 * @ordered
	 */
	protected EList<String> taskIds;

	/**
	 * The cached setting delegate for the '{@link #getTasks() <em>Tasks</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTasks()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate TASKS__ESETTING_DELEGATE = ((EStructuralFeature.Internal)SystemPackage.Literals.JOB__TASKS).getSettingDelegate();

	/**
	 * The cached setting delegate for the '{@link #getInfo() <em>Info</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInfo()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate INFO__ESETTING_DELEGATE = ((EStructuralFeature.Internal)SystemPackage.Literals.JOB__INFO).getSettingDelegate();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JobImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SystemPackage.Literals.JOB;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public system.System getRunsOn() {
		if (runsOn != null && runsOn.eIsProxy()) {
			InternalEObject oldRunsOn = (InternalEObject)runsOn;
			runsOn = (system.System)eResolveProxy(oldRunsOn);
			if (runsOn != oldRunsOn) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, SystemPackage.JOB__RUNS_ON, oldRunsOn, runsOn));
			}
		}
		return runsOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public system.System basicGetRunsOn() {
		return runsOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRunsOn(system.System newRunsOn) {
		system.System oldRunsOn = runsOn;
		runsOn = newRunsOn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SystemPackage.JOB__RUNS_ON, oldRunsOn, runsOn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Interface> getCalls() {
		if (calls == null) {
			calls = new EObjectResolvingEList<Interface>(Interface.class, this, SystemPackage.JOB__CALLS);
		}
		return calls;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getTaskIds() {
		if (taskIds == null) {
			taskIds = new EDataTypeUniqueEList<String>(String.class, this, SystemPackage.JOB__TASK_IDS);
		}
		return taskIds;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<Task> getTasks() {
		return (EList<Task>)TASKS__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<RuntimeInformation> getInfo() {
		return (EList<RuntimeInformation>)INFO__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SystemPackage.JOB__RUNS_ON:
				if (resolve) return getRunsOn();
				return basicGetRunsOn();
			case SystemPackage.JOB__CALLS:
				return getCalls();
			case SystemPackage.JOB__TASK_IDS:
				return getTaskIds();
			case SystemPackage.JOB__TASKS:
				return getTasks();
			case SystemPackage.JOB__INFO:
				return getInfo();
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
			case SystemPackage.JOB__RUNS_ON:
				setRunsOn((system.System)newValue);
				return;
			case SystemPackage.JOB__CALLS:
				getCalls().clear();
				getCalls().addAll((Collection<? extends Interface>)newValue);
				return;
			case SystemPackage.JOB__TASK_IDS:
				getTaskIds().clear();
				getTaskIds().addAll((Collection<? extends String>)newValue);
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
			case SystemPackage.JOB__RUNS_ON:
				setRunsOn((system.System)null);
				return;
			case SystemPackage.JOB__CALLS:
				getCalls().clear();
				return;
			case SystemPackage.JOB__TASK_IDS:
				getTaskIds().clear();
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
			case SystemPackage.JOB__RUNS_ON:
				return runsOn != null;
			case SystemPackage.JOB__CALLS:
				return calls != null && !calls.isEmpty();
			case SystemPackage.JOB__TASK_IDS:
				return taskIds != null && !taskIds.isEmpty();
			case SystemPackage.JOB__TASKS:
				return TASKS__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
			case SystemPackage.JOB__INFO:
				return INFO__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
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
		result.append(" (taskIds: ");
		result.append(taskIds);
		result.append(')');
		return result.toString();
	}

} //JobImpl
