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

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.BookCategory;
import org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage;
import org.eclipse.incquery.examples.eiqlibrary.Writer;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.BookImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.BookImpl#getPages <em>Pages</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.BookImpl#getCategory <em>Category</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.impl.BookImpl#getAuthors <em>Authors</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BookImpl extends MinimalEObjectImpl.Container implements Book {
    /**
     * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTitle()
     * @generated
     * @ordered
     */
    protected static final String TITLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTitle()
     * @generated
     * @ordered
     */
    protected String title = TITLE_EDEFAULT;

    /**
     * The default value of the '{@link #getPages() <em>Pages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPages()
     * @generated
     * @ordered
     */
    protected static final int PAGES_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPages() <em>Pages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPages()
     * @generated
     * @ordered
     */
    protected int pages = PAGES_EDEFAULT;

    /**
     * The cached value of the '{@link #getCategory() <em>Category</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCategory()
     * @generated
     * @ordered
     */
    protected EList<BookCategory> category;

    /**
     * The cached value of the '{@link #getAuthors() <em>Authors</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAuthors()
     * @generated
     * @ordered
     */
    protected EList<Writer> authors;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BookImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EIQLibraryPackage.Literals.BOOK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTitle() {
        return title;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTitle(String newTitle) {
        String oldTitle = title;
        title = newTitle;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EIQLibraryPackage.BOOK__TITLE, oldTitle, title));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getPages() {
        return pages;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPages(int newPages) {
        int oldPages = pages;
        pages = newPages;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EIQLibraryPackage.BOOK__PAGES, oldPages, pages));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<BookCategory> getCategory() {
        if (category == null) {
            category = new EDataTypeUniqueEList<BookCategory>(BookCategory.class, this, EIQLibraryPackage.BOOK__CATEGORY);
        }
        return category;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Writer> getAuthors() {
        if (authors == null) {
            authors = new EObjectWithInverseResolvingEList.ManyInverse<Writer>(Writer.class, this, EIQLibraryPackage.BOOK__AUTHORS, EIQLibraryPackage.WRITER__BOOKS);
        }
        return authors;
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
            case EIQLibraryPackage.BOOK__AUTHORS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getAuthors()).basicAdd(otherEnd, msgs);
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
            case EIQLibraryPackage.BOOK__AUTHORS:
                return ((InternalEList<?>)getAuthors()).basicRemove(otherEnd, msgs);
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
            case EIQLibraryPackage.BOOK__TITLE:
                return getTitle();
            case EIQLibraryPackage.BOOK__PAGES:
                return getPages();
            case EIQLibraryPackage.BOOK__CATEGORY:
                return getCategory();
            case EIQLibraryPackage.BOOK__AUTHORS:
                return getAuthors();
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
            case EIQLibraryPackage.BOOK__TITLE:
                setTitle((String)newValue);
                return;
            case EIQLibraryPackage.BOOK__PAGES:
                setPages((Integer)newValue);
                return;
            case EIQLibraryPackage.BOOK__CATEGORY:
                getCategory().clear();
                getCategory().addAll((Collection<? extends BookCategory>)newValue);
                return;
            case EIQLibraryPackage.BOOK__AUTHORS:
                getAuthors().clear();
                getAuthors().addAll((Collection<? extends Writer>)newValue);
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
            case EIQLibraryPackage.BOOK__TITLE:
                setTitle(TITLE_EDEFAULT);
                return;
            case EIQLibraryPackage.BOOK__PAGES:
                setPages(PAGES_EDEFAULT);
                return;
            case EIQLibraryPackage.BOOK__CATEGORY:
                getCategory().clear();
                return;
            case EIQLibraryPackage.BOOK__AUTHORS:
                getAuthors().clear();
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
            case EIQLibraryPackage.BOOK__TITLE:
                return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
            case EIQLibraryPackage.BOOK__PAGES:
                return pages != PAGES_EDEFAULT;
            case EIQLibraryPackage.BOOK__CATEGORY:
                return category != null && !category.isEmpty();
            case EIQLibraryPackage.BOOK__AUTHORS:
                return authors != null && !authors.isEmpty();
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
        result.append(" (title: ");
        result.append(title);
        result.append(", pages: ");
        result.append(pages);
        result.append(", category: ");
        result.append(category);
        result.append(')');
        return result.toString();
    }

} //BookImpl
