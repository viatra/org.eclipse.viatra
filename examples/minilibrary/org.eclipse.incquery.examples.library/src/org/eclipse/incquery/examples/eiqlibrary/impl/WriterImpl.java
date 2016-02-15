/**
 */
package org.eclipse.incquery.examples.eiqlibrary.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage;
import org.eclipse.incquery.examples.eiqlibrary.Writer;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Writer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.WriterImpl#getBooks <em>Books</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.WriterImpl#getFirstBook <em>First Book</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.WriterImpl#getScifiBooks <em>Scifi Books</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.WriterImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WriterImpl extends MinimalEObjectImpl.Container implements Writer {
    /**
     * The cached value of the '{@link #getBooks() <em>Books</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBooks()
     * @generated
     * @ordered
     */
    protected EList<Book> books;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WriterImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EIQLibraryPackage.Literals.WRITER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Book> getBooks() {
        if (books == null) {
            books = new EObjectWithInverseResolvingEList.ManyInverse<Book>(Book.class, this, EIQLibraryPackage.WRITER__BOOKS, EIQLibraryPackage.BOOK__AUTHORS);
        }
        return books;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Book getFirstBook() {
        Book firstBook = basicGetFirstBook();
        return firstBook != null && firstBook.eIsProxy() ? (Book)eResolveProxy((InternalEObject)firstBook) : firstBook;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Book basicGetFirstBook() {
        EList<Book> allBooks = getBooks();
        if(!allBooks.isEmpty()){
          return allBooks.get(0);
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Book> getScifiBooks() {
        EList<Book> allBooks = getBooks();
        java.util.List<Book> scifiBooks = new java.util.ArrayList<Book>();
        for (Book book : allBooks) {
          if(book.getCategory().contains(org.eclipse.incquery.examples.eiqlibrary.BookCategory.SCI_FI)) {
             scifiBooks.add(book);
          }
        }
        return org.eclipse.emf.common.util.ECollections.asEList(scifiBooks);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EIQLibraryPackage.WRITER__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EIQLibraryPackage.WRITER__BOOKS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getBooks()).basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EIQLibraryPackage.WRITER__BOOKS:
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
            case EIQLibraryPackage.WRITER__BOOKS:
                return getBooks();
            case EIQLibraryPackage.WRITER__FIRST_BOOK:
                if (resolve) return getFirstBook();
                return basicGetFirstBook();
            case EIQLibraryPackage.WRITER__SCIFI_BOOKS:
                return getScifiBooks();
            case EIQLibraryPackage.WRITER__NAME:
                return getName();
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
            case EIQLibraryPackage.WRITER__BOOKS:
                getBooks().clear();
                getBooks().addAll((Collection<? extends Book>)newValue);
                return;
            case EIQLibraryPackage.WRITER__NAME:
                setName((String)newValue);
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
            case EIQLibraryPackage.WRITER__BOOKS:
                getBooks().clear();
                return;
            case EIQLibraryPackage.WRITER__NAME:
                setName(NAME_EDEFAULT);
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
            case EIQLibraryPackage.WRITER__BOOKS:
                return books != null && !books.isEmpty();
            case EIQLibraryPackage.WRITER__FIRST_BOOK:
                return basicGetFirstBook() != null;
            case EIQLibraryPackage.WRITER__SCIFI_BOOKS:
                return !getScifiBooks().isEmpty();
            case EIQLibraryPackage.WRITER__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
        result.append(" (name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //WriterImpl
