/**
 */
package system.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import process.Task;

import system.Data;
import system.SystemPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link system.impl.DataImpl#getReadingTaskIds <em>Reading Task Ids</em>}</li>
 *   <li>{@link system.impl.DataImpl#getWritingTaskIds <em>Writing Task Ids</em>}</li>
 *   <li>{@link system.impl.DataImpl#getWritingTask <em>Writing Task</em>}</li>
 *   <li>{@link system.impl.DataImpl#getReadingTask <em>Reading Task</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataImpl extends ResourceElementImpl implements Data {
	/**
	 * The cached value of the '{@link #getReadingTaskIds() <em>Reading Task Ids</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReadingTaskIds()
	 * @generated
	 * @ordered
	 */
	protected EList<String> readingTaskIds;

	/**
	 * The cached value of the '{@link #getWritingTaskIds() <em>Writing Task Ids</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWritingTaskIds()
	 * @generated
	 * @ordered
	 */
	protected EList<String> writingTaskIds;

	/**
	 * The cached setting delegate for the '{@link #getWritingTask() <em>Writing Task</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWritingTask()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate WRITING_TASK__ESETTING_DELEGATE = ((EStructuralFeature.Internal)SystemPackage.Literals.DATA__WRITING_TASK).getSettingDelegate();

	/**
	 * The cached setting delegate for the '{@link #getReadingTask() <em>Reading Task</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReadingTask()
	 * @generated
	 * @ordered
	 */
	protected EStructuralFeature.Internal.SettingDelegate READING_TASK__ESETTING_DELEGATE = ((EStructuralFeature.Internal)SystemPackage.Literals.DATA__READING_TASK).getSettingDelegate();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SystemPackage.Literals.DATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getReadingTaskIds() {
		if (readingTaskIds == null) {
			readingTaskIds = new EDataTypeUniqueEList<String>(String.class, this, SystemPackage.DATA__READING_TASK_IDS);
		}
		return readingTaskIds;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getWritingTaskIds() {
		if (writingTaskIds == null) {
			writingTaskIds = new EDataTypeUniqueEList<String>(String.class, this, SystemPackage.DATA__WRITING_TASK_IDS);
		}
		return writingTaskIds;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<Task> getWritingTask() {
		return (EList<Task>)WRITING_TASK__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<Task> getReadingTask() {
		return (EList<Task>)READING_TASK__ESETTING_DELEGATE.dynamicGet(this, null, 0, true, false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SystemPackage.DATA__READING_TASK_IDS:
				return getReadingTaskIds();
			case SystemPackage.DATA__WRITING_TASK_IDS:
				return getWritingTaskIds();
			case SystemPackage.DATA__WRITING_TASK:
				return getWritingTask();
			case SystemPackage.DATA__READING_TASK:
				return getReadingTask();
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
			case SystemPackage.DATA__READING_TASK_IDS:
				getReadingTaskIds().clear();
				getReadingTaskIds().addAll((Collection<? extends String>)newValue);
				return;
			case SystemPackage.DATA__WRITING_TASK_IDS:
				getWritingTaskIds().clear();
				getWritingTaskIds().addAll((Collection<? extends String>)newValue);
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
			case SystemPackage.DATA__READING_TASK_IDS:
				getReadingTaskIds().clear();
				return;
			case SystemPackage.DATA__WRITING_TASK_IDS:
				getWritingTaskIds().clear();
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
			case SystemPackage.DATA__READING_TASK_IDS:
				return readingTaskIds != null && !readingTaskIds.isEmpty();
			case SystemPackage.DATA__WRITING_TASK_IDS:
				return writingTaskIds != null && !writingTaskIds.isEmpty();
			case SystemPackage.DATA__WRITING_TASK:
				return WRITING_TASK__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
			case SystemPackage.DATA__READING_TASK:
				return READING_TASK__ESETTING_DELEGATE.dynamicIsSet(this, null, 0);
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
		result.append(" (readingTaskIds: ");
		result.append(readingTaskIds);
		result.append(", writingTaskIds: ");
		result.append(writingTaskIds);
		result.append(')');
		return result.toString();
	}

} //DataImpl
