/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt.util;

import org.eclipse.jdt.core.IJavaElementDelta;

public enum ChangeFlag {
    /**
     * Change flag indicating that a classpath entry corresponding to the element has been added to the project's classpath.
     * This flag is only valid if the element is an IPackageFragmentRoot.
     * 
     * @see IJavaElementDelta#F_ADDED_TO_CLASSPATH
     */
    ADDED_TO_CLASSPATH			(IJavaElementDelta.F_ADDED_TO_CLASSPATH),
    /**
     * Change flag indicating that the annotations of the element have changed.
     * Use #getAnnotationDeltas() to get the added/removed/changed annotations.
     * This flag is only valid if the element is an IAnnotatable.
     * 
     * @see IJavaElementDelta#F_ANNOTATIONS
     */
    ANNOTATIONS					(IJavaElementDelta.F_ANNOTATIONS),
    /**
     * Change flag indicating that the element's archive content on the classpath has changed.
     * This flag is only valid if the element is an IPackageFragmentRoot
     * which is an archive.
     * 
     * @see IJavaElementDelta#F_ARCHIVE_CONTENT_CHANGED
     */
    ARCHIVE_CONTENT_CHANGED		(IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED),
    /**
     * Change flag indicating that a reconcile operation has affected the compilation unit AST created in a
     * previous reconcile operation. Use #getCompilationUnitAST() to retrieve the AST (if any is available).
     * This flag is only valid if the element is an ICompilationUnit in working copy mode.
     * 
     * @see IJavaElementDelta#F_AST_AFFECTED
     */
    AST_AFFECTED				(IJavaElementDelta.F_AST_AFFECTED),
    /**
     * Change flag indicating that the categories of the element have changed.
     * This flag is only valid if the element is an IMember.
     * 
     * @see IJavaElementDelta#F_CATEGORIES
     */
    CATEGORIES					(IJavaElementDelta.F_CATEGORIES),
    /**
     * Change flag indicating that there are changes to the children of the element.
     * This flag is only valid if the element is an IParent.
     * 
     * @see IJavaElementDelta#F_CHILDREN
     */
    CHILDREN					(IJavaElementDelta.F_CHILDREN),
    /**
     * Change flag indicating that the IJavaProject#getRawClasspath() raw classpath
     * (or the IJavaProject#getOutputLocation() output folder) of a project has changed.
     * This flag is only valid if the element is an IJavaProject.
     * Also see #F_RESOLVED_CLASSPATH_CHANGED, which indicates that there is a
     * change to the IJavaProject#getResolvedClasspath(boolean) resolved class path.
     * The resolved classpath can change without the raw classpath changing (e.g.
     * if a container resolves to a different set of classpath entries).
     * And conversely, it is possible to construct a case where the raw classpath
     * can change without the resolved classpath changing.
     * 
     * @see IJavaElementDelta#F_CLASSPATH_CHANGED
     */
    CLASSPATH_CHANGED			(IJavaElementDelta.F_CLASSPATH_CHANGED),
    /**
     * Change flag indicating that the underlying org.eclipse.core.resources.IProject has been
     * closed. This flag is only valid if the element is an IJavaProject.
     * 
     * @see IJavaElementDelta#F_CLOSED
     */
    CLOSED						(IJavaElementDelta.F_CLOSED),
    /**
     * Change flag indicating that the content of the element has changed.
     * This flag is only valid for elements which correspond to files.
     * 
     * @see IJavaElementDelta#F_CONTENT
     */
    CONTENT						(IJavaElementDelta.F_CONTENT),
    /**
     * Change flag indicating that this is a fine-grained delta, that is, an analysis down
     * to the members level was done to determine if there were structural changes to
     * members.
     * <p>
     * Clients can use this flag to find out if a compilation unit
     * that have a #F_CONTENT change should assume that there are
     * no finer grained changes (#F_FINE_GRAINED is set) or if
     * finer grained changes were not considered (#F_FINE_GRAINED
     * is not set).
     * 
     * @see IJavaElementDelta#F_FINE_GRAINED
     */
    FINE_GRAINED				(IJavaElementDelta.F_FINE_GRAINED),
    /**
     * Change flag indicating that the modifiers of the element have changed.
     * This flag is only valid if the element is an IMember.
     * 
     * @see IJavaElementDelta#F_MODIFIERS
     */
    MODIFIERS					(IJavaElementDelta.F_MODIFIERS),
    /**
     * Change flag indicating that the element was moved from another location.
     * The location of the old element can be retrieved using #getMovedFromElement.
     * 
     * @see IJavaElementDelta#F_MOVED_FROM
     */
    MOVED_FROM					(IJavaElementDelta.F_MOVED_FROM),
    /**
     * Change flag indicating that the element was moved to another location.
     * The location of the new element can be retrieved using #getMovedToElement.
     * 
     * @see IJavaElementDelta#F_MOVED_TO
     */
    MOVED_TO					(IJavaElementDelta.F_MOVED_TO),
    /**
     * Change flag indicating that the underlying org.eclipse.core.resources.IProject has been
     * opened. This flag is only valid if the element is an IJavaProject.
     * 
     * @see IJavaElementDelta#F_OPENED
     */
    OPENED						(IJavaElementDelta.F_OPENED),
    /**
     * Change flag indicating that the resource of a primary compilation unit has changed.
     * This flag is only valid if the element is a primary ICompilationUnit.
     * 
     * @see IJavaElementDelta#F_PRIMARY_RESOURCE
     */
    PRIMARY_RESOURCE			(IJavaElementDelta.F_PRIMARY_RESOURCE),
    /**
     * Change flag indicating that a compilation unit has become a primary working copy, or that a
     * primary working copy has reverted to a compilation unit.
     * This flag is only valid if the element is an ICompilationUnit.
     * 
     * @see IJavaElementDelta#F_PRIMARY_WORKING_COPY
     */
    PRIMARY_WORKING_COPY		(IJavaElementDelta.F_PRIMARY_WORKING_COPY),
    /**
     * Change flag indicating that a classpath entry corresponding to the element has been removed from the project's
     * classpath. This flag is only valid if the element is an IPackageFragmentRoot.
     * 
     * @see IJavaElementDelta#F_REMOVED_FROM_CLASSPATH
     */
    REMOVED_FROM_CLASSPATH		(IJavaElementDelta.F_REMOVED_FROM_CLASSPATH),
    /**
     * Change flag indicating that the element has changed position relatively to its siblings.
     * If the element is an IPackageFragmentRoot,  a classpath entry corresponding
     * to the element has changed position in the project's classpath.
     * 
     * @see IJavaElementDelta#F_REORDER
     */
    REORDER						(IJavaElementDelta.F_REORDER),
    /**
     * Change flag indicating that the IJavaProject#getResolvedClasspath(boolean)
     * resolved classpath of a project has changed.
     * This flag is only valid if the element is an IJavaProject.
     * Also see #F_CLASSPATH_CHANGED, which indicates that there is a
     * change to the IJavaProject#getRawClasspath() raw class path.
     * The resolved classpath can change without the raw classpath changing (e.g.
     * if a container resolves to a different set of classpath entries).
     * And conversely, it is possible to construct a case where the raw classpath
     * can change without the resolved classpath changing.
     * 
     * @see IJavaElementDelta#F_RESOLVED_CLASSPATH_CHANGED
     */
    RESOLVED_CLASSPATH_CHANGED	(IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED),
    /**
     * Change flag indicating that the source attachment path or the source attachment root path of a classpath entry
     * corresponding to the element was added. This flag is only valid if the element is an
     * IPackageFragmentRoot.
     * 
     * @see IJavaElementDelta#F_SOURCEATTACHED
     */
    SOURCEATTACHED				(IJavaElementDelta.F_SOURCEATTACHED),
    /**
     * Change flag indicating that the source attachment path or the source attachment root path of a classpath entry
     * corresponding to the element was removed. This flag is only valid if the element is an
     * IPackageFragmentRoot.
     * 
     * @see IJavaElementDelta#F_SOURCEDETACHED
     */
    SOURCEDETACHED				(IJavaElementDelta.F_SOURCEDETACHED),
    /**
     * Change flag indicating that one of the supertypes of an IType
     * has changed.
     * 
     * @see IJavaElementDelta#F_SUPER_TYPES
     */
    SUPER_TYPES					(IJavaElementDelta.F_SUPER_TYPES);
    
    private final int value;
    ChangeFlag(int value) {
        this.value = value;
    }
    
    public final int getValue() {
        return this.value;
    }
}
