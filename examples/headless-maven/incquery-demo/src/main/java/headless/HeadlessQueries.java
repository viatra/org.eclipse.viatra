package headless;

import headless.ClassesInPackageHierarchyMatcher;
import headless.ClassesInPackageMatcher;
import headless.EClassMatcher;
import headless.EClassNamesKeywordMatcher;
import headless.EClassNamesMatcher;
import headless.EObjectMatcher;
import headless.EPackageMatcher;
import headless.SubPackageMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A pattern group formed of all patterns defined in headlessQueries.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file headlessQueries.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package headless, the group contains the definition of the following patterns: <ul>
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
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static HeadlessQueries instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new HeadlessQueries();
    }
    return INSTANCE;
    
  }
  
  private static HeadlessQueries INSTANCE;
  
  private HeadlessQueries() throws IncQueryException {
    querySpecifications.add(EClassNamesKeywordMatcher.querySpecification());
    querySpecifications.add(EObjectMatcher.querySpecification());
    querySpecifications.add(SubPackageMatcher.querySpecification());
    querySpecifications.add(ClassesInPackageMatcher.querySpecification());
    querySpecifications.add(EClassNamesMatcher.querySpecification());
    querySpecifications.add(ClassesInPackageHierarchyMatcher.querySpecification());
    querySpecifications.add(EPackageMatcher.querySpecification());
    querySpecifications.add(EClassMatcher.querySpecification());
    
  }
}
