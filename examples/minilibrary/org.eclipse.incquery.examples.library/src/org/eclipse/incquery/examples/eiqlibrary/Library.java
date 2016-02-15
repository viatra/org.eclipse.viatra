/**
 */
package org.eclipse.incquery.examples.eiqlibrary;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Library</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getAddress <em>Address</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getWriters <em>Writers</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getSumOfPages <em>Sum Of Pages</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getBooks <em>Books</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getRequestCount <em>Request Count</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Library#getSomeBooks <em>Some Books</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary()
 * @model features="internalRequestCount" 
 *        internalRequestCountDefault="1" internalRequestCountDataType="org.eclipse.emf.ecore.EInt" internalRequestCountTransient="true" internalRequestCountSuppressedGetVisibility="true" internalRequestCountSuppressedSetVisibility="true"
 * @generated
 */
public interface Library extends EObject {
    /**
     * Returns the value of the '<em><b>Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Address</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Address</em>' attribute.
     * @see #setAddress(String)
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_Address()
     * @model
     * @generated
     */
    String getAddress();

    /**
     * Sets the value of the '{@link org.eclipse.incquery.examples.eiqlibrary.Library#getAddress <em>Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Address</em>' attribute.
     * @see #getAddress()
     * @generated
     */
    void setAddress(String value);

    /**
     * Returns the value of the '<em><b>Writers</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.incquery.examples.eiqlibrary.Writer}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Writers</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Writers</em>' containment reference list.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_Writers()
     * @model containment="true"
     * @generated
     */
    EList<Writer> getWriters();

    /**
     * Returns the value of the '<em><b>Sum Of Pages</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sum Of Pages</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sum Of Pages</em>' attribute.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_SumOfPages()
     * @model transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="http://www.eclipse.org/emf/2002/GenModel get='EList<Book> allBooks = getBooks();\r\nint sumOfPages = 0;\r\nfor (Book book : allBooks) {\r\n  sumOfPages += book.getPages();\r\n}\r\nreturn sumOfPages;'"
     * @generated
     */
    int getSumOfPages();

    /**
     * Returns the value of the '<em><b>Books</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.incquery.examples.eiqlibrary.Book}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Books</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Books</em>' containment reference list.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_Books()
     * @model containment="true"
     * @generated
     */
    EList<Book> getBooks();

    /**
     * Returns the value of the '<em><b>Request Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Request Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Request Count</em>' attribute.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_RequestCount()
     * @model transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="http://www.eclipse.org/emf/2002/GenModel get='internalRequestCount++;\r\nreturn internalRequestCount;'"
     * @generated
     */
    int getRequestCount();

    /**
     * Returns the value of the '<em><b>Some Books</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.incquery.examples.eiqlibrary.Book}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Some Books</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Some Books</em>' reference list.
     * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage#getLibrary_SomeBooks()
     * @model transient="true" changeable="false" volatile="true" derived="true"
     *        annotation="http://www.eclipse.org/emf/2002/GenModel get='int count = internalRequestCount;\r\njava.util.List<Book> someBooks = new java.util.ArrayList<Book>();\r\n\r\nBook[] books = getBooks().toArray(new Book[0]);\r\nfor (int i = 0; i < books.length; i++) {\r\n    Book book = books[i];\r\n    if(i%count == 0) {\r\n        someBooks.add(book);\r\n    }\r\n}\r\nreturn org.eclipse.emf.common.util.ECollections.asEList(someBooks);'"
     * @generated
     */
    EList<Book> getSomeBooks();

} // Library
