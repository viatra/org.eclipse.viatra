package org.eclipse.viatra.query.application.queries;

import org.eclipse.viatra.query.application.queries.ClassesInPackageHierarchyMatcher;
import org.eclipse.viatra.query.application.queries.ClassesInPackageMatcher;
import org.eclipse.viatra.query.application.queries.EClassMatcher;
import org.eclipse.viatra.query.application.queries.EClassNamesKeywordMatcher;
import org.eclipse.viatra.query.application.queries.EClassNamesMatcher;
import org.eclipse.viatra.query.application.queries.EObjectMatcher;
import org.eclipse.viatra.query.application.queries.EPackageMatcher;
import org.eclipse.viatra.query.application.queries.SubPackageMatcher;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageHierarchyQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.EClassNamesKeywordQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.EClassNamesQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.EClassQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.EObjectQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.EPackageQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.SubPackageQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

/**
 * A pattern group formed of all patterns defined in headlessQueries.vql.
 * 
 * <p>Use the static instance as any {@link org.eclipse.viatra.query.runtime.api.IPatternGroup}, to conveniently prepare
 * a VIATRA Query engine for matching all patterns originally defined in file headlessQueries.vql,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.viatra.query.application.queries, the group contains the definition of the following patterns: <ul>
 * <li>eClassNames</li>
 * <li>eClassNamesKeyword</li>
 * <li>eObject</li>
 * <li>classesInPackage</li>
 * <li>subPackage</li>
 * <li>classesInPackageHierarchy</li>
 * <li>ePackage</li>
 * <li>eClass</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class HeadlessQueries extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws ViatraQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static HeadlessQueries instance() throws ViatraQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new HeadlessQueries();
    }
    return INSTANCE;
  }
  
  private static HeadlessQueries INSTANCE;
  
  private HeadlessQueries() throws ViatraQueryException {
    querySpecifications.add(EClassNamesQuerySpecification.instance());
    querySpecifications.add(EClassNamesKeywordQuerySpecification.instance());
    querySpecifications.add(EObjectQuerySpecification.instance());
    querySpecifications.add(ClassesInPackageQuerySpecification.instance());
    querySpecifications.add(SubPackageQuerySpecification.instance());
    querySpecifications.add(ClassesInPackageHierarchyQuerySpecification.instance());
    querySpecifications.add(EPackageQuerySpecification.instance());
    querySpecifications.add(EClassQuerySpecification.instance());
  }
  
  public EClassNamesQuerySpecification getEClassNames() throws ViatraQueryException {
    return EClassNamesQuerySpecification.instance();
  }
  
  public EClassNamesMatcher getEClassNames(final ViatraQueryEngine engine) throws ViatraQueryException {
    return EClassNamesMatcher.on(engine);
  }
  
  public EClassNamesKeywordQuerySpecification getEClassNamesKeyword() throws ViatraQueryException {
    return EClassNamesKeywordQuerySpecification.instance();
  }
  
  public EClassNamesKeywordMatcher getEClassNamesKeyword(final ViatraQueryEngine engine) throws ViatraQueryException {
    return EClassNamesKeywordMatcher.on(engine);
  }
  
  public EObjectQuerySpecification getEObject() throws ViatraQueryException {
    return EObjectQuerySpecification.instance();
  }
  
  public EObjectMatcher getEObject(final ViatraQueryEngine engine) throws ViatraQueryException {
    return EObjectMatcher.on(engine);
  }
  
  public ClassesInPackageQuerySpecification getClassesInPackage() throws ViatraQueryException {
    return ClassesInPackageQuerySpecification.instance();
  }
  
  public ClassesInPackageMatcher getClassesInPackage(final ViatraQueryEngine engine) throws ViatraQueryException {
    return ClassesInPackageMatcher.on(engine);
  }
  
  public SubPackageQuerySpecification getSubPackage() throws ViatraQueryException {
    return SubPackageQuerySpecification.instance();
  }
  
  public SubPackageMatcher getSubPackage(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SubPackageMatcher.on(engine);
  }
  
  public ClassesInPackageHierarchyQuerySpecification getClassesInPackageHierarchy() throws ViatraQueryException {
    return ClassesInPackageHierarchyQuerySpecification.instance();
  }
  
  public ClassesInPackageHierarchyMatcher getClassesInPackageHierarchy(final ViatraQueryEngine engine) throws ViatraQueryException {
    return ClassesInPackageHierarchyMatcher.on(engine);
  }
  
  public EPackageQuerySpecification getEPackage() throws ViatraQueryException {
    return EPackageQuerySpecification.instance();
  }
  
  public EPackageMatcher getEPackage(final ViatraQueryEngine engine) throws ViatraQueryException {
    return EPackageMatcher.on(engine);
  }
  
  public EClassQuerySpecification getEClass() throws ViatraQueryException {
    return EClassQuerySpecification.instance();
  }
  
  public EClassMatcher getEClass(final ViatraQueryEngine engine) throws ViatraQueryException {
    return EClassMatcher.on(engine);
  }
}
