/**
 */
package org.eclipse.viatra.examples.library.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.examples.library.LibraryPackage;
import org.eclipse.viatra.examples.library.Writer;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Library</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getAddress <em>Address</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getWriters <em>Writers</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getSumOfPages <em>Sum Of Pages</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getBooks <em>Books</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getInternalRequestCount <em>Internal Request Count</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getRequestCount <em>Request Count</em>}</li>
 *   <li>{@link org.eclipse.viatra.examples.library.impl.LibraryImpl#getSomeBooks <em>Some Books</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LibraryImpl extends MinimalEObjectImpl.Container implements Library {
	/**
	 * The default value of the '{@link #getAddress() <em>Address</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAddress()
	 * @generated
	 * @ordered
	 */
	protected static final String ADDRESS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAddress() <em>Address</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAddress()
	 * @generated
	 * @ordered
	 */
	protected String address = ADDRESS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getWriters() <em>Writers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWriters()
	 * @generated
	 * @ordered
	 */
	protected EList<Writer> writers;

	/**
	 * The default value of the '{@link #getSumOfPages() <em>Sum Of Pages</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSumOfPages()
	 * @generated
	 * @ordered
	 */
	protected static final int SUM_OF_PAGES_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBooks() <em>Books</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBooks()
	 * @generated
	 * @ordered
	 */
	protected EList<Book> books;

	/**
	 * The default value of the '{@link #getInternalRequestCount() <em>Internal Request Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalRequestCount()
	 * @generated
	 * @ordered
	 */
	protected static final int INTERNAL_REQUEST_COUNT_EDEFAULT = 1;

	/**
	 * The cached value of the '{@link #getInternalRequestCount() <em>Internal Request Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalRequestCount()
	 * @generated
	 * @ordered
	 */
	protected int internalRequestCount = INTERNAL_REQUEST_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getRequestCount() <em>Request Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequestCount()
	 * @generated
	 * @ordered
	 */
	protected static final int REQUEST_COUNT_EDEFAULT = 0;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LibraryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LibraryPackage.Literals.LIBRARY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAddress(String newAddress) {
		String oldAddress = address;
		address = newAddress;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LibraryPackage.LIBRARY__ADDRESS, oldAddress, address));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Writer> getWriters() {
		if (writers == null) {
			writers = new EObjectContainmentEList<Writer>(Writer.class, this, LibraryPackage.LIBRARY__WRITERS);
		}
		return writers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getSumOfPages() {
		EList<Book> allBooks = getBooks();
		int sumOfPages = 0;
		for (Book book : allBooks) {
		  sumOfPages += book.getPages();
		}
		return sumOfPages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Book> getBooks() {
		if (books == null) {
			books = new EObjectContainmentEList<Book>(Book.class, this, LibraryPackage.LIBRARY__BOOKS);
		}
		return books;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getInternalRequestCount() {
		return internalRequestCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInternalRequestCount(int newInternalRequestCount) {
		int oldInternalRequestCount = internalRequestCount;
		internalRequestCount = newInternalRequestCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LibraryPackage.LIBRARY__INTERNAL_REQUEST_COUNT, oldInternalRequestCount, internalRequestCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getRequestCount() {
		internalRequestCount++;
		return internalRequestCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Book> getSomeBooks() {
		int count = internalRequestCount;
		java.util.List<Book> someBooks = new java.util.ArrayList<Book>();
		
		Book[] books = getBooks().toArray(new Book[0]);
		for (int i = 0; i < books.length; i++) {
		    Book book = books[i];
		    if(i%count == 0) {
		        someBooks.add(book);
		    }
		}
		return org.eclipse.emf.common.util.ECollections.asEList(someBooks);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LibraryPackage.LIBRARY__WRITERS:
				return ((InternalEList<?>)getWriters()).basicRemove(otherEnd, msgs);
			case LibraryPackage.LIBRARY__BOOKS:
				return ((InternalEList<?>)getBooks()).basicRemove(otherEnd, msgs);
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
			case LibraryPackage.LIBRARY__ADDRESS:
				return getAddress();
			case LibraryPackage.LIBRARY__WRITERS:
				return getWriters();
			case LibraryPackage.LIBRARY__SUM_OF_PAGES:
				return getSumOfPages();
			case LibraryPackage.LIBRARY__BOOKS:
				return getBooks();
			case LibraryPackage.LIBRARY__INTERNAL_REQUEST_COUNT:
				return getInternalRequestCount();
			case LibraryPackage.LIBRARY__REQUEST_COUNT:
				return getRequestCount();
			case LibraryPackage.LIBRARY__SOME_BOOKS:
				return getSomeBooks();
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
			case LibraryPackage.LIBRARY__ADDRESS:
				setAddress((String)newValue);
				return;
			case LibraryPackage.LIBRARY__WRITERS:
				getWriters().clear();
				getWriters().addAll((Collection<? extends Writer>)newValue);
				return;
			case LibraryPackage.LIBRARY__BOOKS:
				getBooks().clear();
				getBooks().addAll((Collection<? extends Book>)newValue);
				return;
			case LibraryPackage.LIBRARY__INTERNAL_REQUEST_COUNT:
				setInternalRequestCount((Integer)newValue);
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
			case LibraryPackage.LIBRARY__ADDRESS:
				setAddress(ADDRESS_EDEFAULT);
				return;
			case LibraryPackage.LIBRARY__WRITERS:
				getWriters().clear();
				return;
			case LibraryPackage.LIBRARY__BOOKS:
				getBooks().clear();
				return;
			case LibraryPackage.LIBRARY__INTERNAL_REQUEST_COUNT:
				setInternalRequestCount(INTERNAL_REQUEST_COUNT_EDEFAULT);
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
			case LibraryPackage.LIBRARY__ADDRESS:
				return ADDRESS_EDEFAULT == null ? address != null : !ADDRESS_EDEFAULT.equals(address);
			case LibraryPackage.LIBRARY__WRITERS:
				return writers != null && !writers.isEmpty();
			case LibraryPackage.LIBRARY__SUM_OF_PAGES:
				return getSumOfPages() != SUM_OF_PAGES_EDEFAULT;
			case LibraryPackage.LIBRARY__BOOKS:
				return books != null && !books.isEmpty();
			case LibraryPackage.LIBRARY__INTERNAL_REQUEST_COUNT:
				return internalRequestCount != INTERNAL_REQUEST_COUNT_EDEFAULT;
			case LibraryPackage.LIBRARY__REQUEST_COUNT:
				return getRequestCount() != REQUEST_COUNT_EDEFAULT;
			case LibraryPackage.LIBRARY__SOME_BOOKS:
				return !getSomeBooks().isEmpty();
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
		result.append(" (address: ");
		result.append(address);
		result.append(", internalRequestCount: ");
		result.append(internalRequestCount);
		result.append(')');
		return result.toString();
	}

} //LibraryImpl
