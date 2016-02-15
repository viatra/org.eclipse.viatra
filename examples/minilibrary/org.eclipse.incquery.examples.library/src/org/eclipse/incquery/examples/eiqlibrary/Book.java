/**
 */
package org.eclipse.incquery.examples.eiqlibrary;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Book#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Book#getPages <em>Pages</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Book#getCategory <em>Category</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Book#getAuthors <em>Authors</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getBook()
 * @model
 * @generated
 */
public interface Book extends EObject {
    /**
     * Returns the value of the '<em><b>Title</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Title</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Title</em>' attribute.
     * @see #setTitle(String)
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getBook_Title()
     * @model
     * @generated
     */
    String getTitle();

    /**
     * Sets the value of the '{@link org.eclipse.incquery.examples.eiqlibrary.Book#getTitle <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Title</em>' attribute.
     * @see #getTitle()
     * @generated
     */
    void setTitle(String value);

    /**
     * Returns the value of the '<em><b>Pages</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pages</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pages</em>' attribute.
     * @see #setPages(int)
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getBook_Pages()
     * @model
     * @generated
     */
    int getPages();

    /**
     * Sets the value of the '{@link org.eclipse.incquery.examples.eiqlibrary.Book#getPages <em>Pages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pages</em>' attribute.
     * @see #getPages()
     * @generated
     */
    void setPages(int value);

    /**
     * Returns the value of the '<em><b>Category</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.incquery.examples.eiqlibrary.BookCategory}.
     * The literals are from the enumeration {@link org.eclipse.incquery.examples.eiqlibrary.BookCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Category</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Category</em>' attribute list.
     * @see org.eclipse.incquery.examples.eiqlibrary.BookCategory
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getBook_Category()
     * @model
     * @generated
     */
    EList<BookCategory> getCategory();

    /**
     * Returns the value of the '<em><b>Authors</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.incquery.examples.eiqlibrary.Writer}.
     * It is bidirectional and its opposite is '{@link org.eclipse.incquery.examples.eiqlibrary.Writer#getBooks <em>Books</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Authors</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Authors</em>' reference list.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getBook_Authors()
     * @see org.eclipse.incquery.examples.eiqlibrary.Writer#getBooks
     * @model opposite="books"
     * @generated
     */
    EList<Writer> getAuthors();

} // Book
